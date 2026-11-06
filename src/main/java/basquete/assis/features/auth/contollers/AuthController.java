package basquete.assis.features.auth.contollers;

import basquete.assis.core.TokenService;
import basquete.assis.features.atleta.AtletaStore;
import basquete.assis.shared.entities.Atleta;
import basquete.assis.shared.entities.CategoriaBase;
import basquete.assis.shared.entities.Usuario;
import basquete.assis.shared.enums.Role;
import basquete.assis.shared.repositories.AtletaRepository;
import basquete.assis.shared.repositories.CategoriaBaseRepository;
import basquete.assis.shared.repositories.UsuarioRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

@RequestMapping("auth")
@RestController
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final TokenService tokenService;
    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;
    private final AtletaRepository atletaRepository;
    private final CategoriaBaseRepository categoriaBaseRepository;
    private final AtletaStore atletaStore;

    public AuthController(AuthenticationManager authenticationManager, TokenService tokenService,
                          UsuarioRepository usuarioRepository, PasswordEncoder passwordEncoder,
                          AtletaRepository atletaRepository, CategoriaBaseRepository categoriaBaseRepository,
                          AtletaStore atletaStore) {
        this.authenticationManager = authenticationManager;
        this.tokenService = tokenService;
        this.usuarioRepository = usuarioRepository;
        this.passwordEncoder = passwordEncoder;
        this.atletaRepository = atletaRepository;
        this.categoriaBaseRepository = categoriaBaseRepository;
        this.atletaStore = atletaStore;
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody Map<String, String> body) {
        String login = body.getOrDefault("login", body.get("email"));
        String password = body.get("password");
        if (login == null || password == null) {
            return ResponseEntity.badRequest().body(Map.of("message", "Credenciais inválidas"));
        }
        try {
            var credentials = new UsernamePasswordAuthenticationToken(login, password);
            var auth = authenticationManager.authenticate(credentials);
            var usuario = (Usuario) auth.getPrincipal();
            var token = tokenService.generateToken(usuario);
            return ResponseEntity.ok(Map.of("token", token));
        } catch (Exception e) {
            return ResponseEntity.status(401).body(Map.of("message", "E-mail ou senha inválidos"));
        }
    }

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody Map<String, Object> body) {
        String email = str(body.get("email"));
        String password = str(body.get("password"));
        String roleStr = str(body.get("role"));
        if (roleStr == null) roleStr = str(body.get("perfil"));
        if (roleStr == null) roleStr = "ROLE_ATLETA";
        String nome = str(body.get("nome"));
        if (nome == null || nome.isBlank()) nome = email;
        String cpf = str(body.get("cpf"));
        String telefone = str(body.get("telefone"));
        LocalDate dataNascimento = parseDate(body.get("dataNascimento"));

        if (email == null || password == null) {
            return ResponseEntity.badRequest().body(Map.of("message", "Dados inválidos"));
        }
        if (usuarioRepository.findByEmail(email) != null) {
            return ResponseEntity.status(409).body(Map.of("message", "E-mail já cadastrado"));
        }
        Role role;
        try {
            String normalized = roleStr.startsWith("ROLE_") ? roleStr.substring(5) : roleStr;
            role = Role.valueOf(normalized);
        } catch (Exception e) {
            role = Role.ATLETA;
        }
        Usuario usuario;
        if (role == Role.ATLETA) {
            Atleta a = new Atleta();
            a.setEmail(email);
            a.setNome(nome);
            a.setCpf(cpf);
            a.setTelefone(telefone);
            a.setDataNascimento(dataNascimento);
            a.setSenhaHash(passwordEncoder.encode(password));
            a.setRole(role);
            a.setCamisa(parseNumero(body.get("numero")));
            a.setCategoriaBase(obterOuCriarCategoriaBase());
            usuario = atletaRepository.save(a);
            popularStore(a);
        } else {
            Usuario u = new Usuario();
            u.setEmail(email);
            u.setNome(nome);
            u.setCpf(cpf);
            u.setTelefone(telefone);
            u.setDataNascimento(dataNascimento);
            u.setSenhaHash(passwordEncoder.encode(password));
            u.setRole(role);
            usuario = usuarioRepository.save(u);
        }
        var token = tokenService.generateToken(usuario);
        return ResponseEntity.ok(Map.of("token", token));
    }

    private void popularStore(Atleta a) {
        if (a == null || a.getId() == null || atletaStore == null) return;
        try {
            String id = a.getId().toString();
            Map<String, Object> m = new HashMap<>(atletaStore.get(id) != null ? atletaStore.get(id) : Map.of());
            m.put("id", id);
            m.put("nome", a.getNome());
            m.put("email", a.getEmail());
            m.putIfAbsent("cpf", a.getCpf() != null ? a.getCpf() : "");
            m.putIfAbsent("dataNascimento", a.getDataNascimento() != null ? a.getDataNascimento().toString() : null);
            m.putIfAbsent("telefone", a.getTelefone() != null ? a.getTelefone() : "");
            m.putIfAbsent("numero", a.getCamisa() != null ? a.getCamisa() : 0);
            m.putIfAbsent("posicao", "ALA");
            m.putIfAbsent("ativo", true);
            atletaStore.put(id, m);
        } catch (Exception ignored) {}
    }

    private CategoriaBase obterOuCriarCategoriaBase() {
        return categoriaBaseRepository.findAll().stream().findFirst().orElseGet(() -> {
            CategoriaBase c = new CategoriaBase();
            c.setNome("Geral");
            try { return categoriaBaseRepository.save(c); } catch (Exception e) { return null; }
        });
    }

    private Long parseNumero(Object v) {
        try { if (v != null) return Long.parseLong(v.toString()); } catch (Exception ignored) {}
        return 0L;
    }

    private LocalDate parseDate(Object v) {
        if (v == null) return null;
        try { return LocalDate.parse(v.toString()); } catch (Exception e) { return null; }
    }

    private String str(Object v) {
        return v != null ? v.toString() : null;
    }
}
