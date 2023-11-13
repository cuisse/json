package com.cuisse;

public record Integral(long value) implements Element {

    @Override
    public JsonType type() {
        return JsonType.NUMBER;
    }

}
