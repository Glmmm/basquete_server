package basquete.assis.shared.entities;


import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "categoria_base")
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@Builder
public class CategoriaBase {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String nome;
}