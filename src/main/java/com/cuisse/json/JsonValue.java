package com.cuisse.json;

/**
 * @author Brayan Roman
 */
public interface JsonValue {

    /**
     * Get this value type.
     *
     * @return This value type.
     */
    JsonType type();

    /**
     * Check whether or not this value match the expected type.
     *
     * @param type The expected type.
     * @return     true if the the type match this value, otherwise false.
     */
    default boolean is(JsonType type) {
        return type().equals(type);
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
     * Get this value as a long.
     *
     * @return The long value.
     * @throws ClassCastException If the conversion to long cannot be possible.
     */
    default long integral() {
        return switch (this) {
            case JsonDecimal decimal   -> (long) decimal.value();
            case JsonIntegral integral -> integral.value();
            default                    -> throw new ClassCastException("Cannot cast " + getClass() + " to long.");
        };
    }

    /**
     * Get this value as a double.
     *
     * @return The double value.
     * @throws ClassCastException If the conversion to double cannot be possible.
     */
    default double decimal() {
        return switch (this) {
            case JsonDecimal decimal   -> decimal.value();
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
     * Get a pretty printed string interpretation of this value.
     *
     * @return The pretty printed string.
     */
    default String print() {
        return print(0);
    }

    /**
     * Get a pretty printed string interpretation of this value.
     *
     * @param depth The depth used to format the string.
     * @return      The pretty printed string.
     */
    default String print(int depth) {
        return toString(); // default implementation
    }

}
