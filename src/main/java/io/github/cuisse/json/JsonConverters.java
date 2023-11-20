package io.github.cuisse.json;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Brayan Roman
 */
public final class JsonConverters {

    private static volatile JsonConverters instance;

    /**
     * Get this registry instance.
     *
     * @return The JsonConverters instance.
     */
    public static JsonConverters instance() {
        if (instance == null) {
            instance = new JsonConverters(); // lazy initialization
        }
        return instance;
    }

    private final Map<Class<?>, JsonConverter<?>> converters = new HashMap<>();

    /**
     * Registers a new JsonConverter into this registry.
     *
     * @param target    The target class.
     * @param converter The converter to add.
     */
    public void register(Class<?> target, JsonConverter<?> converter) {
        converters.put(target, converter);
    }

    /**
     * Find a specified converter.
     *
     * @param target The target class to lookup.
     * @return       A JsonConverter instance if found, otherwise null.
     */
    public JsonConverter<?> find(Class<?> target) {
        return converters.get(target);
    }

    /**
     * Delete a JsonConverter from the registry.
     *
     * @param target The target class to delete.
     * @return       True if any converter was deleted, otherwise false.
     */
    public boolean delete(Class<?> target) {
        return converters.remove(target) != null;
    }

    private JsonConverters() { }

}
