package basquete.assis.features.treinador.models;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class TreinadorFormDTO {
    @NotBlank private String nome;
    @NotBlank private String email;
    @NotBlank private String senha;
    @NotBlank private String cref;
    @NotBlank private String especialidade;
}