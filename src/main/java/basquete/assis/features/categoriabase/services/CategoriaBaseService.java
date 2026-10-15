package basquete.assis.features.categoriabase.services;

import basquete.assis.features.categoriabase.models.CategoriaBaseFormDTO;
import basquete.assis.shared.entities.CategoriaBase;
import basquete.assis.shared.repositories.CategoriaBaseRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CategoriaBaseService {

    private final CategoriaBaseRepository categoriaBaseRepository;

    @Transactional
    public CategoriaBase criar(CategoriaBaseFormDTO dto) {
        CategoriaBase categoria = new CategoriaBase();
        categoria.setNome(dto.getNome());
        return categoriaBaseRepository.save(categoria);
    }

    public List<CategoriaBase> listarTodas() {
        return categoriaBaseRepository.findAll();
    }
}