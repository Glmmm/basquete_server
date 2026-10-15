package basquete.assis.shared.entities;

import basquete.assis.features.auth.models.RegistroTreinadorForm;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "treinadores")
@PrimaryKeyJoinColumn(name = "id")
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@Builder
public class Treinador extends Usuario {

    @Column(nullable = false, unique = true)
    private String cref;

    @Column(nullable = false)
    private String especialidade;

    public Treinador(Usuario usuario, RegistroTreinadorForm form) {
    }
}