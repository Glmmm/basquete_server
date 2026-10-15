package basquete.assis.features.atleta.controllers;


import basquete.assis.features.atleta.models.AtletaFormDTO;
import basquete.assis.features.atleta.services.AtletaService;
import basquete.assis.shared.entities.Atleta;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/atletas")
@RequiredArgsConstructor
@Tag(name = "Atletas", description = "Endpoints para gerenciamento de atletas")
public class AtletaController {

    private final AtletaService atletaService;

    @PostMapping
    @Operation(summary = "Cadastrar novo atleta")
    public ResponseEntity<Atleta> criar(@RequestBody @Valid AtletaFormDTO dto) {
        Atleta novoAtleta = atletaService.criar(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(novoAtleta);
    }

    @GetMapping
    @Operation(summary = "Listar todos os atletas")
    public ResponseEntity<List<Atleta>> listar() {
        return ResponseEntity.ok(atletaService.listarTodos());
    }
}