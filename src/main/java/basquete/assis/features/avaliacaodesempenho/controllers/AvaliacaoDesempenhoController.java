package basquete.assis.features.avaliacaodesempenho.controllers;

import basquete.assis.features.avaliacaodesempenho.models.AvaliacaoDesempenhoFormDTO;
import basquete.assis.features.avaliacaodesempenho.services.AvaliacaoDesempenhoService;
import basquete.assis.shared.entities.AvaliacaoDesempenho;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/avaliacoes-desempenho")
@RequiredArgsConstructor
@Tag(name = "Avaliação de Desempenho", description = "Gestão de notas físicas, táticas, técnicas e comentários dos atletas")
public class AvaliacaoDesempenhoController {

    private final AvaliacaoDesempenhoService avaliacaoService;

    @PostMapping
    @Operation(summary = "Criar ou salvar avaliação de desempenho do atleta")
    public ResponseEntity<AvaliacaoDesempenho> salvar(@RequestBody @Valid AvaliacaoDesempenhoFormDTO dto) {
        AvaliacaoDesempenho avaliacao = avaliacaoService.salvar(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(avaliacao);
    }

    @GetMapping("/atleta/{atletaId}")
    @Operation(summary = "Listar histórico de avaliações de desempenho por atleta")
    public ResponseEntity<List<AvaliacaoDesempenho>> listarPorAtleta(@PathVariable Long atletaId) {
        List<AvaliacaoDesempenho> avaliacoes = avaliacaoService.listarPorAtleta(atletaId);
        return ResponseEntity.ok(avaliacoes);
    }
}