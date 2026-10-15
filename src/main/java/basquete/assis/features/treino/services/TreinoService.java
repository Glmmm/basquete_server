package basquete.assis.features.treino.services;

import basquete.assis.features.treino.models.TreinoFormDTO;
import basquete.assis.shared.entities.CategoriaBase;
import basquete.assis.shared.entities.TipoAgendamento;
import basquete.assis.shared.entities.Treino;
import basquete.assis.shared.repositories.CategoriaBaseRepository;
import basquete.assis.shared.repositories.TipoAgendamentoRepository;
import basquete.assis.shared.repositories.TreinoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@Service
@RequiredArgsConstructor
public class TreinoService {

    private final TreinoRepository treinoRepository;
    private final TipoAgendamentoRepository tipoAgendamentoRepository;
    private final CategoriaBaseRepository categoriaBaseRepository;

    @Transactional
    public Treino criarTreino(TreinoFormDTO dto) {
        TipoAgendamento tipo = tipoAgendamentoRepository.findById(dto.getTipoAgendamentoId())
                .orElseThrow(() -> new RuntimeException("Tipo de agendamento não encontrado"));

        CategoriaBase categoria = categoriaBaseRepository.findById(dto.getCategoriaBaseId())
                .orElseThrow(() -> new RuntimeException("Categoria base não encontrada"));

        Treino treino = new Treino();
        treino.setTitulo(dto.getTitulo());
        treino.setDescricao(dto.getDescricao());
        treino.setDataHoraInicio(dto.getDataHoraInicio());
        treino.setDataHoraFim(dto.getDataHoraFim());
        treino.setLocal(dto.getLocal());
        treino.setTipoAgendamento(tipo);
        treino.setTipoTreino(dto.getTipoTreino());
        treino.setCategoriaBase(categoria);

        return treinoRepository.save(treino);
    }
}