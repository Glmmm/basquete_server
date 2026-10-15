package basquete.assis.features.treinador.services;

import basquete.assis.features.treinador.models.TreinadorFormDTO;
import basquete.assis.shared.entities.Treinador;
import basquete.assis.shared.enums.Role;
import basquete.assis.shared.repositories.TreinadorRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class TreinadorService {

    private final TreinadorRepository treinadorRepository;
    private final PasswordEncoder passwordEncoder;

    @Transactional
    public Treinador criar(TreinadorFormDTO dto) {
        Treinador treinador = new Treinador();
        treinador.setNome(dto.getNome());
        treinador.setEmail(dto.getEmail());
        treinador.setSenhaHash(passwordEncoder.encode(dto.getSenha()));
        treinador.setRole(Role.TREINADOR);
        treinador.setCref(dto.getCref());
        treinador.setEspecialidade(dto.getEspecialidade());

        return treinadorRepository.save(treinador);
    }

    public List<Treinador> listarTodos() {
        return treinadorRepository.findAll();
    }
}