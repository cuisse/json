package com.cuisse;

/**
 * @author Brayan Roman
 */
public record JsonString(String value) implements JsonValue {

    @Override
    public JsonType type() {
        return JsonType.STRING;
    }

    @Override
    public String toString() {
        return "\"" + value + "\"";
    }

}
