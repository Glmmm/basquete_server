package basquete.assis.shared.repositories;

import basquete.assis.shared.entities.Presenca;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PresencaRepository extends JpaRepository<Presenca, Long> {
    List<Presenca> findByAgendamentoId(Long agendamentoId);
}