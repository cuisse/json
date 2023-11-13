package com.cuisse;

public record Boolean(boolean value) implements Element {

    @Override
    public JsonType type() {
        return JsonType.BOOLEAN;
    }

}
