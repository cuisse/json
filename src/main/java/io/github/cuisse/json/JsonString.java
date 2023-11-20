package io.github.cuisse.json;

/**
 * @author Brayan Roman
 */
public record JsonString(String value) implements JsonValue {
    public static final JsonString EMPTY = new JsonString("");

    @Override
    public JsonType type() {
        return JsonType.STRING;
    }

    @Override
    public String toString() {
        return "\"" + value + "\"";
    }

}
