package io.github.cuisse.json;

/**
 * Represents a JSON string reader.
 * 
 * @author Brayan Roman
 */
public class JsonStringReader implements JsonReader {

    private char[] input;
    private int index;

    public JsonStringReader(String input) {
        if (input == null) {
            throw new NullPointerException("input == null");
        } else {
            this.input = input.toCharArray();
        }
    }

    @Override
    public boolean eof() {
        return index >= input.length;
    }

    @Override
    public char read() {
        if (index >= input.length) {
            return (char) -1;
        } else {
            return input[index++];
        }
    }

    @Override
    public void dispose() {
        input = null;
        index = -1;
    }
    
}
