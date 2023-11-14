package com.cuisse.json;

/**
 * @author Brayan Roman
 */
public record JsonIntegral(long value) implements JsonValue, Comparable<JsonIntegral> {

    @Override
    public JsonType type() {
        return JsonType.NUMBER;
    }

    @Override
    public int compareTo(JsonIntegral other) {
        return Long.compare(value, other.value());
    }

    @Override
    public String toString() {
        return Long.toString(value);
    }

}
