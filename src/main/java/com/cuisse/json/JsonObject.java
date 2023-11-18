package com.cuisse.json;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * @author Brayan Roman
 */
public final class JsonObject implements JsonValue, Map<String, JsonValue> {

    private final Map<String, JsonValue> values = new HashMap<>();

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
    public String print(int depth) {
        if (values.isEmpty()) {
            return "{}";
        }
        StringBuilder sb = new StringBuilder();
        sb.append('{');
        values.forEach((key, val) -> {
            sb.append('\n');
            sb.repeat(" ", depth + 1).append('"').append(key).append('"').append(':').append(' ');
            sb.append(val.print(depth + 2));
            sb.append(',');
        });
        sb.replace(sb.length() - 1, sb.length(), "");
        sb.append('\n').repeat(" ", depth).append('}');
        return sb.toString();
    }

    @Override
    public String toString() {
        if (values.isEmpty()) {
            return "{}";
        } else {
            StringBuilder sb = new StringBuilder();
            sb.append('{');
            values.forEach((key, val) -> {
                sb.append('"').append(key).append('"').append(':').append(' ');
                sb.append(val.toString());
                sb.append(',').append(' ');
            });
            sb.replace(sb.length() - 2, sb.length(), "");
            sb.append('}');
            return sb.toString();
        }
    }

}
