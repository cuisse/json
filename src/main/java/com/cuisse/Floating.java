package com.cuisse;

public record Floating(double value) implements Element {

    @Override
    public JsonType type() {
        return JsonType.NUMBER;
    }

}
