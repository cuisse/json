package io.github.cuisse.json;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.function.Supplier;

/**
 * @author Brayan Roman
 */
public final class JsonObject implements JsonValue, Map<String, JsonValue> {

    private final Map<String, JsonValue> values;

    public JsonObject() {
        this.values = new HashMap<>();
    }

    public JsonObject(Map<String, JsonValue> values) {
        this.values = values;
    }

    @Override
    public JsonType type() {
        return JsonType.OBJECT;
    }

    @Override
    public int size() {
        return values.size();
    }

    @Override
    public boolean isEmpty() {
        return values.isEmpty();
    }

    @Override
    public boolean containsKey(Object key) {
        return values.containsKey(key);
    }

    @Override
    public boolean containsValue(Object value) {
        return values.containsValue(value);
    }

    @Override
    public JsonValue get(Object key) {
        return values.get(key);
    }

    @Override
    public JsonValue getOrDefault(Object key, JsonValue defaultValue) {
        return values.getOrDefault(key, defaultValue);
    }

    public JsonValue getOrDefault(Object key, Supplier<JsonValue> supplier) {
        return values.getOrDefault(key, supplier.get());
    }

    @Override
    public JsonValue put(String key, JsonValue value) {
        return values.put(key, value);
    }

    @Override
    public JsonValue remove(Object key) {
        return values.remove(key);
    }

    @Override
    public void putAll(Map<? extends String, ? extends JsonValue> map) {
        values.putAll(map);
    }

    @Override
    public void clear() {
        values.clear();
    }

    @Override
    public Set<String> keySet() {
        return values.keySet();
    }

    @Override
    public Collection<JsonValue> values() {
        return values.values();
    }

    @Override
    public Set<Entry<String, JsonValue>> entrySet() {
        return values.entrySet();
    }

    @Override
    public String toString() {
        return SimpleJsonPrinter.pretty(this);
    }

}
