package com.cuisse;

public record Null() implements Element {

    @Override
    public JsonType type() {
        return JsonType.NULL;
    }

}
