package basquete.assis.features.fichatecnica.models;


import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class FichaTecnicaFormDTO {
    @NotNull private Long atletaId;
    private Double altura;
    private Double peso;
    private Integer experienciaAnos;
    private String historicoDoencas;
    private String medicamentosUsoContinuo;
}