package basquete.assis.shared.repositories;

import basquete.assis.shared.entities.CategoriaBase;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CategoriaBaseRepository extends JpaRepository<CategoriaBase, Long> {}
