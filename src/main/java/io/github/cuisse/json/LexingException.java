package io.github.cuisse.json;

/**
 * @author Brayan Roman
 */
public class LexingException extends RuntimeException {

    public LexingException(String message) {
        super(message);
    }

    public LexingException(String message, Exception cause) {
        super(message, cause);
    }

}
