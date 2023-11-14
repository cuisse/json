package com.cuisse;

/**
 * @author Brayan Roman
 */
public record JsonDecimal(double value) implements JsonValue, Comparable<JsonDecimal> {

    @Override
    public JsonType type() {
        return JsonType.NUMBER;
    }

    @Override
    public int compareTo(JsonDecimal other) {
        return Double.compare(value, other.value());
    }

    @Override
    public String toString() {
        return Double.toString(value);
    }

}