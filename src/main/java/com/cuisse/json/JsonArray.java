package com.cuisse.json;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.function.Predicate;

/**
 * @author Brayan Roman
 */
public class JsonArray implements JsonValue, List<JsonValue> {

    private final List<JsonValue> values = new ArrayList<>();

    @Override
    public JsonType type() {
        return JsonType.ARRAY;
    }

    @Override
    public int size() {
        return values.size();
    }

    @Override
    public boolean isEmpty() {
        return values.isEmpty();
    }

    @Override
    public boolean contains(Object value) {
        return values.contains(value);
    }

    @Override
    public boolean add(JsonValue element) {
        if (element == null) {
            throw new NullPointerException("element == null");
        } else {
            return values.add(element);
        }
    }

    @Override
    public boolean remove(Object value) {
        return values.remove(value);
    }

    @Override
    public boolean containsAll(Collection<?> collection) {
        return new HashSet<>(values).containsAll(collection);
    }

    @Override
    public boolean addAll(Collection<? extends JsonValue> collection) {
        return values.addAll(collection);
    }

    @Override
    public boolean addAll(int index, Collection<? extends JsonValue> collection) {
        return values.addAll(collection);
    }

    @Override
    public boolean removeAll(Collection<?> collection) {
        return values.removeAll(collection);
    }

    @Override
    public boolean retainAll(Collection<?> collection) {
        return values.retainAll(collection);
    }

    @Override
    public void clear() {
        values.clear();
    }

    @Override
    public JsonValue get(int index) {
        return values.get(index);
    }

    public JsonValue find(Predicate<JsonValue> predicate) {
        for (JsonValue value : values) {
            if (predicate.test(value)) {
                return value;
            }
        }
        return null;
    }

    @Override
    public JsonValue set(int index, JsonValue element) {
        return values.set(index, element);
    }

    @Override
    public void add(int index, JsonValue element) {
        values.add(index, element);
    }

    @Override
    public JsonValue remove(int index) {
        return values.remove(index);
    }

    @Override
    public int indexOf(Object value) {
        return values.indexOf(value);
    }

    @Override
    public int lastIndexOf(Object value) {
        return values.lastIndexOf(value);
    }

    @Override
    public ListIterator<JsonValue> listIterator() {
        return values.listIterator();
    }

    @Override
    public ListIterator<JsonValue> listIterator(int index) {
        return values.listIterator(index);
    }

    @Override
    public List<JsonValue> subList(int fromIndex, int toIndex) {
        return values.subList(fromIndex, toIndex);
    }

    @Override
    public Iterator<JsonValue> iterator() {
        return values.iterator();
    }

    @Override
    public Object[] toArray() {
        return values.toArray();
    }

    @Override
    public <T> T[] toArray(T[] target) {
        return values.toArray(target);
    }

    @Override
    public String print(int depth) {
        if (values.isEmpty()) {
            return "[]";
        } else {
            StringBuilder sb = new StringBuilder();
            sb.append('[');
            values.forEach(val -> {
                sb.append('\n');
                sb.repeat(" ", depth + 1).append(val.print(depth + 2));
                sb.append(',');
            });
            sb.replace(sb.length() - 1, sb.length(), "");
            sb.append('\n').repeat(" ", depth).append(']');
            return sb.toString();
        }
    }

    @Override
    public String toString() {
        if (values.isEmpty()) {
            return "[]";
        } else {
            StringBuilder sb = new StringBuilder();
            sb.append('[');
            values.forEach(val -> {
                sb.append(val.toString());
                sb.append(',').append(' ');
            });
            sb.replace(sb.length() - 2, sb.length(), "");
            sb.append(']');
            return sb.toString();
        }
    }

}
