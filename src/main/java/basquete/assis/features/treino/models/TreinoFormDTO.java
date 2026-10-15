package basquete.assis.features.treino.models;


import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import java.time.LocalDateTime;

@Getter @Setter
public class TreinoFormDTO {
    @NotBlank private String titulo;
    private String descricao;
    @NotNull private LocalDateTime dataHoraInicio;
    @NotNull private LocalDateTime dataHoraFim;
    @NotBlank private String local;
    @NotNull private Long tipoAgendamentoId;
    @NotBlank private String tipoTreino;
    @NotNull private Long categoriaBaseId;
}