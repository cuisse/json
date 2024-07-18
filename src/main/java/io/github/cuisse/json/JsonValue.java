package io.github.cuisse.json;

/**
 * @author Brayan Roman
 */
public sealed interface JsonValue permits JsonArray, JsonBoolean, JsonDecimal, JsonIntegral, JsonObject, JsonNull, JsonString {

    /**
     * Create a JsonValue from an object.
     *
     * @param object The object to convert.
     * @return       The JsonValue instance.
     */
    static<T> JsonValue from(T object) {
        return from(object, JsonConverterRegistry.instance());
    }
    
    /**
     * Create a JsonValue from an object.
     *
     * @param object   The object to convert.
     * @param registry The registry to use.
     * @return         The JsonValue instance.
     */
    @SuppressWarnings("unchecked")
    static<T> JsonValue from(T object, JsonConverterRegistry registry) {
        if (object == null) {
            return JsonNull.NULL;
        } else {
            if (object instanceof JsonValue value) {
                return value;
            } else {
                var converter = (JsonConverter<T>) registry.find(object.getClass());
                if (converter != null) {
                    return converter.convert(object);
                } else {
                    throw new UnsupportedOperationException("Failed to find a converter for " + object.getClass());
                }
            }
        }
    }

    /**
     * Get this value type.
     *
     * @return This value type.
     */
    JsonType type();

    /**
     * Check whether this value match the expected type.
     *
     * @param type The expected type.
     * @return     true if the type match this value, otherwise false.
     */
    default boolean is(JsonType type) {
        return type().equals(type);
    }

    /**
     * Check whether this value match the expected type.
     *
     * @param type The expected type.
     * @return     true if the type match this value, otherwise false.
     */
    default boolean is(Class<?> type) {
        return type.isAssignableFrom(this.getClass());
    }

    /**
     * Check whether this value is valid.
     *
     * @return true if this value is not a JsonType.NULL, otherwise false.
     */
    default boolean valid() {
        return is(JsonType.NULL) == false;
    }

    /**
     * Get this value as a JsonArray.
     *
     * @return The JsonArray value.
     * @throws ClassCastException If the conversion to JsonArray cannot be possible.
     */
    default JsonArray array() {
        return (JsonArray) this;
    }

    /**
     * Get this value as a JsonObject.
     *
     * @return The JsonObject value.
     * @throws ClassCastException If the conversion to JsonObject cannot be possible.
     */
    default JsonObject object() {
        return (JsonObject) this;
    }

    /**
     * Get this value as a string.
     *
     * @return The string value. If this value if not a JsonString instance, the method 'toString' will be returned instead.
     */
    default String string() {
        return (this instanceof JsonString string) ? string.value() : toString();
    }

    /**
     * Get this value as an integer.
     *
     * @return The integer value.
     * @throws ClassCastException If the conversion to integer cannot be possible.
     */
    default int integral32() {
        return (int) integral64();
    }

    /**
     * Get this value as a long.
     *
     * @return The long value.
     * @throws ClassCastException If the conversion to long cannot be possible.
     */
    default long integral64() {
        return switch (this) {
            case JsonDecimal  decimal  -> (long) decimal.value();
            case JsonIntegral integral -> integral.value();
            default                    -> throw new ClassCastException("Cannot cast " + getClass() + " to long.");
        };
    }

    /**
     * Get this value as a float.
     *
     * @return The float value.
     * @throws ClassCastException If the conversion to float cannot be possible.
     */
    default float decimal32() {
        return (float) decimal64();
    }

    /**
     * Get this value as a double.
     *
     * @return The double value.
     * @throws ClassCastException If the conversion to double cannot be possible.
     */
    default double decimal64() {
        return switch (this) {
            case JsonDecimal  decimal  -> decimal.value();
            case JsonIntegral integral -> (double) integral.value();
            default                    -> throw new ClassCastException("Cannot cast " + getClass() + " to double.");
        };
    }

    /**
     * Get this value as a boolean.
     *
     * @return The boolean value.
     * @throws ClassCastException If the conversion to boolean cannot be possible.
     */
    default boolean bool() {
        return switch (this) {
            case JsonBoolean  bool     -> bool.value();
            case JsonDecimal  decimal  -> Double.compare(decimal.value(), 1) == 0;
            case JsonIntegral integral -> integral.value() == 1;
            default                    -> throw new ClassCastException("Cannot cast " + getClass() + " to boolean.");
        };
    }

    /**
     * Get this value as a null.
     * 
     * @param type The target type.
     * @return     The null value.
     * @throws ClassCastException If the conversion to null cannot be possible.
     */
    default<T> T as(Class<T> type) {
        return as(type, JsonConverterRegistry.instance());
    }

    /**
     * Get this value as a null.
     * 
     * @param type     The target type.
     * @param registry The registry to use.
     * @return         The null value.
     * @throws ClassCastException If the conversion to null cannot be possible.
     */
    @SuppressWarnings("unchecked")
    default<T> T as(Class<T> type, JsonConverterRegistry registry) {
        var converter = registry.find(type);
        if (converter != null) {
            if (converter.type() == type()) {
                return (T) converter.convert(this);
            } else {
                throw new UnsupportedOperationException("Cannot convert " + getClass() + " to " + type);
            }
        } else {
            throw new UnsupportedOperationException("Failed to find a converter for " + type);
        }
    }

}
