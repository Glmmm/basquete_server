package basquete.assis.features.atleta;

import org.springframework.stereotype.Component;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class AtletaStore {
    private final Map<String, Map<String, Object>> store = new ConcurrentHashMap<>();

    public Map<String, Map<String, Object>> all() { return store; }
    public Collection<Map<String, Object>> values() { return store.values(); }
    public Map<String, Object> get(String id) { return store.get(id); }
    public void put(String id, Map<String, Object> v) { store.put(id, v); }
    public void remove(String id) { store.remove(id); }
}
