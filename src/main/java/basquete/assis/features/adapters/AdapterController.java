package basquete.assis.features.adapters;

import basquete.assis.features.atleta.AtletaStore;
import basquete.assis.shared.entities.Atleta;
import basquete.assis.shared.entities.Jogo;
import basquete.assis.shared.entities.Usuario;
import basquete.assis.shared.enums.Role;
import basquete.assis.shared.repositories.AtletaRepository;
import basquete.assis.shared.repositories.JogoRepository;
import basquete.assis.shared.repositories.UsuarioRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.time.ZoneId;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@RestController
public class AdapterController {

    private final UsuarioRepository usuarioRepository;
    private final AtletaRepository atletaRepository;
    private final AtletaStore atletaStore;
    private final JogoRepository jogoRepository;

    private static final Map<String, List<Map<String, Object>>> BOX_SCORES = new ConcurrentHashMap<>();
    private static final Map<String, Map<String, Object>> REGISTROS = new ConcurrentHashMap<>();

    public AdapterController(UsuarioRepository usuarioRepository,
                             AtletaRepository atletaRepository,
                             AtletaStore atletaStore,
                             JogoRepository jogoRepository) {
        this.usuarioRepository = usuarioRepository;
        this.atletaRepository = atletaRepository;
        this.atletaStore = atletaStore;
        this.jogoRepository = jogoRepository;
    }

    @GetMapping("/time")
    public ResponseEntity<Map<String, Object>> meuTime() {
        Map<String, Object> resp = new HashMap<>();
        resp.put("nome", "Basquete Assis");

        List<Map<String, Object>> comissao = new ArrayList<>();
        try {
            for (Usuario u : usuarioRepository.findAll()) {
                if (u.getRole() == Role.TREINADOR) {
                    Map<String, Object> t = new HashMap<>();
                    t.put("id", u.getId() != null ? u.getId().toString() : null);
                    t.put("nome", u.getNome());
                    t.put("cargo", "Treinador");
                    t.put("email", u.getEmail());
                    t.put("telefone", "");
                    t.put("fotoUrl", null);
                    comissao.add(t);
                }
            }
        } catch (Exception ignored) {}
        resp.put("comissaoTecnica", comissao);

        Map<String, Map<String, Object>> byId = new LinkedHashMap<>();
        try {
            for (Atleta a : atletaRepository.findAll()) {
                String id = a.getId() != null ? a.getId().toString() : UUID.randomUUID().toString();
                Map<String, Object> e = new HashMap<>();
                e.put("id", id);
                e.put("nome", a.getNome());
                e.put("email", a.getEmail());
                e.put("cpf", "");
                e.put("dataNascimento", a.getDataNascimento() != null ? a.getDataNascimento().toString() : null);
                e.put("telefone", "");
                e.put("numero", a.getCamisa() != null ? a.getCamisa() : 0);
                e.put("posicao", "ALA");
                e.put("ativo", true);
                byId.put(id, e);
            }
        } catch (Exception ignored) {}
        for (Map<String, Object> a : atletaStore.values()) {
            String id = a.get("id") != null ? a.get("id").toString() : null;
            if (id != null) {
                Map<String, Object> merged = new HashMap<>(byId.getOrDefault(id, Map.of()));
                merged.putAll(a);
                merged.putIfAbsent("ativo", true);
                byId.put(id, merged);
            } else {
                byId.put(UUID.randomUUID().toString(), new HashMap<>(a));
            }
        }
        resp.put("elenco", new ArrayList<>(byId.values()));
        return ResponseEntity.ok(resp);
    }

    @GetMapping("/estatisticas/me")
    public ResponseEntity<List<Map<String, Object>>> meuDesempenho() {
        String myId = currentAtletaIdStr();
        List<Map<String, Object>> out = new ArrayList<>();
        if (myId != null) {
            for (Map.Entry<String, List<Map<String, Object>>> e : BOX_SCORES.entrySet()) {
                for (Map<String, Object> l : e.getValue()) {
                    if (myId.equals(String.valueOf(l.get("atletaId")))) out.add(l);
                }
            }
        }
        return ResponseEntity.ok(out);
    }

    @GetMapping("/jogos/{id}/estatisticas")
    public ResponseEntity<List<Map<String, Object>>> boxScore(@PathVariable("id") String id) {
        List<Map<String, Object>> stored = BOX_SCORES.get(id);
        if (stored != null) return ResponseEntity.ok(stored);
        Jogo j = findJogo(id);
        List<Map<String, Object>> linhas = new ArrayList<>();
        for (Map<String, Object> a : atletaStore.values()) {
            if (Boolean.FALSE.equals(a.get("ativo"))) continue;
            linhas.add(linhaZerada(a, id, j));
        }
        if (linhas.isEmpty()) {
            try {
                for (Atleta a : atletaRepository.findAll()) {
                    Map<String, Object> am = new HashMap<>();
                    am.put("atletaId", a.getId() != null ? a.getId().toString() : null);
                    am.put("atletaNome", a.getNome());
                    am.put("numero", a.getCamisa() != null ? a.getCamisa() : 0);
                    linhas.add(linhaZerada(am, id, j));
                }
            } catch (Exception ignored) {}
        }
        BOX_SCORES.put(id, linhas);
        return ResponseEntity.ok(linhas);
    }

