package basquete.assis.features.eventos.controllers;

import basquete.assis.shared.entities.*;
import basquete.assis.features.atleta.AtletaStore;
import basquete.assis.shared.enums.StatusPresenca;
import basquete.assis.shared.repositories.*;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import java.time.*;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/eventos")
public class EventoController {

    private final TreinoRepository treinoRepository;
    private final AgendamentoRepository agendamentoRepository;
    private final PresencaRepository presencaRepository;
    private final TipoAgendamentoRepository tipoAgendamentoRepository;
    private final CategoriaBaseRepository categoriaBaseRepository;
    private final AtletaRepository atletaRepository;
    private final JogoRepository jogoRepository;
    private final AtletaStore atletaStore;
    private final UsuarioRepository usuarioRepository;

    // Fallback para jogos e casos não persistidos no domínio
    private static final Map<String, Map<String, Object>> FALLBACK = new ConcurrentHashMap<>();
    private static final Map<String, List<Map<String, Object>>> PRESENCAS_FALLBACK = new ConcurrentHashMap<>();

    public EventoController(TreinoRepository treinoRepository,
                            AgendamentoRepository agendamentoRepository,
                            PresencaRepository presencaRepository,
                            TipoAgendamentoRepository tipoAgendamentoRepository,
                            CategoriaBaseRepository categoriaBaseRepository,
                            AtletaRepository atletaRepository,
                            AtletaStore atletaStore,
                            UsuarioRepository usuarioRepository,
                            JogoRepository jogoRepository) {
        this.treinoRepository = treinoRepository;
        this.agendamentoRepository = agendamentoRepository;
        this.presencaRepository = presencaRepository;
        this.tipoAgendamentoRepository = tipoAgendamentoRepository;
        this.categoriaBaseRepository = categoriaBaseRepository;
        this.atletaRepository = atletaRepository;
        this.atletaStore = atletaStore;
        this.usuarioRepository = usuarioRepository;
        this.jogoRepository = jogoRepository;
    }

    @GetMapping
    public ResponseEntity<List<Map<String, Object>>> listar(
            @RequestParam(name = "de", required = false) String de,
            @RequestParam(name = "ate", required = false) String ate,
            @RequestParam(name = "tipo", required = false) String tipo) {
        List<Map<String, Object>> result = new ArrayList<>();
        try {
            for (Agendamento a : agendamentoRepository.findAll()) {
                Map<String, Object> m = toClient(a);
                if (tipo == null || tipo.equalsIgnoreCase(String.valueOf(m.get("tipo")))) {
                    result.add(m);
                }
            }
        } catch (Exception ignored) {}
        for (Map<String, Object> e : FALLBACK.values()) {
            if (tipo == null || tipo.equalsIgnoreCase(String.valueOf(e.get("tipo")))) {
                result.add(e);
            }
        }
        result.sort(Comparator.comparing(e -> String.valueOf(e.getOrDefault("inicio", ""))));
        return ResponseEntity.ok(result);
    }

    @GetMapping("/proximo")
    public ResponseEntity<Object> proximo() {
        String now = OffsetDateTime.now().toString();
        List<Map<String, Object>> all = listar(null, null, null).getBody();
        if (all == null) return ResponseEntity.noContent().build();
        return all.stream()
                .filter(e -> String.valueOf(e.getOrDefault("inicio", "")).compareTo(now) >= 0)
                .findFirst()
                .<ResponseEntity<Object>>map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.noContent().build());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Map<String, Object>> buscar(@PathVariable("id") String id) {
        try {
            Long lid = Long.parseLong(id);
            return agendamentoRepository.findById(lid)
                    .map(a -> ResponseEntity.ok(toClient(a)))
                    .orElseGet(() -> {
                        Map<String, Object> fb = FALLBACK.get(id);
                        return fb != null ? ResponseEntity.ok(fb) : ResponseEntity.notFound().build();
                    });
        } catch (Exception e) {
            Map<String, Object> fb = FALLBACK.get(id);
            return fb != null ? ResponseEntity.ok(fb) : ResponseEntity.notFound().build();
        }
    }

