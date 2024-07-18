package io.github.cuisse.json;

/**
 * Represents an exception that occurs during the lexing of a JSON string.
 * 
 * @author Brayan Roman
 */
public class LexingException extends JsonException {

    public LexingException(String message) {
        super(message);
    }

    public LexingException(String message, Exception cause) {
        super(message, cause);
    }

}
