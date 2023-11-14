package com.cuisse.json;

/**
 * @author Brayan Roman
 */
public record Null() implements JsonValue {

    @Override
    public JsonType type() {
        return JsonType.NULL;
    }

    @Override
    public String toString() {
        return "null";
    }

}
