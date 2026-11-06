package basquete.assis.features.auth.contollers;

import basquete.assis.core.TokenService;
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

    public AuthController(AuthenticationManager authenticationManager, TokenService tokenService,
                          UsuarioRepository usuarioRepository, PasswordEncoder passwordEncoder,
                          AtletaRepository atletaRepository, CategoriaBaseRepository categoriaBaseRepository) {
        this.authenticationManager = authenticationManager;
        this.tokenService = tokenService;
        this.usuarioRepository = usuarioRepository;
        this.passwordEncoder = passwordEncoder;
        this.atletaRepository = atletaRepository;
        this.categoriaBaseRepository = categoriaBaseRepository;
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
        String email = body.get("email") != null ? body.get("email").toString() : null;
        String password = body.get("password") != null ? body.get("password").toString() : null;
        String roleStr = body.get("role") != null ? body.get("role").toString()
                : (body.get("perfil") != null ? body.get("perfil").toString() : "ROLE_ATLETA");
        String nome = body.get("nome") != null ? body.get("nome").toString() : email;

        if (email == null || password == null) {
            return ResponseEntity.badRequest().body(Map.of("message", "Dados inválidos"));
        }
        if (usuarioRepository.findByEmail(email) != null) {
            return ResponseEntity.badRequest().body(Map.of("message", "E-mail já cadastrado"));
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
            a.setSenhaHash(passwordEncoder.encode(password));
            a.setRole(role);
            try {
                a.setDataNascimento(body.get("dataNascimento") != null ? LocalDate.parse(body.get("dataNascimento").toString()) : LocalDate.of(2000, 1, 1));
            } catch (Exception e) { a.setDataNascimento(LocalDate.of(2000, 1, 1)); }
            try {
                a.setCamisa(body.get("numero") != null ? Long.parseLong(body.get("numero").toString()) : 0L);
            } catch (Exception e) { a.setCamisa(0L); }
            CategoriaBase cat = categoriaBaseRepository.findAll().stream().findFirst().orElse(null);
            a.setCategoriaBase(cat);
            usuario = (cat != null) ? atletaRepository.save(a) : a;
            if (cat == null) usuario = usuarioRepository.save(a);
        } else {
            Usuario u = new Usuario();
            u.setEmail(email);
            u.setNome(nome);
            u.setSenhaHash(passwordEncoder.encode(password));
            u.setRole(role);
            usuario = usuarioRepository.save(u);
        }
        var token = tokenService.generateToken(usuario);
        return ResponseEntity.ok(Map.of("token", token));
    }
}
