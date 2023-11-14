package com.cuisse;

/**
 * @author Brayan Roman
 */
public record JsonBoolean(boolean value) implements JsonValue, Comparable<JsonBoolean> {

    @Override
    public JsonType type() {
        return JsonType.BOOLEAN;
    }

    @Override
    public int compareTo(JsonBoolean other) {
        return Boolean.compare(value, other.value());
    }

    @Override
    public String toString() {
        return Boolean.toString(value);
    }

}
