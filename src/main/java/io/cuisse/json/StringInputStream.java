package io.cuisse.json;

import java.io.InputStream;

/**
 * @author Brayan Roman
 */
public class StringInputStream extends InputStream {

    private final String input;
    private int index;

    public StringInputStream(String input) {
        if (input == null) {
            throw new NullPointerException("input == null");
        } else {
            this.input = input;
        }
    }

    public int length() {
        return input.length();
    }

    @Override
    public int read() {
        return index >= input.length() ? -1 : input.charAt(index++);
    }

}
