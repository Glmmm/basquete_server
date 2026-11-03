package basquete.assis.features.relatorios.services;


import basquete.assis.shared.entities.Agendamento;
import basquete.assis.shared.entities.AvaliacaoDesempenho;
import basquete.assis.shared.repositories.AgendamentoRepository;
import basquete.assis.shared.repositories.AtletaRepository;
import basquete.assis.shared.repositories.AvaliacaoDesempenhoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class RelatorioService {

    private final AvaliacaoDesempenhoRepository avaliacaoDesempenhoRepository;
    private final AtletaRepository atletaRepository;
    private final AgendamentoRepository agendamentoRepository;

    public List<AvaliacaoDesempenho> gerarRelatorioDesempenhoIndividual(Long atletaId) {
        return avaliacaoDesempenhoRepository.findByAtletaId(atletaId);
    }

    public List<Agendamento> gerarRetrospectivaPorAno(int ano) {
        return agendamentoRepository.findJogosTreinosPorAno(ano);
    }

    public Map<String, Long> gerarDistribuicaoPorCategoria() {
        List<Object[]> resultados = atletaRepository.contarAtletasPorCategoria();
        Map<String, Long> distribuicao = new HashMap<>();
        for (Object[] row : resultados) {
            distribuicao.put((String) row[0], (Long) row[1]);
        }
        return distribuicao;
    }

    public Map<String, Object> gerarExtratoEstoqueSimulado() {
        Map<String, Object> extrato = new HashMap<>();
        extrato.put("totalItensCadastrados", 45);
        extrato.put("itensComEstoqueBaixo", List.of("Kit de Primeiros Socorros", "Coletes de Treino M"));
        extrato.put("dataGeracao", java.time.LocalDateTime.now());
        return extrato;
    }
}