    @PostMapping
    public ResponseEntity<Map<String, Object>> criar(@RequestBody Map<String, Object> body) {
        String tipo = String.valueOf(body.getOrDefault("tipo", "TREINO")).toUpperCase();
        try {
            if ("TREINO".equals(tipo)) {
                Treino t = new Treino();
                applyAgendamento(t, body);
                t.setTipoTreino(String.valueOf(body.getOrDefault("foco", body.getOrDefault("tipoTreino", "Geral"))));
                t.setTipoAgendamento(getTipo("Treino"));
                CategoriaBase cat = categoriaBaseRepository.findAll().stream().findFirst().orElse(null);
                t.setCategoriaBase(cat);
                t = treinoRepository.save(t);
                return ResponseEntity.ok(toClient(t));
            } else {
                Jogo j = new Jogo();
                applyAgendamento(j, body);
                j.setAdversario(body.get("adversario") != null ? body.get("adversario").toString() : "");
                j.setMandante(body.get("mandante") == null || Boolean.TRUE.equals(body.get("mandante")) || "true".equalsIgnoreCase(String.valueOf(body.get("mandante"))));
                try { if (body.get("placarTime") != null) j.setPlacarTime(Integer.parseInt(body.get("placarTime").toString())); } catch (Exception ignored) {}
                try { if (body.get("placarAdversario") != null) j.setPlacarAdversario(Integer.parseInt(body.get("placarAdversario").toString())); } catch (Exception ignored) {}
                j.setTipoAgendamento(getTipo("Jogo"));
                j = jogoRepository.save(j);
                return ResponseEntity.ok(toClient(j));
            }
        } catch (Exception e) {
            String id = body.get("id") != null ? body.get("id").toString() : UUID.randomUUID().toString();
            body.put("id", id);
            body.putIfAbsent("resumoPresenca", Map.of("convocados", 0, "confirmados", 0, "ausentes", 0, "pendentes", 0));
            FALLBACK.put(id, new HashMap<>(body));
            return ResponseEntity.ok(body);
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<Map<String, Object>> atualizar(@PathVariable("id") String id, @RequestBody Map<String, Object> body) {
        try {
            Long lid = Long.parseLong(id);
            Optional<Agendamento> opt = agendamentoRepository.findById(lid);
            if (opt.isPresent()) {
                Agendamento a = opt.get();
                applyAgendamento(a, body);
                if (a instanceof Treino) {
                    Treino t = (Treino) a;
                    if (body.get("foco") != null) t.setTipoTreino(body.get("foco").toString());
                    treinoRepository.save(t);
                    return ResponseEntity.ok(toClient(t));
                } else if (a instanceof Jogo) {
                    Jogo j = (Jogo) a;
                    if (body.get("adversario") != null) j.setAdversario(body.get("adversario").toString());
                    if (body.get("mandante") != null) j.setMandante(Boolean.TRUE.equals(body.get("mandante")) || "true".equalsIgnoreCase(String.valueOf(body.get("mandante"))));
                    try { if (body.containsKey("placarTime")) j.setPlacarTime(body.get("placarTime") == null ? null : Integer.parseInt(body.get("placarTime").toString())); } catch (Exception ignored) {}
                    try { if (body.containsKey("placarAdversario")) j.setPlacarAdversario(body.get("placarAdversario") == null ? null : Integer.parseInt(body.get("placarAdversario").toString())); } catch (Exception ignored) {}
                    jogoRepository.save(j);
                    return ResponseEntity.ok(toClient(j));
                } else {
                    agendamentoRepository.save(a);
                    Map<String, Object> m = toClient(a);
                    if ("JOGO".equalsIgnoreCase(String.valueOf(body.get("tipo")))) {
                        m.put("tipo", "JOGO");
                        copyIfPresent(body, m, "adversario", "mandante", "placarTime", "placarAdversario");
                    }
                    return ResponseEntity.ok(m);
                }
            }
        } catch (Exception ignored) {}
        body.put("id", id);
        FALLBACK.put(id, new HashMap<>(body));
        return ResponseEntity.ok(body);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> excluir(@PathVariable("id") String id) {
        try { agendamentoRepository.deleteById(Long.parseLong(id)); } catch (Exception ignored) {}
        FALLBACK.remove(id);
        PRESENCAS_FALLBACK.remove(id);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/{id}/minha-presenca")
    public ResponseEntity<Object> responderPresenca(@PathVariable("id") String id, @RequestBody Map<String, Object> body) {
        String statusStr = body.get("status") != null ? body.get("status").toString() : "PENDENTE";
        // best-effort: persistir Presenca para agendamento/atleta de domínio
        try {
            Long lid = Long.parseLong(id);
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            if (auth != null && auth.getPrincipal() instanceof Usuario) {
                Usuario u = (Usuario) auth.getPrincipal();
                Atleta at0 = null;
                if (u.getId() != null) at0 = atletaRepository.findById(u.getId()).orElse(null);
                final Atleta at = at0;
                final Agendamento ag = agendamentoRepository.findById(lid).orElse(null);
                if (at != null && ag != null) {
                    StatusPresenca sp = "CONFIRMADO".equalsIgnoreCase(statusStr) || "PRESENTE".equalsIgnoreCase(statusStr)
                            ? StatusPresenca.PRESENTE
                            : ("JUSTIFICADO".equalsIgnoreCase(statusStr) ? StatusPresenca.JUSTIFICADO
                                : ("AUSENTE".equalsIgnoreCase(statusStr) ? StatusPresenca.AUSENTE : StatusPresenca.PENDENTE));
                    Presenca p = presencaRepository.findByAgendamentoId(lid).stream()
                            .filter(x -> x.getAtleta() != null && x.getAtleta().getId() != null && x.getAtleta().getId().equals(at.getId()))
                            .findFirst().orElseGet(() -> { Presenca np = new Presenca(); np.setAtleta(at); np.setAgendamento(ag); return np; });
                    p.setStatus(sp);
                    p.setDataConfirmacao(java.time.LocalDateTime.now());
                    presencaRepository.save(p);
                }
            }
        } catch (Exception ignored) {}
        Map<String, Object> resp = new HashMap<>();
        resp.put("eventoId", id);
        resp.put("status", statusStr);
        Map<String, Object> fb = FALLBACK.get(id);
        if (fb != null) fb.put("minhaPresenca", statusStr);
        return ResponseEntity.ok(resp);
    }

    @GetMapping("/{id}/presencas")
    public ResponseEntity<List<Map<String, Object>>> presencas(@PathVariable("id") String id) {
        try {
            Long lid = Long.parseLong(id);
            List<Presenca> list = presencaRepository.findByAgendamentoId(lid);
            if (!list.isEmpty() || agendamentoRepository.existsById(lid)) {
                List<Map<String, Object>> out = new ArrayList<>();
                for (Presenca p : list) {
                    Map<String, Object> m = new HashMap<>();
                    if (p.getAtleta() != null) {
                        m.put("atletaId", p.getAtleta().getId() != null ? p.getAtleta().getId().toString() : null);
                        m.put("atletaNome", p.getAtleta().getNome());
                        m.put("numero", p.getAtleta().getCamisa() != null ? p.getAtleta().getCamisa() : 0);
                    }
                    m.put("status", toClientStatus(p.getStatus()));
                    out.add(m);
                }
                return ResponseEntity.ok(out);
            }
        } catch (Exception ignored) {}
        return ResponseEntity.ok(PRESENCAS_FALLBACK.getOrDefault(id, Collections.emptyList()));
    }

    @PostMapping("/{id}/convocacao")
    public ResponseEntity<Map<String, Object>> convocar(@PathVariable("id") String id, @RequestBody Map<String, Object> body) {
        @SuppressWarnings("unchecked")
        List<Object> rawIds = body.get("atletaIds") instanceof List ? (List<Object>) body.get("atletaIds") : Collections.emptyList();
        List<Map<String, Object>> atletas = new ArrayList<>();
        try {
            Long lid = Long.parseLong(id);
            Agendamento ag = agendamentoRepository.findById(lid).orElse(null);
            if (ag != null) {
                for (Object rid : rawIds) {
                    String aid = String.valueOf(rid);
                    Map<String, Object> p = new HashMap<>();
                    p.put("atletaId", aid);
                    p.put("atletaNome", ""); p.put("numero", 0); p.put("status", "PENDENTE");
                    // info do store (contrato do client) como base
                    Map<String, Object> fromStore = atletaStore.get(aid);
                    if (fromStore != null) {
                        if (fromStore.get("nome") != null) p.put("atletaNome", fromStore.get("nome"));
                        if (fromStore.get("numero") != null) p.put("numero", fromStore.get("numero"));
                    }
                    Atleta at = null;
                    try { at = atletaRepository.findById(Long.parseLong(aid)).orElse(null); } catch (Exception ignored) {}
                    if (at == null && fromStore != null && fromStore.get("email") != null) {
                        try {
                            var u = usuarioRepository.findByEmail(fromStore.get("email").toString());
                            if (u != null && u.getId() != null) at = atletaRepository.findById(u.getId()).orElse(null);
                        } catch (Exception ignored) {}
                    }
                    if (at != null) {
                        if (at.getNome() != null) p.put("atletaNome", at.getNome());
                        p.put("numero", at.getCamisa() != null ? at.getCamisa() : p.getOrDefault("numero", 0));
                        p.put("atletaId", at.getId() != null ? at.getId().toString() : aid);
                        final Atleta fat = at;
                        Presenca pres = presencaRepository.findByAgendamentoId(lid).stream()
                                .filter(x -> x.getAtleta() != null && x.getAtleta().getId() != null && x.getAtleta().getId().equals(fat.getId()))
                                .findFirst().orElseGet(() -> { Presenca np = new Presenca(); np.setAtleta(fat); np.setAgendamento(ag); np.setStatus(StatusPresenca.PENDENTE); return np; });
                        if (pres.getId() == null) pres.setStatus(StatusPresenca.PENDENTE);
                        presencaRepository.save(pres);
                        p.put("status", toClientStatus(pres.getStatus()));
                    }
                    atletas.add(p);
                }
                Map<String, Object> resp = new HashMap<>();
                resp.put("eventoId", id);
                resp.put("geradaEm", OffsetDateTime.now().toString());
                resp.put("atletas", atletas);
                return ResponseEntity.ok(resp);
            }
        } catch (Exception ignored) {}
        // fallback
        for (Object rid : rawIds) {
            String aid = String.valueOf(rid);
            Map<String, Object> p = new HashMap<>();
            Map<String, Object> fromStore = atletaStore != null ? atletaStore.get(aid) : null;
            p.put("atletaId", aid);
            p.put("atletaNome", fromStore != null ? fromStore.getOrDefault("nome","") : "");
            p.put("numero", fromStore != null ? fromStore.getOrDefault("numero",0) : 0);
            p.put("status", "PENDENTE");
            atletas.add(p);
        }
        PRESENCAS_FALLBACK.put(id, atletas);
        Map<String, Object> resp = new HashMap<>();
        resp.put("eventoId", id);
        resp.put("geradaEm", OffsetDateTime.now().toString());
        resp.put("atletas", atletas);
        return ResponseEntity.ok(resp);
    }

    private void applyAgendamento(Agendamento a, Map<String, Object> body) {
        a.setTitulo(String.valueOf(body.getOrDefault("foco", body.getOrDefault("titulo", body.getOrDefault("tipo", "Evento")))));
        a.setDescricao(body.get("anotacoes") != null ? body.get("anotacoes").toString() : (body.get("descricao") != null ? body.get("descricao").toString() : ""));
        a.setLocal(String.valueOf(body.getOrDefault("local", "")));
        LocalDateTime ini = parseDateTime(body.get("inicio"));
        LocalDateTime fim = parseDateTime(body.get("fim"));
        a.setDataHoraInicio(ini != null ? ini : LocalDateTime.now());
        a.setDataHoraFim(fim != null ? fim : (ini != null ? ini.plusHours(2) : LocalDateTime.now().plusHours(2)));
    }

    private LocalDateTime parseDateTime(Object v) {
        if (v == null) return null;
        try {
            return OffsetDateTime.parse(v.toString()).toLocalDateTime();
        } catch (Exception e) {
            try { return LocalDateTime.parse(v.toString()); } catch (Exception e2) { return null; }
        }
    }

    private String formatDateTime(LocalDateTime dt) {
        if (dt == null) return null;
        return dt.atZone(ZoneId.systemDefault()).toOffsetDateTime().toString();
    }

    private TipoAgendamento getTipo(String nome) {
        return tipoAgendamentoRepository.findAll().stream()
                .filter(t -> nome.equalsIgnoreCase(t.getNome()))
                .findFirst()
                .orElseGet(() -> {
                    TipoAgendamento t = new TipoAgendamento();
                    t.setNome(nome);
                    return tipoAgendamentoRepository.save(t);
                });
    }

    private Map<String, Object> toClient(Agendamento a) {
        Map<String, Object> m = new HashMap<>();
        m.put("id", a.getId() != null ? a.getId().toString() : null);
        m.put("inicio", formatDateTime(a.getDataHoraInicio()));
        m.put("fim", formatDateTime(a.getDataHoraFim()));
        m.put("local", a.getLocal());
        m.put("anotacoes", a.getDescricao());
        if (a instanceof Treino) {
            Treino t = (Treino) a;
            m.put("tipo", "TREINO");
            m.put("foco", t.getTipoTreino());
        } else if (a instanceof Jogo) {
            Jogo j = (Jogo) a;
            m.put("tipo", "JOGO");
            m.put("adversario", j.getAdversario() != null ? j.getAdversario() : "");
            m.put("mandante", j.getMandante() != null ? j.getMandante() : true);
            m.put("placarTime", j.getPlacarTime());
            m.put("placarAdversario", j.getPlacarAdversario());
        } else {
            m.put("tipo", "JOGO");
            m.put("adversario", "");
            m.put("mandante", true);
        }
        m.put("resumoPresenca", resumo(a.getId()));
        Long atId = currentAtletaId();
        if (atId != null && a.getId() != null) {
            try {
                presencaRepository.findByAgendamentoId(a.getId()).stream()
                        .filter(p -> p.getAtleta() != null && atId.equals(p.getAtleta().getId()))
                        .findFirst().ifPresent(p -> m.put("minhaPresenca", toClientStatus(p.getStatus())));
            } catch (Exception ignored) {}
        }
        return m;
    }

    private Map<String, Object> resumo(Long agendamentoId) {
        Map<String, Object> r = new HashMap<>();
        int convocados = 0, confirmados = 0, ausentes = 0, pendentes = 0;
        if (agendamentoId != null) {
            try {
                List<Presenca> list = presencaRepository.findByAgendamentoId(agendamentoId);
                convocados = list.size();
                for (Presenca p : list) {
                    if (p.getStatus() == StatusPresenca.PRESENTE) confirmados++;
                    else if (p.getStatus() == StatusPresenca.AUSENTE || p.getStatus() == StatusPresenca.JUSTIFICADO) ausentes++;
                    else pendentes++;
                }
            } catch (Exception ignored) {}
        }
        r.put("convocados", convocados);
        r.put("confirmados", confirmados);
        r.put("ausentes", ausentes);
        r.put("pendentes", pendentes);
        return r;
    }

    private String toClientStatus(StatusPresenca s) {
        if (s == null) return "PENDENTE";
        if (s == StatusPresenca.PRESENTE) return "CONFIRMADO";
        if (s == StatusPresenca.AUSENTE || s == StatusPresenca.JUSTIFICADO) return "AUSENTE";
        return "PENDENTE";
    }


    private Long currentAtletaId() {
        try {
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            if (auth != null && auth.getPrincipal() instanceof Usuario) {
                Usuario u = (Usuario) auth.getPrincipal();
                if (u.getId() != null && atletaRepository.findById(u.getId()).isPresent()) return u.getId();
            }
        } catch (Exception ignored) {}
        return null;
    }

    private void copyIfPresent(Map<String, Object> src, Map<String, Object> dst, String... keys) {
        for (String k : keys) if (src.containsKey(k)) dst.put(k, src.get(k));
    }
}
