package basquete.assis.shared.entities;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDate;

@Entity
@Table(name = "tb_historico_lesoes")
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@Builder
public class HistoricoLesao {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "atleta_id", nullable = false)
    private Atleta atleta;

    @ManyToOne(optional = false)
    @JoinColumn(name = "registrado_por_usuario_id", nullable = false)
    private Usuario registradoPor; // Atleta ou Treinador

    @Column(columnDefinition = "TEXT", nullable = false)
    private String descricaoLesao;

    private String periodoRepouso;

    @Column(columnDefinition = "TEXT")
    private String medicamentos;

    @Column(columnDefinition = "TEXT")
    private String procedimentosRealizados;

    @Column(nullable = false)
    private LocalDate dataInicio;

    private LocalDate dataRetorno;
}