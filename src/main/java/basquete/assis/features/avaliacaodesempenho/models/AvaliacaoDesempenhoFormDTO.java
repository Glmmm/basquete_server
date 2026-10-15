package basquete.assis.features.avaliacaodesempenho.models;


import basquete.assis.shared.enums.StatusAvaliacao;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class AvaliacaoDesempenhoFormDTO {
    @NotNull private Long atletaId;
    private Long treinoId;
    private Double notaFisica;
    private Double notaTatica;
    private Double notaTecnica;
    private String comentarios;
    @NotNull private StatusAvaliacao status;
}