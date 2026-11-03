package basquete.assis.features.relatorios.controllers;


import basquete.assis.features.relatorios.services.RelatorioService;
import basquete.assis.shared.entities.Agendamento;
import basquete.assis.shared.entities.AvaliacaoDesempenho;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/relatorios")
@RequiredArgsConstructor
@Tag(name = "Relatórios e Analytics", description = "Endpoints para emissão de relatórios gerenciais e estatísticas")
public class RelatorioController {

    private final RelatorioService relatorioService;

    @GetMapping("/desempenho-individual/{atletaId}")
    @Operation(summary = "Emitir relatório de desempenho individual do atleta")
    public ResponseEntity<List<AvaliacaoDesempenho>> relatorioDesempenhoIndividual(@PathVariable Long atletaId) {
        return ResponseEntity.ok(relatorioService.gerarRelatorioDesempenhoIndividual(atletaId));
    }

    @GetMapping("/retrospectiva-ano/{ano}")
    @Operation(summary = "Emitir retrospectiva de jogos e treinos por ano")
    public ResponseEntity<List<Agendamento>> retrospectivaPorAno(@PathVariable int ano) {
        return ResponseEntity.ok(relatorioService.gerarRetrospectivaPorAno(ano));
    }

    @GetMapping("/distribuicao-categorias")
    @Operation(summary = "Obter distribuição de atletas por categoria base")
    public ResponseEntity<Map<String, Long>> distribuicaoPorCategoria() {
        return ResponseEntity.ok(relatorioService.gerarDistribuicaoPorCategoria());
    }

    @GetMapping("/extrato-estoque")
    @Operation(summary = "Emitir extrato de estoque e suprimentos")
    public ResponseEntity<Map<String, Object>> extratoEstoque() {
        return ResponseEntity.ok(relatorioService.gerarExtratoEstoqueSimulado());
    }
}