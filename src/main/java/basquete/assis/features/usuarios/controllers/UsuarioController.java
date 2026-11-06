package basquete.assis.features.usuarios.controllers;

import basquete.assis.core.TokenService;
import basquete.assis.shared.entities.Atleta;
import basquete.assis.shared.entities.Usuario;
import basquete.assis.shared.repositories.AtletaRepository;
import basquete.assis.shared.repositories.AvaliacaoDesempenhoRepository;
import basquete.assis.shared.repositories.HistoricoLesaoRepository;
import basquete.assis.shared.repositories.PresencaRepository;
import basquete.assis.shared.repositories.UsuarioRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/usuarios")
public class UsuarioController {

    private final UsuarioRepository usuarioRepository;
    private final TokenService tokenService;
    private final PasswordEncoder passwordEncoder;
    private final PresencaRepository presencaRepository;
    private final AvaliacaoDesempenhoRepository avaliacaoDesempenhoRepository;
    private final AtletaRepository atletaRepository;
    private final HistoricoLesaoRepository historicoLesaoRepository;

    public UsuarioController(UsuarioRepository usuarioRepository, TokenService tokenService,
                             PasswordEncoder passwordEncoder, PresencaRepository presencaRepository,
                             AvaliacaoDesempenhoRepository avaliacaoDesempenhoRepository,
                             AtletaRepository atletaRepository, HistoricoLesaoRepository historicoLesaoRepository) {
        this.usuarioRepository = usuarioRepository;
        this.tokenService = tokenService;
        this.passwordEncoder = passwordEncoder;
        this.presencaRepository = presencaRepository;
        this.avaliacaoDesempenhoRepository = avaliacaoDesempenhoRepository;
        this.atletaRepository = atletaRepository;
        this.historicoLesaoRepository = historicoLesaoRepository;
    }

    @GetMapping("/me")
    public ResponseEntity<Map<String, Object>> me() {
        Usuario usuario = getUsuarioAtual();
        if (usuario == null) {
            return ResponseEntity.status(401).build();
        }
        return ResponseEntity.ok(toConta(usuario));
    }

    @PutMapping("/me")
    public ResponseEntity<Map<String, Object>> atualizar(@RequestBody Map<String, Object> body) {
        Usuario usuario = getUsuarioAtual();
        if (usuario == null) return ResponseEntity.status(401).build();
        if (body.get("nome") != null) usuario.setNome(body.get("nome").toString());
        if (body.get("email") != null) {
            String novoEmail = body.get("email").toString();
            Usuario existente = usuarioRepository.findByEmail(novoEmail);
            if (existente != null && !existente.getId().equals(usuario.getId())) {
                return ResponseEntity.status(409).body(Map.of("message", "E-mail já cadastrado"));
            }
            usuario.setEmail(novoEmail);
        }
        if (body.get("telefone") != null) usuario.setTelefone(body.get("telefone").toString());
        if (body.get("dataNascimento") != null) {
            try { usuario.setDataNascimento(LocalDate.parse(body.get("dataNascimento").toString())); } catch (Exception ignored) {}
        }
        if (body.containsKey("fotoUrl")) {
            Object f = body.get("fotoUrl");
            usuario.setFotoUrl(f != null ? f.toString() : null);
        }
        // cpf não é alterado pelo próprio usuário
        usuarioRepository.save(usuario);
        return ResponseEntity.ok(toConta(usuario));
    }

    @PostMapping("/me/foto")
    public ResponseEntity<Map<String, Object>> enviarFoto() {
        // upload real (multipart) pode ser implementado depois; preserva contrato e não quebra o client
        return me();
    }

    @DeleteMapping("/me/foto")
    public ResponseEntity<Map<String, Object>> removerFoto() {
        Usuario usuario = getUsuarioAtual();
        if (usuario == null) return ResponseEntity.status(401).build();
        usuario.setFotoUrl(null);
        usuarioRepository.save(usuario);
        return ResponseEntity.ok(toConta(usuario));
    }

    @DeleteMapping("/me")
    @Transactional
    public ResponseEntity<?> excluir(@RequestBody(required = false) Map<String, Object> body) {
        Usuario usuario = getUsuarioAtual();
        if (usuario == null) return ResponseEntity.status(401).build();
        String senha = body != null && body.get("password") != null ? body.get("password").toString() : null;
        if (senha == null || !passwordEncoder.matches(senha, usuario.getSenhaHash())) {
            return ResponseEntity.status(403).body(Map.of("message", "Senha incorreta"));
        }
        Long id = usuario.getId();
        try {
            // limpa dependências que podem bloquear a exclusão
            historicoLesaoRepository.findAll().stream()
                    .filter(h -> h.getRegistradoPor() != null && id.equals(h.getRegistradoPor().getId()))
                    .forEach(h -> h.setRegistradoPor(null));
            if (usuario instanceof Atleta || atletaRepository.findById(id).isPresent()) {
                presencaRepository.deleteAll(presencaRepository.findAll().stream()
                        .filter(p -> p.getAtleta() != null && id.equals(p.getAtleta().getId())).toList());
                avaliacaoDesempenhoRepository.deleteAll(avaliacaoDesempenhoRepository.findAll().stream()
                        .filter(a -> a.getAtleta() != null && id.equals(a.getAtleta().getId())).toList());
            }
            usuarioRepository.deleteById(id);
        } catch (Exception e) {
            try { usuarioRepository.deleteById(id); } catch (Exception ex) {
                return ResponseEntity.status(409).body(Map.of("message", "Não foi possível excluir a conta."));
            }
        }
        return ResponseEntity.noContent().build();
    }

    private Map<String, Object> toConta(Usuario usuario) {
        Map<String, Object> resp = new HashMap<>();
        resp.put("id", usuario.getId() != null ? usuario.getId().toString() : null);
        resp.put("nome", usuario.getNome());
        resp.put("email", usuario.getEmail());
        resp.put("cpf", usuario.getCpf() != null ? usuario.getCpf() : "");
        resp.put("dataNascimento", usuario.getDataNascimento() != null ? usuario.getDataNascimento().toString() : "");
        resp.put("telefone", usuario.getTelefone() != null ? usuario.getTelefone() : "");
        resp.put("fotoUrl", usuario.getFotoUrl());
        String role = usuario.getRole() != null ? "ROLE_" + usuario.getRole().name() : null;
        if (usuario.getRole() != null && usuario.getRole().name().startsWith("ROLE_")) role = usuario.getRole().name();
        resp.put("perfil", role);
        return resp;
    }

    private Usuario getUsuarioAtual() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null) return null;
        Object principal = auth.getPrincipal();
        if (principal instanceof Usuario u) {
            if (u.getId() != null) return usuarioRepository.findById(u.getId()).orElse(u);
            return u;
        }
        if (auth.getName() != null) return usuarioRepository.findByEmail(auth.getName());
        return null;
    }
}
