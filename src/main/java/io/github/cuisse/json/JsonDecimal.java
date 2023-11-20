package io.github.cuisse.json;

/**
 * @author Brayan Roman
 */
public record JsonDecimal(double value) implements JsonValue, Comparable<JsonDecimal> {
    public static final JsonDecimal ZERO = new JsonDecimal(0);

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
