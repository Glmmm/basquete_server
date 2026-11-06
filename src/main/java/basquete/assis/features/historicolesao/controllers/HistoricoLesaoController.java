package basquete.assis.features.historicolesao.controllers;

import basquete.assis.features.historicolesao.models.HistoricoLesaoFormDTO;
import basquete.assis.shared.entities.Atleta;
import basquete.assis.shared.entities.HistoricoLesao;
import basquete.assis.shared.entities.Usuario;
import basquete.assis.shared.repositories.AtletaRepository;
import basquete.assis.shared.repositories.HistoricoLesaoRepository;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/historico-lesoes")
public class HistoricoLesaoController {

    private final HistoricoLesaoRepository historicoLesaoRepository;
    private final AtletaRepository atletaRepository;

    public HistoricoLesaoController(HistoricoLesaoRepository historicoLesaoRepository,
                                    AtletaRepository atletaRepository) {
        this.historicoLesaoRepository = historicoLesaoRepository;
        this.atletaRepository = atletaRepository;
    }

    @PostMapping
    public ResponseEntity<?> criar(@RequestBody @Valid HistoricoLesaoFormDTO dto) {
        Atleta atleta = atletaRepository.findById(dto.getAtletaId()).orElse(null);
        if (atleta == null) return ResponseEntity.badRequest().body(Map.of("message", "Atleta não encontrado"));
        Usuario registrador = currentUser();
        if (registrador == null) registrador = atleta; // fallback: o próprio atleta
        HistoricoLesao lesao = new HistoricoLesao();
        lesao.setAtleta(atleta);
        lesao.setRegistradoPor(registrador);
        lesao.setDataInicio(dto.getDataInicio());
        lesao.setDataRetorno(dto.getDataRetorno());
        lesao.setDescricaoLesao(dto.getDescricaoLesao());
        lesao.setProcedimentosRealizados(dto.getProcedimentosRealizados());
        lesao.setMedicamentos(dto.getMedicamentos());
        lesao.setPeriodoRepouso(dto.getPeriodoRepouso());
        lesao = historicoLesaoRepository.save(lesao);
        return ResponseEntity.status(HttpStatus.CREATED).body(toMap(lesao));
    }

    @GetMapping("/atleta/{atletaId}")
    public ResponseEntity<List<Map<String, Object>>> listarPorAtleta(@PathVariable("atletaId") Long atletaId) {
        List<Map<String, Object>> out = new ArrayList<>();
        for (HistoricoLesao l : historicoLesaoRepository.findByAtletaId(atletaId)) out.add(toMap(l));
        return ResponseEntity.ok(out);
    }

    private Usuario currentUser() {
        try {
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            if (auth != null && auth.getPrincipal() instanceof Usuario) return (Usuario) auth.getPrincipal();
        } catch (Exception ignored) {}
        return null;
    }

    private Map<String, Object> toMap(HistoricoLesao l) {
        Map<String, Object> m = new LinkedHashMap<>();
        m.put("id", l.getId());
        m.put("atletaId", l.getAtleta() != null ? l.getAtleta().getId() : null);
        m.put("registradoPorUsuarioId", l.getRegistradoPor() != null ? l.getRegistradoPor().getId() : null);
        m.put("dataInicio", l.getDataInicio() != null ? l.getDataInicio().toString() : null);
        m.put("dataRetorno", l.getDataRetorno() != null ? l.getDataRetorno().toString() : null);
        m.put("descricaoLesao", l.getDescricaoLesao());
        m.put("procedimentosRealizados", l.getProcedimentosRealizados());
        m.put("medicamentos", l.getMedicamentos());
        m.put("periodoRepouso", l.getPeriodoRepouso());
        return m;
    }
}
