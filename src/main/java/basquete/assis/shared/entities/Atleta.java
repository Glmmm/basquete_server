package basquete.assis.shared.entities;


import basquete.assis.features.auth.models.RegistroAtletaForm;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.util.List;

@Entity
@Table(name = "atleta")
@PrimaryKeyJoinColumn(name = "id")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Atleta extends Usuario {

    @Column(nullable = false)
    private LocalDate dataNascimento;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "categoria_base_id", nullable = false)
    private CategoriaBase categoriaBase;

    @OneToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JoinColumn(name = "ficha_tecnica_id")
    private FichaTecnica fichaTecnica;

    @OneToMany(mappedBy = "atleta", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<HistoricoLesao> historicoLesoes;

    public Atleta(Usuario usuario, RegistroAtletaForm form) {
    }
}
