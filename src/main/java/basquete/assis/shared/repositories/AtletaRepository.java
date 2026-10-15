package basquete.assis.shared.repositories;

import basquete.assis.shared.entities.Atleta;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AtletaRepository extends JpaRepository<Atleta, Long> {
}
