package io.github.cuisse.json;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;

/**
 * Represents a JSON input stream reader.
 * 
 * @author Brayan Roman
 */
public class JsonInputStreamReader implements JsonReader {

    private Reader reader;
    private char[] buffer = new char[1024];
    private int index;
    private int total;
    private boolean eof;

    public JsonInputStreamReader(InputStream input) {
        this.reader = new InputStreamReader(input);
    }

    @Override
    public boolean eof() {
        return eof;
    }

    @Override
    public char read() {
        try {
            if (total <= index) {
                total = reader.read(buffer, 0, buffer.length);
                if (total != -1) {
                    index = 0;
                } else {
                    eof = true;
                }
            }
            return eof ? (char) -1 : buffer[index++];
        } catch (Exception error) {
            throw new JsonReaderException(error);
        }
    }

    @Override
    public void dispose() {
        try {
            reader.close();
        } catch (IOException error) {
            throw new JsonReaderException(error);
        } finally {
            reader = null;
            buffer = null;
        }
    }

}
