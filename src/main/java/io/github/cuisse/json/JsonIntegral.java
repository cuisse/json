package io.github.cuisse.json;

/**
 * @author Brayan Roman
 */
public record JsonIntegral(long value) implements JsonValue, Comparable<JsonIntegral> {
    public static final JsonIntegral INTEGRAL_0 = new JsonIntegral(0);
    public static final JsonIntegral INTEGRAL_1 = new JsonIntegral(1);
    public static final JsonIntegral INTEGRAL_2 = new JsonIntegral(2);
    public static final JsonIntegral INTEGRAL_3 = new JsonIntegral(3);
    public static final JsonIntegral INTEGRAL_4 = new JsonIntegral(4);
    public static final JsonIntegral INTEGRAL_5 = new JsonIntegral(5);
    public static final JsonIntegral INTEGRAL_6 = new JsonIntegral(6);
    public static final JsonIntegral INTEGRAL_7 = new JsonIntegral(7);
    public static final JsonIntegral INTEGRAL_8 = new JsonIntegral(8);
    public static final JsonIntegral INTEGRAL_9 = new JsonIntegral(9);

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
