package io.github.cuisse.json;

/**
 * Base class for all exceptions thrown by the JSON parser.
 * 
 * @author Brayan Roman
 */
public abstract class JsonException extends RuntimeException {
    
    public JsonException(String message) {
        super(message);
    }

    public JsonException(String message, Exception cause) {
        super(message, cause);
    }

    public JsonException(Exception cause) {
        super(cause);
    }

}
