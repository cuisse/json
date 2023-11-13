package com.cuisse;

public class ParsingException extends RuntimeException {

    public ParsingException(String message, Exception cause) {
        super(message, cause);
    }

}
