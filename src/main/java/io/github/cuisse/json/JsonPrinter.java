package io.github.cuisse.json;

/**
 * A JSON printer.
 * 
 * @author Brayan Roman
 */
public interface JsonPrinter {

    /**
     * Prints the JSON value.
     * 
     * @param value The JSON value.
     * @return      The JSON string.
     */
    default String print(JsonValue value) {
        return print(value, 0);
    }
    
    /**
     * Prints the JSON value.
     * 
     * @param value  The JSON value.
     * @param indent The indentation level.
     * @return       The JSON string.
     */
    String print(JsonValue value, int indent);

}
