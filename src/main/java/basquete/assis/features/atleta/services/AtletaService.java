package basquete.assis.features.atleta.services;


import basquete.assis.features.atleta.models.AtletaFormDTO;
import basquete.assis.shared.entities.Atleta;
import basquete.assis.shared.entities.CategoriaBase;
import basquete.assis.shared.enums.Role;
import basquete.assis.shared.repositories.AtletaRepository;
import basquete.assis.shared.repositories.CategoriaBaseRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AtletaService {

    private final AtletaRepository atletaRepository;
    private final CategoriaBaseRepository categoriaBaseRepository;

    @Transactional
    public Atleta criar(AtletaFormDTO dto) {
        CategoriaBase categoria = categoriaBaseRepository.findById(dto.getCategoriaBaseId())
                .orElseThrow(() -> new RuntimeException("Categoria Base não encontrada"));

        Atleta atleta = new Atleta();
        atleta.setNome(dto.getNome());
        atleta.setEmail(dto.getEmail());
        atleta.setSenhaHash(dto.getSenha());
        atleta.setRole(Role.ATLETA);
        atleta.setDataNascimento(dto.getDataNascimento());
        atleta.setCategoriaBase(categoria);

        return atletaRepository.save(atleta);
    }

    public List<Atleta> listarTodos() {
        return atletaRepository.findAll();
    }
}