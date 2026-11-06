package basquete.assis.core;

import basquete.assis.shared.entities.Usuario;
import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTCreationException;
import com.auth0.jwt.exceptions.JWTVerificationException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;

@Service
public class TokenService   {
    @Value("${api.security.token.secret}")
    private String secret;

    public String generateToken(Usuario user) {
        try {
            Algorithm algorithm = Algorithm.HMAC256(secret);
            String roleName = user.getRole() != null ? "ROLE_" + user.getRole().name() : null;
            if (user.getRole() != null && user.getRole().name().startsWith("ROLE_")) {
                roleName = user.getRole().name();
            }
            String sub = user.getId() != null ? user.getId().toString() : user.getEmail();
            return JWT.create()
                    .withIssuer("auth")
                    .withSubject(sub)
                    .withClaim("email", user.getEmail())
                    .withClaim("nome", user.getNome())
                    .withClaim("role", roleName)
                    .withArrayClaim("roles", roleName != null ? new String[]{roleName} : new String[]{})
                    .withExpiresAt(genExpirationDate())
                    .sign(algorithm);

        } catch (JWTCreationException exception) {
            throw new RuntimeException("Error while generating token", exception);
        }
    }
    public String validateToken(String token) {
        try {
            Algorithm algorithm = Algorithm.HMAC256(secret);
            return JWT.require(algorithm)
                    .withIssuer("auth")
                    .build()
                    .verify(token)
                    .getSubject();
        }catch (JWTVerificationException exception){
            return "";
        }
    }
    private Instant genExpirationDate() {
        return ZonedDateTime.now(ZoneId.of("America/Sao_Paulo"))
                .plusHours(4)
                .toInstant();
    }
}
