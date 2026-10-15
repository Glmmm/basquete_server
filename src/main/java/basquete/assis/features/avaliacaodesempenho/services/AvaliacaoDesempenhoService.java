package basquete.assis.features.avaliacaodesempenho.services;


import basquete.assis.features.avaliacaodesempenho.models.AvaliacaoDesempenhoFormDTO;
import basquete.assis.shared.entities.Atleta;
import basquete.assis.shared.entities.AvaliacaoDesempenho;
import basquete.assis.shared.entities.Treino;
import basquete.assis.shared.repositories.AtletaRepository;
import basquete.assis.shared.repositories.AvaliacaoDesempenhoRepository;
import basquete.assis.shared.repositories.TreinoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AvaliacaoDesempenhoService {

    private final AvaliacaoDesempenhoRepository avaliacaoRepository;
    private final AtletaRepository atletaRepository;
    private final TreinoRepository treinoRepository;

    @Transactional
    public AvaliacaoDesempenho salvar(AvaliacaoDesempenhoFormDTO dto) {
        Atleta atleta = atletaRepository.findById(dto.getAtletaId())
                .orElseThrow(() -> new RuntimeException("Atleta não encontrado"));

        Treino treino = null;
        if (dto.getTreinoId() != null) {
            treino = treinoRepository.findById(dto.getTreinoId()).orElse(null);
        }

        AvaliacaoDesempenho avaliacao = new AvaliacaoDesempenho();
        avaliacao.setAtleta(atleta);
        avaliacao.setTreino(treino);
        avaliacao.setNotaFisica(dto.getNotaFisica());
        avaliacao.setNotaTatica(dto.getNotaTatica());
        avaliacao.setNotaTecnica(dto.getNotaTecnica());
        avaliacao.setComentarios(dto.getComentarios());
        avaliacao.setStatus(dto.getStatus());

        return avaliacaoRepository.save(avaliacao);
    }

    public List<AvaliacaoDesempenho> listarPorAtleta(Long atletaId) {
        return avaliacaoRepository.findByAtletaId(atletaId);
    }
}