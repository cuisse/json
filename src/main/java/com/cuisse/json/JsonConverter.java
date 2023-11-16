package com.cuisse.json;

/**
 * @author Brayan Roman
 */
public interface JsonConverter<T> {

    /**
     * Get the type this converter is targeting.
     *
     * @return The target type.
     */
    JsonType type();

    /**
     * Convert this converter target into a JsonValue representation.
     *
     * @param value The object to convert.
     * @return      The JsonValue representation of the specified object.
     */
    JsonValue convert(T value);

    /**
     * Convert a JsonValue into this converter target.
     *
     * @param value The JsonValue to convert.
     * @return      The target object build out from the JsonValue.
     */
    T convert(JsonValue value);

}
