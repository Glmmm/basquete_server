package basquete.assis.shared.entities;

import basquete.assis.features.auth.models.RegistroAdministradorForm;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "admin")
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@Builder
public class Administrador {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(optional = false)
    @JoinColumn(name = "usuario_id", referencedColumnName = "id", nullable = false)
    private Usuario usuario;


    public Administrador(Usuario usuario, RegistroAdministradorForm form) {
    }
}