package com.cuisse.json;

/**
 * @author Brayan Roman
 */
public record JsonNull() implements JsonValue {
    public static final JsonNull NULL = new JsonNull();

    @Override
    public JsonType type() {
        return JsonType.NULL;
    }

    @Override
    public String toString() {
        return "null";
    }

}
