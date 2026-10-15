package basquete.assis.features.categoriabase.models;


import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class CategoriaBaseFormDTO {
    @NotBlank private String nome;
}
