package basquete.assis.features.historicolesao.models;


import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import java.time.LocalDate;

@Getter @Setter
public class HistoricoLesaoFormDTO {
    @NotNull private Long atletaId;
    @NotNull private LocalDate dataInicio;
    private LocalDate dataRetorno;
    @NotBlank private String descricaoLesao;
    private String procedimentosRealizados;
    private String medicamentos;
    private String periodoRepouso;
}