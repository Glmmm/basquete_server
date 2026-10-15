package basquete.assis.features.treino.controllers;

import basquete.assis.features.treino.models.TreinoFormDTO;
import basquete.assis.features.treino.services.AgendamentoService;
import basquete.assis.features.treino.services.PresencaService;
import basquete.assis.features.treino.services.TreinoService;
import basquete.assis.shared.entities.Agendamento;
import basquete.assis.shared.entities.Presenca;
import basquete.assis.shared.entities.Treino;
import basquete.assis.shared.enums.StatusPresenca;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/treinos")
@RequiredArgsConstructor
@Tag(name = "Treinos e Presenças", description = "Gestão de treinos, agendamentos e chamada de presença")
public class TreinoController {

    private final TreinoService treinoService;
    private final PresencaService presencaService;
    private final AgendamentoService agendamentoService;

    @PostMapping
    @Operation(summary = "Criar novo treino ou agendamento")
    public ResponseEntity<Treino> criarTreino(@RequestBody @Valid TreinoFormDTO dto) {
        Treino treino = treinoService.criarTreino(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(treino);
    }

    @PostMapping("/{agendamentoId}/presencas/{atletaId}")
    @Operation(summary = "Confirmar ou atualizar presença do atleta em um treino/jogo")
    public ResponseEntity<Presenca> registrarPresenca(
            @PathVariable Long agendamentoId,
            @PathVariable Long atletaId,
            @RequestParam StatusPresenca status) {
        Presenca presenca = presencaService.confirmarPresenca(atletaId, agendamentoId, status);
        return ResponseEntity.ok(presenca);
    }

    @GetMapping("/calendario")
    @Operation(summary = "Consultar agendamentos e treinos por intervalo de datas para exibição em calendário")
    public ResponseEntity<List<Agendamento>> listarParaCalendario(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime inicio,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime fim) {

        List<Agendamento> agendamentos = agendamentoService.listarPorPeriodo(inicio, fim);
        return ResponseEntity.ok(agendamentos);
    }
}