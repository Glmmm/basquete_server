package basquete.assis.features.categoriabase.controllers;

import basquete.assis.features.categoriabase.models.CategoriaBaseFormDTO;
import basquete.assis.features.categoriabase.services.CategoriaBaseService;
import basquete.assis.shared.entities.CategoriaBase;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/categorias-base")
@RequiredArgsConstructor
@Tag(name = "Categorias Base", description = "Endpoints para gerenciamento de categorias (Sub 13, Sub 17, Adulto)")
public class CategoriaBaseController {

    private final CategoriaBaseService categoriaBaseService;

    @PostMapping
    @Operation(summary = "Cadastrar nova categoria base")
    public ResponseEntity<CategoriaBase> criar(@RequestBody @Valid CategoriaBaseFormDTO dto) {
        CategoriaBase novaCategoria = categoriaBaseService.criar(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(novaCategoria);
    }

    @GetMapping
    @Operation(summary = "Listar todas as categorias base")
    public ResponseEntity<List<CategoriaBase>> listar() {
        return ResponseEntity.ok(categoriaBaseService.listarTodas());
    }
}