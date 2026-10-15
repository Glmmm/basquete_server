package basquete.assis.features.treinador.controllers;

import basquete.assis.features.treinador.models.TreinadorFormDTO;
import basquete.assis.features.treinador.services.TreinadorService;
import basquete.assis.shared.entities.Treinador;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/treinadores")
@RequiredArgsConstructor
@Tag(name = "Treinadores", description = "Endpoints para gerenciamento de treinadores")
public class TreinadorController {

    private final TreinadorService treinadorService;

    @PostMapping
    @Operation(summary = "Cadastrar novo treinador")
    public ResponseEntity<Treinador> criar(@RequestBody @Valid TreinadorFormDTO dto) {
        Treinador novoTreinador = treinadorService.criar(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(novoTreinador);
    }

    @GetMapping
    @Operation(summary = "Listar todos os treinadores")
    public ResponseEntity<List<Treinador>> listar() {
        return ResponseEntity.ok(treinadorService.listarTodos());
    }
}