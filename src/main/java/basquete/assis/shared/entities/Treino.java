package basquete.assis.shared.entities;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "treino")
@Getter @Setter
@PrimaryKeyJoinColumn(name = "id")
@NoArgsConstructor @AllArgsConstructor
@PrimaryKeyJoinColumn(name = "agendamento_id")
public class Treino extends Agendamento {
    @Column(nullable = false)
    private String tipoTreino;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "categoria_base_id")
    private CategoriaBase categoriaBase;
}