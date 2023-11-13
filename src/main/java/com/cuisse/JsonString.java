package com.cuisse;

public record JsonString(String value) implements Element {

    @Override
    public JsonType type() {
        return JsonType.STRING;
    }

}
