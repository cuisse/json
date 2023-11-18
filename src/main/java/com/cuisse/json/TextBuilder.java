package com.cuisse.json;

/**
 * @author Brayan Roman
 */
public final class TextBuilder {

    private int offset;
    private char[] buffer;

    public TextBuilder(int size) {
        if (size <= 0) {
            throw new IllegalArgumentException("size <= 0");
        } else {
            this.buffer = new char[size];
        }
    }

    /**
     * Returns the current length of the text.
     *
     * @return The length of the text.
     */
    public int length() {
        return offset;
    }

    /**
     * Adds a new character into the buffer, increasing the offset by 1.
     *
     * @param value The character to add.
     * @return      The current instance.
     */
    public TextBuilder add(char value) {
        if (offset >= buffer.length) {
            resize();
        }
        buffer[offset++] = value;
        return this;
    }

    /**
     * Sets the current offset to 0.
     */
    public void reset() {
        offset = 0;
    }

    @Override
    public String toString() {
        return new String(buffer, 0, offset);
    }

    private void resize() {
        var temp = new char[computeNewLength(buffer.length + 1)];
        System.arraycopy(buffer, 0, temp, 0, buffer.length);
        buffer = temp;
    }

    private int computeNewLength(int value) {
        int highestOneBit = Integer.highestOneBit(value);
        if (value == highestOneBit) {
            return value;
        }
        return highestOneBit << 1;
    }

}
