package com.cuisse;

import java.io.IOException;
import java.io.InputStream;

public class StringInputStream extends InputStream {

    private final String input;
    private int index;

    public StringInputStream(String input) {
        if (input == null) {
            throw new NullPointerException();
        } else {
            this.input = input;
        }
    }

    @Override
    public int read() {
        return index >= input.length() ? -1 : input.charAt(index++);
    }

}
