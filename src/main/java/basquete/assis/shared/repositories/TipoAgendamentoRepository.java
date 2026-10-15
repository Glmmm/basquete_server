package basquete.assis.shared.repositories;

import basquete.assis.shared.entities.TipoAgendamento;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TipoAgendamentoRepository extends JpaRepository<TipoAgendamento, Long> {
}
