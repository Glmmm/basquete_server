package basquete.assis.features.auth.contollers;

import basquete.assis.core.TokenService;
import basquete.assis.features.auth.models.LoginForm;
import basquete.assis.features.auth.models.LoginResponse;
import basquete.assis.features.auth.models.RegistroForm;
import basquete.assis.features.auth.services.forms.RegistroFormHandler;
import basquete.assis.shared.entities.Usuario;
import basquete.assis.shared.enums.Role;
import basquete.assis.shared.repositories.UsuarioRepository;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;


@RequestMapping("auth")
@RestController
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final TokenService tokenService;
    private final UsuarioRepository usuarioRepository;
    private final List<RegistroFormHandler> formHandlers;

    public AuthController(AuthenticationManager authenticationManager, TokenService tokenService, UsuarioRepository usuarioRepository, List<RegistroFormHandler> formHandlers) {
        this.authenticationManager = authenticationManager;
        this.tokenService = tokenService;
        this.usuarioRepository = usuarioRepository;
        this.formHandlers = formHandlers;
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@RequestBody @Valid LoginForm form) {
        var credentials = new UsernamePasswordAuthenticationToken(form.login(), form.password());
        var auth = authenticationManager.authenticate(credentials);

        var usuario = (Usuario) auth.getPrincipal();
        var token = tokenService.generateToken(usuario);

        return ResponseEntity.ok(new LoginResponse(token));
    }

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody @Valid RegistroForm form) {
        if (usuarioRepository.findByEmail(form.email()) != null) {
            return ResponseEntity.badRequest().body("E-mail já cadastrado.");
        }

        Role role = Role.valueOf(form.role().toString());

        String encodedPass = new BCryptPasswordEncoder().encode(form.password());

        Usuario usuario = new Usuario(form.email(), encodedPass, role);
        Usuario usuarioSalvo = usuarioRepository.save(usuario);

        formHandlers.stream()
                .filter(handler -> handler.supports(form))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Tipo de formulário não suportado"))
                .handle(usuarioSalvo, form);

        return ResponseEntity.ok(usuarioSalvo);
    }

}
