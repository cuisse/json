package com.cuisse;

import java.util.LinkedList;
import java.util.List;

public class JsonArray implements Element {

    private final List<Element> elements = new LinkedList<>();

    public List<Element> elements() {
        return elements;
    }

    @Override
    public JsonType type() {
        return JsonType.ARRAY;
    }

    public void add(Element element) {
        this.elements.add(element);
    }

    @Override
    public String toString() {
        return elements.toString();
    }
}
