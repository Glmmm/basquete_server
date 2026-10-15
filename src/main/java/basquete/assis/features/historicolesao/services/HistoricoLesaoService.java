package basquete.assis.features.historicolesao.services;


import basquete.assis.features.historicolesao.models.HistoricoLesaoFormDTO;
import basquete.assis.shared.entities.Atleta;
import basquete.assis.shared.entities.HistoricoLesao;
import basquete.assis.shared.repositories.AtletaRepository;
import basquete.assis.shared.repositories.HistoricoLesaoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class HistoricoLesaoService {

    private final HistoricoLesaoRepository historicoLesaoRepository;
    private final AtletaRepository atletaRepository;

    @Transactional
    public HistoricoLesao criar(HistoricoLesaoFormDTO dto) {
        Atleta atleta = atletaRepository.findById(dto.getAtletaId())
                .orElseThrow(() -> new RuntimeException("Atleta não encontrado"));

        HistoricoLesao lesao = new HistoricoLesao();
        lesao.setAtleta(atleta);
        lesao.setDataInicio(dto.getDataInicio());
        lesao.setDataRetorno(dto.getDataRetorno());
        lesao.setDescricaoLesao(dto.getDescricaoLesao());
        lesao.setProcedimentosRealizados(dto.getProcedimentosRealizados());
        lesao.setMedicamentos(dto.getMedicamentos());
        lesao.setPeriodoRepouso(dto.getPeriodoRepouso());

        return historicoLesaoRepository.save(lesao);
    }

    public List<HistoricoLesao> listarPorAtleta(Long atletaId) {
        return historicoLesaoRepository.findByAtletaId(atletaId);
    }
}
