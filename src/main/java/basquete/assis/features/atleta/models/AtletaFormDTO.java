package basquete.assis.features.atleta.models;


import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import java.time.LocalDate;

    @Getter @Setter
    public class AtletaFormDTO {
        @NotBlank private String nome;
        @NotBlank private String email;
        @NotBlank private String senha;
        @NotNull private LocalDate dataNascimento;
        @NotNull private Long categoriaBaseId;
    }