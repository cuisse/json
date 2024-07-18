package io.github.cuisse.json;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Brayan Roman
 */
public final class JsonConverterRegistry {

    private static volatile JsonConverterRegistry instance;

    /**
     * Get this registry instance.
     *
     * @return The JsonConverterRegistry instance.
     */
    public static JsonConverterRegistry instance() {
        if (instance == null) {
            instance = new JsonConverterRegistry(new HashMap<>()); // lazy initialization
        }
        return instance;
    }

    public static JsonConverterRegistry instance(JsonConverterRegistry registry) {
        if (registry == null) {
            throw new NullPointerException("registry == null");
        }
        instance = registry;
        return instance;
    }

    private final Map<Class<?>, JsonConverter<?>> registry;

    public JsonConverterRegistry(Map<Class<?>, JsonConverter<?>> registry) {
        if (registry == null) {
            throw new NullPointerException("registry == null");
        }
        this.registry = registry;
    }

    /**
     * Registers a new JsonConverter into this registry.
     *
     * @param target    The target class.
     * @param converter The converter to add.
     */
    public void register(Class<?> target, JsonConverter<?> converter) {
        registry.put(target, converter);
    }

    /**
     * Find a specified converter.
     *
     * @param target The target class to lookup.
     * @return       A JsonConverter instance if found, otherwise null.
     */
    public JsonConverter<?> find(Class<?> target) {
        return registry.get(target);
    }

    /**
     * Delete a JsonConverter from the registry.
     *
     * @param target The target class to delete.
     * @return       True if any converter was deleted, otherwise false.
     */
    public boolean delete(Class<?> target) {
        return registry.remove(target) != null;
    }

}
