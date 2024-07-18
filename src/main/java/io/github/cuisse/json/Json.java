package io.github.cuisse.json;

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
        return parse(new JsonStringReader(input), JsonOptions.NONE);
    }

    /**
     * Parses the input into a JsonValue.
     *
     * @param input   The JSON input.
     * @param options The JSON options.
     * @return        The parsed value.
     */
    public static JsonValue parse(String input, JsonOptions options) {
        return parse(new JsonStringReader(input), options);
    }

    /**
     * Parses the input into a JsonValue.
     *
     * @param input    The JSON input.
     * @param options  The JSON options.
     * @param registry The JSON registry.
     * @return         The parsed value.
     */
    public static JsonValue parse(String input, JsonOptions options, JsonConverterRegistry registry) {
        return parse(new JsonStringReader(input), options, registry);
    }

    /**
     * Parses the input into a JsonValue.
     *
     * @param input The JSON input.
     * @return      The parsed value.
     */
    public static JsonValue parse(InputStream input) {
        return parse(new JsonInputStreamReader(input), JsonOptions.NONE);
    }

    /**
     * Parses the input into a JsonValue.
     *
     * @param input   The JSON input.
     * @param options The JSON options.
     * @return        The parsed value.
     */
    public static JsonValue parse(InputStream input, JsonOptions options) {
        return parse(new JsonInputStreamReader(input), options);
    }

    /**
     * Parses the input into a JsonValue.
     *
     * @param input    The JSON input.
     * @param options  The JSON options.
     * @param registry The JSON registry.
     * @return         The parsed value.
     */
    public static JsonValue parse(InputStream input, JsonOptions options, JsonConverterRegistry registry) {
        return parse(new JsonInputStreamReader(input), options, registry);
    }

    /**
     * Parses the input into a JsonValue.
     *
     * @param input   The JSON input.
     * @param options The JSON options.
     * @return        The parsed value.
     */
    public static JsonValue parse(JsonReader input, JsonOptions options) {
        return parse(input, options, JsonConverterRegistry.instance());
    }

    /**
     * Parses the input into a JsonValue.
     *
     * @param input    The JSON input.
     * @param options  The JSON options.
     * @param registry The JSON registry.
     * @return         The parsed value.
     */
    public static JsonValue parse(JsonReader input, JsonOptions options, JsonConverterRegistry registry) {
        Parser parser = Parser.create();
        parser.initialize(input, options, registry);
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
        return parse(new JsonStringReader(input), target, JsonOptions.NONE);
    }

    /**
     * Parses the input into the specified type.
     *
     * @param input   The JSON input.
     * @param target  The target class.
     * @param options The JSON options.
     * @return        The parsed value.
     */
    public static<T> T parse(String input, Class<T> target, JsonOptions options) {
        return parse(new JsonStringReader(input), target, options, JsonConverterRegistry.instance());
    }

    /**
     * Parses the input into the specified type.
     *
     * @param input    The JSON input.
     * @param target   The target class.
     * @param options  The JSON options.
     * @param registry The JSON registry.
     * @return         The parsed value.
     */
    public static<T> T parse(String input, Class<T> target, JsonOptions options, JsonConverterRegistry registry) {
        return parse(new JsonStringReader(input), target, options, registry);
    }

    /**
     * Parses the input into the specified type.
     *
     * @param input  The JSON input.
     * @param target The target class.
     * @return       The parsed value.
     */
    public static<T> T parse(InputStream input, Class<T> target) {
        return parse(new JsonInputStreamReader(input), target, JsonOptions.NONE);
    }

    /**
     * Parses the input into the specified type.
     *
     * @param input   The JSON input.
     * @param target  The target class.
     * @param options The JSON options.
     * @return        The parsed value.
     */
    public static<T> T parse(InputStream input, Class<T> target, JsonOptions options) {
        return parse(new JsonInputStreamReader(input), target, options, JsonConverterRegistry.instance());
    }

    /**
     * Parses the input into the specified type.
     *
     * @param input    The JSON input.
     * @param target   The target class.
     * @param options  The JSON options.
     * @param registry The JSON registry.
     * @return         The parsed value.
     */
    public static<T> T parse(InputStream input, Class<T> target, JsonOptions options, JsonConverterRegistry registry) {
        return parse(new JsonInputStreamReader(input), target, options, registry);
    }

    /**
     * Parses the input into the specified type.
     *
     * @param input   The JSON input.
     * @param target  The target class.
     * @param options The JSON options.
     * @return        The parsed value.
     */
    public static<T> T parse(JsonReader input, Class<T> target, JsonOptions options) {
        return parse(input, target, options, JsonConverterRegistry.instance());
    }

    /**
     * Parses the input into the specified type.
     *
     * @param input    The JSON input.
     * @param target   The target class.
     * @param options  The JSON options.
     * @param registry The JSON registry.
     * @return         The parsed value.
     */
    public static<T> T parse(JsonReader input, Class<T> target, JsonOptions options, JsonConverterRegistry registry) {
        Parser parser = Parser.create();
        parser.initialize(input, options, registry);
        return parser.parse(target);
    }

    /**
     * Converts an object into a json string.
     *
     * @param object The object to convert.
     * @param pretty Specifies if the output should be pretty printed or no.
     * @return       The parsed value.
     */
    public static<T> String stringify(T object, boolean pretty) {
        return pretty ? SimpleJsonPrinter.pretty(JsonValue.from(object)) : SimpleJsonPrinter.minified(JsonValue.from(object));
    }

    /**
     * Converts an object into a json string.
     *
     * @param object   The object to convert.
     * @param pretty   Specifies if the output should be pretty printed or no.
     * @param registry The JSON registry.
     * @return         The parsed value.
     */
    public static<T> String stringify(T object, boolean pretty, JsonConverterRegistry registry) {
        return pretty ? SimpleJsonPrinter.pretty(JsonValue.from(object, registry)) : SimpleJsonPrinter.minified(JsonValue.from(object, registry));
    }

    private Json() { }

}
