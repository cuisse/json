package io.cuisse.json;

/**
 * @author Brayan Roman
 */
public record JsonBoolean(boolean value) implements JsonValue, Comparable<JsonBoolean> {
    public static final JsonBoolean TRUE  = new JsonBoolean(true);
    public static final JsonBoolean FALSE = new JsonBoolean(false);

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
