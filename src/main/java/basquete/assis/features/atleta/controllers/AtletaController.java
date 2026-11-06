package basquete.assis.features.atleta.controllers;

import basquete.assis.features.atleta.AtletaStore;
import basquete.assis.shared.entities.Atleta;
import basquete.assis.shared.entities.CategoriaBase;
import basquete.assis.shared.enums.Role;
import basquete.assis.shared.repositories.AtletaRepository;
import basquete.assis.shared.repositories.CategoriaBaseRepository;
import basquete.assis.shared.repositories.UsuarioRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.*;

@RestController
@RequestMapping("/atletas")
public class AtletaController {

    private final AtletaStore store;
    private final AtletaRepository atletaRepository;
    private final UsuarioRepository usuarioRepository;
    private final CategoriaBaseRepository categoriaBaseRepository;
    private final PasswordEncoder passwordEncoder;

    public AtletaController(AtletaStore store,
                            AtletaRepository atletaRepository,
                            UsuarioRepository usuarioRepository,
                            CategoriaBaseRepository categoriaBaseRepository,
                            PasswordEncoder passwordEncoder) {
        this.store = store;
        this.atletaRepository = atletaRepository;
        this.usuarioRepository = usuarioRepository;
        this.categoriaBaseRepository = categoriaBaseRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @PostMapping
    public ResponseEntity<Map<String, Object>> criar(@RequestBody Map<String, Object> body) {
        String id = body.get("id") != null ? body.get("id").toString() : null;
        body.putIfAbsent("ativo", true);
        // best-effort: persistir no domínio (JOINED Atleta extends Usuario)
        try {
            String email = body.get("email") != null ? body.get("email").toString() : null;
            if (email != null) {
                var existente = usuarioRepository.findByEmail(email);
                if (existente instanceof Atleta) {
                    id = existente.getId() != null ? existente.getId().toString() : id;
                } else if (existente == null) {
                    Atleta a = new Atleta();
                    a.setNome(String.valueOf(body.getOrDefault("nome", "")));
                    a.setEmail(email);
                    String senha = body.get("senha") != null ? body.get("senha").toString()
                            : (body.get("password") != null ? body.get("password").toString() : UUID.randomUUID().toString());
                    a.setSenhaHash(passwordEncoder.encode(senha));
                    a.setRole(Role.ATLETA);
                    try {
                        a.setDataNascimento(body.get("dataNascimento") != null ? LocalDate.parse(body.get("dataNascimento").toString()) : LocalDate.of(2000, 1, 1));
                    } catch (Exception e) {
                        a.setDataNascimento(LocalDate.of(2000, 1, 1));
                    }
                    try {
                        a.setCamisa(body.get("numero") != null ? Long.parseLong(body.get("numero").toString()) : 0L);
                    } catch (Exception e) {
                        a.setCamisa(0L);
                    }
                    CategoriaBase cat = categoriaBaseRepository.findAll().stream().findFirst().orElse(null);
                    a.setCategoriaBase(cat);
                    if (cat != null) {
                        a = atletaRepository.save(a);
                        id = a.getId() != null ? a.getId().toString() : id;
                    }
                } else if (existente.getId() != null) {
                    // usuário existe mas não é Atleta de domínio: mantém id para consistência
                    id = existente.getId().toString();
                }
            }
        } catch (Exception ignored) {}
        if (id == null) id = UUID.randomUUID().toString();
        body.put("id", id);
        store.put(id, new HashMap<>(body));
        return ResponseEntity.status(HttpStatus.CREATED).body(body);
    }

    @GetMapping
    public ResponseEntity<List<Map<String, Object>>> listar() {
        return ResponseEntity.ok(new ArrayList<>(store.values()));
    }

    @GetMapping("/{id}")
    public ResponseEntity<Map<String, Object>> buscar(@PathVariable("id") String id) {
        Map<String, Object> a = store.get(id);
        if (a == null) return ResponseEntity.notFound().build();
        return ResponseEntity.ok(a);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Map<String, Object>> atualizar(@PathVariable("id") String id, @RequestBody Map<String, Object> body) {
        body.put("id", id);
        body.putIfAbsent("ativo", store.get(id) != null ? store.get(id).getOrDefault("ativo", true) : true);
        store.put(id, new HashMap<>(body));
        // best-effort update em domínio se id numérico
        try {
            Long lid = Long.parseLong(id);
            atletaRepository.findById(lid).ifPresent(a -> {
                if (body.get("nome") != null) a.setNome(body.get("nome").toString());
                if (body.get("numero") != null) { try { a.setCamisa(Long.parseLong(body.get("numero").toString())); } catch (Exception ignored) {} }
                if (body.get("dataNascimento") != null) { try { a.setDataNascimento(LocalDate.parse(body.get("dataNascimento").toString())); } catch (Exception ignored) {} }
                atletaRepository.save(a);
            });
        } catch (Exception ignored) {}
        return ResponseEntity.ok(body);
    }

    @PatchMapping("/{id}/status")
    public ResponseEntity<Map<String, Object>> alterarStatus(@PathVariable("id") String id, @RequestBody Map<String, Object> body) {
        Map<String, Object> a = store.get(id);
        if (a == null) {
            a = new HashMap<>();
            a.put("id", id);
        }
        if (body.containsKey("ativo")) a.put("ativo", body.get("ativo"));
        store.put(id, a);
        return ResponseEntity.ok(a);
    }
}