    @PutMapping("/jogos/{id}/estatisticas")
    public ResponseEntity<List<Map<String, Object>>> salvarBoxScore(@PathVariable("id") String id, @RequestBody List<Map<String, Object>> linhas) {
        Jogo j = findJogo(id);
        List<Map<String, Object>> enriched = new ArrayList<>();
        for (Map<String, Object> l : linhas) {
            Map<String, Object> m = new HashMap<>(l);
            m.put("jogoId", id);
            if (j != null) {
                if (j.getDataHoraInicio() != null) m.put("data", j.getDataHoraInicio().atZone(ZoneId.systemDefault()).toOffsetDateTime().toString());
                m.put("adversario", j.getAdversario());
            }
            enriched.add(m);
        }
        BOX_SCORES.put(id, enriched);
        return ResponseEntity.ok(enriched);
    }

    @GetMapping("/jogos/{id}/registro")
    public ResponseEntity<Map<String, Object>> registro(@PathVariable("id") String id) {
        Map<String, Object> r = REGISTROS.get(id);
        if (r != null) return ResponseEntity.ok(r);
        Map<String, Object> vazio = new HashMap<>();
        vazio.put("lances", Collections.emptyList());
        vazio.put("placarAdversario", 0);
        return ResponseEntity.ok(vazio);
    }

    @PutMapping("/jogos/{id}/registro")
    public ResponseEntity<Map<String, Object>> salvarRegistro(@PathVariable("id") String id, @RequestBody Map<String, Object> registro) {
        registro.put("jogoId", id);
        REGISTROS.put(id, new HashMap<>(registro));
        // atualiza placar do jogo persistido
        try {
            Jogo j = findJogo(id);
            if (j != null) {
                @SuppressWarnings("unchecked")
                List<Map<String, Object>> lances = registro.get("lances") instanceof List ? (List<Map<String, Object>>) registro.get("lances") : Collections.emptyList();
                j.setPlacarTime(pontosDosLances(lances));
                if (registro.get("placarAdversario") != null) j.setPlacarAdversario(toInt(registro.get("placarAdversario")));
                jogoRepository.save(j);
            }
        } catch (Exception ignored) {}
        return ResponseEntity.ok(registro);
    }

    private Jogo findJogo(String id) {
        try { return jogoRepository.findById(Long.parseLong(id)).orElse(null); } catch (Exception e) { return null; }
    }

    private String currentAtletaIdStr() {
        try {
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            if (auth != null && auth.getPrincipal() instanceof Usuario) {
                Usuario u = (Usuario) auth.getPrincipal();
                if (u.getId() != null && atletaRepository.findById(u.getId()).isPresent()) return u.getId().toString();
            }
        } catch (Exception ignored) {}
        return null;
    }

    private int pontosDosLances(List<Map<String, Object>> lances) {
        int pts = 0;
        for (Map<String, Object> l : lances) {
            String tipo = l.get("tipo") != null ? l.get("tipo").toString() : "";
            if ("LL_CONVERTIDO".equals(tipo)) pts += 1;
            else if ("DOIS_CONVERTIDO".equals(tipo)) pts += 2;
            else if ("TRES_CONVERTIDO".equals(tipo)) pts += 3;
        }
        return pts;
    }

    private int toInt(Object o) { try { return Integer.parseInt(String.valueOf(o)); } catch (Exception e) { return 0; } }

    private Map<String, Object> linhaZerada(Map<String, Object> atleta, String jogoId, Jogo j) {
        Map<String, Object> m = new HashMap<>();
        m.put("atletaId", atleta.get("atletaId") != null ? atleta.get("atletaId") : atleta.get("id"));
        m.put("atletaNome", atleta.get("atletaNome") != null ? atleta.get("atletaNome") : atleta.get("nome"));
        m.put("numero", atleta.get("numero") != null ? atleta.get("numero") : 0);
        m.put("jogoId", jogoId);
        if (j != null) {
            if (j.getDataHoraInicio() != null) m.put("data", j.getDataHoraInicio().atZone(ZoneId.systemDefault()).toOffsetDateTime().toString());
            m.put("adversario", j.getAdversario());
        }
        for (String k : List.of("minutos","assistencias","rebotes","tocos","roubos","lanceLivreConvertidos","lanceLivreTentados","doisPontosConvertidos","doisPontosTentados","tresPontosConvertidos","tresPontosTentados"))
            m.put(k, 0);
        return m;
    }
}
