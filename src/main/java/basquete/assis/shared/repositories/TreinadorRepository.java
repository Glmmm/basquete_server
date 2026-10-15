package basquete.assis.shared.repositories;

import basquete.assis.shared.entities.Treinador;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TreinadorRepository extends JpaRepository<Treinador,String> {
}
