package com.cuisse.json;

/**
 * @author Brayan Roman
 */
public class ParsingException extends RuntimeException {

    public ParsingException(String message) {
        super(message);
    }

    public ParsingException(String message, Exception cause) {
        super(message, cause);
    }

}
