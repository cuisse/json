package io.github.cuisse.json;

/**
 * Represents an exception that occurs during the reading of a JSON value.
 * 
 * @author Brayan Roman
 */
public class JsonReaderException extends JsonException {
    
    public JsonReaderException(String message) {
        super(message);
    }

    public JsonReaderException(String message, Exception cause) {
        super(message, cause);
    }

    public JsonReaderException(Exception cause) {
        super(cause);
    }

}
