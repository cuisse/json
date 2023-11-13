package com.cuisse;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

public class JsonObject implements Element {

    private final Map<String, Element> elements = new LinkedHashMap<>();

    public Map<String, Element> elements() {
        return elements;
    }

    @Override
    public JsonType type() {
        return JsonType.OBJECT;
    }

    public void put(String key, Element element) {
        elements.put(key, element);
    }

    public boolean contains(String key) {
        return elements.containsKey(key);
    }

    @Override
    public String toString() {
        return elements.toString();
    }
}
