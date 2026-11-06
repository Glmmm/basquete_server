package basquete.assis.features.usuarios.controllers;

import basquete.assis.core.TokenService;
import basquete.assis.shared.entities.Usuario;
import basquete.assis.shared.repositories.UsuarioRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/usuarios")
public class UsuarioController {

    private final UsuarioRepository usuarioRepository;
    private final TokenService tokenService;

    public UsuarioController(UsuarioRepository usuarioRepository, TokenService tokenService) {
        this.usuarioRepository = usuarioRepository;
        this.tokenService = tokenService;
    }

    @GetMapping("/me")
    public ResponseEntity<Map<String, Object>> me() {
        Usuario usuario = getUsuarioAtual();
        if (usuario == null) {
            return ResponseEntity.status(401).build();
        }
        Map<String, Object> resp = new HashMap<>();
        resp.put("id", usuario.getId() != null ? usuario.getId().toString() : null);
        resp.put("nome", usuario.getNome());
        resp.put("email", usuario.getEmail());
        String role = usuario.getRole() != null ? "ROLE_" + usuario.getRole().name() : null;
        if (usuario.getRole() != null && usuario.getRole().name().startsWith("ROLE_")) role = usuario.getRole().name();
        resp.put("perfil", role);
        resp.put("fotoUrl", null);
        return ResponseEntity.ok(resp);
    }

    @PutMapping("/me")
    public ResponseEntity<Map<String, Object>> atualizar(@RequestBody Map<String, Object> body) {
        Usuario usuario = getUsuarioAtual();
        if (usuario == null) return ResponseEntity.status(401).build();
        if (body.get("nome") != null) usuario.setNome(body.get("nome").toString());
        if (body.get("email") != null) usuario.setEmail(body.get("email").toString());
        usuarioRepository.save(usuario);
        Map<String, Object> resp = new HashMap<>();
        resp.put("id", usuario.getId() != null ? usuario.getId().toString() : null);
        resp.put("nome", usuario.getNome());
        resp.put("email", usuario.getEmail());
        String role = usuario.getRole() != null ? "ROLE_" + usuario.getRole().name() : null;
        if (usuario.getRole() != null && usuario.getRole().name().startsWith("ROLE_")) role = usuario.getRole().name();
        resp.put("perfil", role);
        resp.put("fotoUrl", body.get("fotoUrl"));
        return ResponseEntity.ok(resp);
    }

    @PostMapping("/me/foto")
    public ResponseEntity<Map<String, Object>> enviarFoto() {
        return me();
    }

    @DeleteMapping("/me/foto")
    public ResponseEntity<Map<String, Object>> removerFoto() {
        return me();
    }

    @DeleteMapping("/me")
    public ResponseEntity<Void> excluir(@RequestBody Map<String, Object> body) {
        return ResponseEntity.noContent().build();
    }

    private Usuario getUsuarioAtual() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || auth.getName() == null) return null;
        return usuarioRepository.findByEmail(auth.getName());
    }
}
