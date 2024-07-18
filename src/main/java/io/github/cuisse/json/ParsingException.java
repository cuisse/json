package io.github.cuisse.json;

/**
 * @author Brayan Roman
 */
public class ParsingException extends JsonException {

    public ParsingException(String message) {
        super(message);
    }

    public ParsingException(String message, Exception cause) {
        super(message, cause);
    }

}
