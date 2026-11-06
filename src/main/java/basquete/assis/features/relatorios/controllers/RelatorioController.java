package basquete.assis.features.relatorios.controllers;

import basquete.assis.features.relatorios.services.RelatorioService;
import basquete.assis.shared.entities.Agendamento;
import basquete.assis.shared.entities.AvaliacaoDesempenho;
import basquete.assis.shared.entities.Jogo;
import basquete.assis.shared.entities.Treino;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.ZoneId;
import java.util.*;

@RestController
@RequestMapping("/relatorios")
public class RelatorioController {

    private final RelatorioService relatorioService;

    public RelatorioController(RelatorioService relatorioService) {
        this.relatorioService = relatorioService;
    }

    @GetMapping("/desempenho-individual/{atletaId}")
    public ResponseEntity<List<Map<String, Object>>> relatorioDesempenhoIndividual(@PathVariable("atletaId") Long atletaId) {
        List<Map<String, Object>> out = new ArrayList<>();
        for (AvaliacaoDesempenho a : relatorioService.gerarRelatorioDesempenhoIndividual(atletaId)) {
            Map<String, Object> m = new LinkedHashMap<>();
            m.put("id", a.getId());
            m.put("atletaId", a.getAtleta() != null ? a.getAtleta().getId() : atletaId);
            m.put("treinoId", a.getTreino() != null ? a.getTreino().getId() : null);
            m.put("notaFisica", a.getNotaFisica());
            m.put("notaTatica", a.getNotaTatica());
            m.put("notaTecnica", a.getNotaTecnica());
            m.put("comentarios", a.getComentarios());
            m.put("status", a.getStatus() != null ? a.getStatus().name() : null);
            out.add(m);
        }
        return ResponseEntity.ok(out);
    }

    @GetMapping("/retrospectiva-ano/{ano}")
    public ResponseEntity<List<Map<String, Object>>> retrospectivaPorAno(@PathVariable("ano") int ano) {
        List<Map<String, Object>> out = new ArrayList<>();
        for (Agendamento a : relatorioService.gerarRetrospectivaPorAno(ano)) {
            Map<String, Object> m = new LinkedHashMap<>();
            m.put("id", a.getId() != null ? a.getId().toString() : null);
            m.put("titulo", a.getTitulo());
            m.put("local", a.getLocal());
            m.put("inicio", a.getDataHoraInicio() != null ? a.getDataHoraInicio().atZone(ZoneId.systemDefault()).toOffsetDateTime().toString() : null);
            m.put("fim", a.getDataHoraFim() != null ? a.getDataHoraFim().atZone(ZoneId.systemDefault()).toOffsetDateTime().toString() : null);
            if (a instanceof Treino) {
                m.put("tipo", "TREINO");
                m.put("foco", ((Treino) a).getTipoTreino());
            } else if (a instanceof Jogo) {
                Jogo j = (Jogo) a;
                m.put("tipo", "JOGO");
                m.put("adversario", j.getAdversario());
                m.put("mandante", j.getMandante());
                m.put("placarTime", j.getPlacarTime());
                m.put("placarAdversario", j.getPlacarAdversario());
            } else {
                m.put("tipo", "EVENTO");
            }
            out.add(m);
        }
        return ResponseEntity.ok(out);
    }

    @GetMapping("/distribuicao-categorias")
    public ResponseEntity<Map<String, Long>> distribuicaoPorCategoria() {
        return ResponseEntity.ok(relatorioService.gerarDistribuicaoPorCategoria());
    }

    @GetMapping("/extrato-estoque")
    public ResponseEntity<Map<String, Object>> extratoEstoque() {
        return ResponseEntity.ok(relatorioService.gerarExtratoEstoqueSimulado());
    }
}
