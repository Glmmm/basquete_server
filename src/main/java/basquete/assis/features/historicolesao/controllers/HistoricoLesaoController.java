package basquete.assis.features.historicolesao.controllers;

import basquete.assis.features.historicolesao.models.HistoricoLesaoFormDTO;
import basquete.assis.features.historicolesao.services.HistoricoLesaoService;
import basquete.assis.shared.entities.HistoricoLesao;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/historico-lesoes")
@RequiredArgsConstructor
@Tag(name = "Histórico de Lesões", description = "Endpoints para gestão de lesões dos atletas")
public class HistoricoLesaoController {

    private final HistoricoLesaoService historicoLesaoService;

    @PostMapping
    @Operation(summary = "Registar nova lesão para o atleta")
    public ResponseEntity<HistoricoLesao> criar(@RequestBody @Valid HistoricoLesaoFormDTO dto) {
        HistoricoLesao novaLesao = historicoLesaoService.criar(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(novaLesao);
    }

    @GetMapping("/atleta/{atletaId}")
    @Operation(summary = "Listar histórico de lesões por atleta")
    public ResponseEntity<List<HistoricoLesao>> listarPorAtleta(@PathVariable Long atletaId) {
        return ResponseEntity.ok(historicoLesaoService.listarPorAtleta(atletaId));
    }
}