package basquete.assis.shared.entities;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;


@Entity
@Table(name = "ficha_tecnica")
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
public class FichaTecnica {

    @Id
    private Long id;

    @MapsId
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "atleta_id")
    private Atleta atleta;

    private Double altura;
    private Double peso;
    private Integer experienciaAnos;
    private String historicoDoencas;
    private String medicamentosUsoContinuo;
    private LocalDateTime dataAtualizacao;
}