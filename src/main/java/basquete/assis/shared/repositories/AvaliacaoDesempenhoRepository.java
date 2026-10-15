package basquete.assis.shared.repositories;

import basquete.assis.shared.entities.AvaliacaoDesempenho;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AvaliacaoDesempenhoRepository extends JpaRepository<AvaliacaoDesempenho, Long> {
    List<AvaliacaoDesempenho> findByAtletaId(Long atletaId);
}