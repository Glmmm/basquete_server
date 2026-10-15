package basquete.assis.shared.repositories;

import basquete.assis.shared.entities.HistoricoLesao;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface HistoricoLesaoRepository extends JpaRepository<HistoricoLesao, Long> {
    List<HistoricoLesao> findByAtletaId(Long atletaId);
}