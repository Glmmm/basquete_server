package basquete.assis.features.fichatecnica.controllers;

import basquete.assis.features.fichatecnica.models.FichaTecnicaFormDTO;
import basquete.assis.features.fichatecnica.services.FichaTecnicaService;
import basquete.assis.shared.entities.FichaTecnica;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.LinkedHashMap;
import java.util.Map;

@RestController
@RequestMapping("/fichas-tecnicas")
public class FichaTecnicaController {

    private final FichaTecnicaService fichaTecnicaService;

    public FichaTecnicaController(FichaTecnicaService fichaTecnicaService) {
        this.fichaTecnicaService = fichaTecnicaService;
    }

    @PostMapping
    public ResponseEntity<Map<String, Object>> salvarOuAtualizar(@RequestBody @Valid FichaTecnicaFormDTO dto) {
        FichaTecnica ficha = fichaTecnicaService.salvarOuAtualizar(dto);
        return ResponseEntity.ok(toMap(ficha, dto.getAtletaId()));
    }

    @GetMapping("/atleta/{atletaId}")
    public ResponseEntity<Map<String, Object>> buscarPorAtleta(@PathVariable("atletaId") Long atletaId) {
        try {
            FichaTecnica ficha = fichaTecnicaService.buscarPorAtleta(atletaId);
            return ficha != null ? ResponseEntity.ok(toMap(ficha, atletaId)) : ResponseEntity.notFound().build();
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }

    private Map<String, Object> toMap(FichaTecnica f, Long atletaId) {
        Map<String, Object> m = new LinkedHashMap<>();
        m.put("id", f.getId());
        m.put("atletaId", atletaId != null ? atletaId : f.getId());
        m.put("altura", f.getAltura());
        m.put("peso", f.getPeso());
        m.put("experienciaAnos", f.getExperienciaAnos());
        m.put("historicoDoencas", f.getHistoricoDoencas());
        m.put("medicamentosUsoContinuo", f.getMedicamentosUsoContinuo());
        m.put("dataAtualizacao", f.getDataAtualizacao() != null ? f.getDataAtualizacao().toString() : null);
        return m;
    }
}
