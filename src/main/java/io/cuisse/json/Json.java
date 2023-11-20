package io.cuisse.json;

import java.io.InputStream;

/**
 * @author Brayan Roman
 */
public final class Json {

    /**
     * Parses the input into a JsonValue.
     *
     * @param input The JSON input.
     * @return      The parsed value.
     */
    public static JsonValue parse(String input) {
        return parse(
                new StringInputStream(input)
        );
    }

    /**
     * Parses the input into a JsonValue.
     *
     * @param input The JSON input.
     * @return      The parsed value.
     */
    public static JsonValue parse(InputStream input) {
        Parser parser = new Parser(input);
        return parser.parse();
    }

    /**
     * Parses the input into the specified type.
     *
     * @param input  The JSON input.
     * @param target The target class.
     * @return       The parsed value.
     */
    public static<T> T parse(String input, Class<T> target) {
        return parse(new StringInputStream(input), target);
    }

    /**
     * Parses the input into the specified type.
     *
     * @param input  The JSON input.
     * @param target The target class.
     * @return       The parsed value.
     */
    public static<T> T parse(InputStream input, Class<T> target) {
        Parser parser = new Parser(input);
        return parser.parse(target);
    }

    /**
     * Converts an object into a json string.
     *
     * @param object The object to convert.
     * @param pretty Specifies if the output should be pretty printed or no.
     * @return       The parsed value.
     */
    @SuppressWarnings("unchecked")
    public static<T> String json(T object, boolean pretty) {
        if (object == null) {
            throw new NullPointerException("object == null");
        }
        if (object instanceof JsonValue json) {
            if (json.is(JsonType.OBJECT) || json.is(JsonType.ARRAY)) {
                return pretty ? json.print() : json.toString();
            } else {
                throw new IllegalArgumentException("Could not convert " + object.getClass() + " into a JSON string.");
            }
        } else {
            var converter = (JsonConverter<T>) JsonConverters.instance().find(object.getClass());
            if (converter != null) {
                return json(converter.convert(object), pretty);
            } else {
                throw new IllegalArgumentException("Cannot convert " + object.getClass() + " into a JSON string because it is missing a JsonConverter.");
            }
        }
    }

    private Json() { }

}
