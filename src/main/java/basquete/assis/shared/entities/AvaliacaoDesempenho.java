package basquete.assis.shared.entities;

import basquete.assis.shared.enums.StatusAvaliacao;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "avaliacao_desempenho")
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
public class AvaliacaoDesempenho {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "treino_id", nullable = false)
    private Treino treino;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "atleta_id", nullable = false)
    private Atleta atleta;

    private Double notaFisica;
    private Double notaTatica;
    private Double notaTecnica;

    private String comentarios;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private StatusAvaliacao status;
}