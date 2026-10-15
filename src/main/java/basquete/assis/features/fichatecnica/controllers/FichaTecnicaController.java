package basquete.assis.features.fichatecnica.controllers;

import basquete.assis.features.fichatecnica.models.FichaTecnicaFormDTO;
import basquete.assis.features.fichatecnica.services.FichaTecnicaService;
import basquete.assis.shared.entities.FichaTecnica;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/fichas-tecnicas")
@RequiredArgsConstructor
@Tag(name = "Ficha Técnica", description = "Endpoints para gestão da ficha técnica do atleta")
public class FichaTecnicaController {

    private final FichaTecnicaService fichaTecnicaService;

    @PostMapping
    @Operation(summary = "Criar ou atualizar ficha técnica de um atleta")
    public ResponseEntity<FichaTecnica> salvarOuAtualizar(@RequestBody @Valid FichaTecnicaFormDTO dto) {
        FichaTecnica ficha = fichaTecnicaService.salvarOuAtualizar(dto);
        return ResponseEntity.ok(ficha);
    }

    @GetMapping("/atleta/{atletaId}")
    @Operation(summary = "Buscar ficha técnica por ID do atleta")
    public ResponseEntity<FichaTecnica> buscarPorAtleta(@PathVariable Long atletaId) {
        FichaTecnica ficha = fichaTecnicaService.buscarPorAtleta(atletaId);
        return ficha != null ? ResponseEntity.ok(ficha) : ResponseEntity.notFound().build();
    }
}