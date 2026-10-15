package basquete.assis.shared.repositories;


import basquete.assis.shared.entities.FichaTecnica;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface FichaTecnicaRepository extends JpaRepository<FichaTecnica, Long> {}