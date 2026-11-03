package basquete.assis.shared.repositories;

import basquete.assis.shared.entities.Atleta;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AtletaRepository extends JpaRepository<Atleta, Long> {

    @Query("SELECT c.nome, COUNT(a) FROM Atleta a JOIN a.categoriaBase c GROUP BY c.nome")
    List<Object[]> contarAtletasPorCategoria();
}
