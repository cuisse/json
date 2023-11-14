package com.cuisse.json;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;

/**
 * A simple json lexer, also known as 'tokenizer'.
 *
 * @author Brayan Roman
 */
public class Lexer {

    private Reader reader;
    private int line;
    private int offset;
    private char current;
    private char previous;
    private Token token;
    private boolean eof;
    private char[] buffer;
    private int index;
    private int total;

    private static final String TRUE  = "true";
    private static final String FALSE = "false";
    private static final String NULL  = "null";

    public Lexer(InputStream stream) {
        if (stream == null) {
            throw new NullPointerException("stream == null");
        } else {
            this.reader  = new InputStreamReader(stream);
            this.buffer  = new char[stream instanceof StringInputStream input ? input.length() : 1024];
            this.current = (char) readChar();
        }
    }

    /**
     * @return The current line of this lexer.
     */
    public int line() {
        return line;
    }

    /**
     * @return The current offset of this lexer.
     */
    public int offset() {
        return offset;
    }

    /**
     * Peek the current token without consuming it.
     *
     * @return The current token.
     */
    public Token peek() {
        if (token == null) {
            token = next();
        }
        return token;
    }

    /**
     * Consumes the current token and replaces it with the next one.
     *
     * @return The consumed token.
     */
    public Token consume() {
        Token consumed = token;
        token = next();
        return consumed;
    }

    private Token next() {
        while (current == ' ' || current == '\t' || current == '\r' || current == '\n') {
            consumeChar();
        }
        if (eof) {
            return new Token(TokenKind.EOF, "");
        }
        return switch (current) {
            case '{' -> create(TokenKind.OBJECT_OPEN);
            case '}' -> create(TokenKind.OBJECT_CLOSE);
            case ':' -> create(TokenKind.COLON);
            case '[' -> create(TokenKind.ARRAY_OPEN);
            case ']' -> create(TokenKind.ARRAY_CLOSE);
            case ',' -> create(TokenKind.COMMA);
            case 't' -> create(TokenKind.TRUE, TRUE);
            case 'f' -> create(TokenKind.FALSE, FALSE);
            case 'n' -> create(TokenKind.NULL, NULL);
            case '"' -> createString();
            case '-', '0', '1', '2', '3', '4', '5', '6', '7', '8', '9' -> createNumber();
            default  -> throw new LexingException("Invalid token '" + current + "' at " + line + ":" + offset);
        };
    }

    private Token create(TokenKind kind) {
        try {
            return new Token(kind, Character.toString(current));
        } finally {
            consumeChar();
        }
    }

    private Token create(TokenKind kind, String expected) {
        consumeChar();
        for (int i = 1; i < expected.length(); i++) {
            if (current != expected.charAt(i)) {
                throw new LexingException("Error while reading token '" + kind + "', unexpected value '" + (eof ? "EOF" : Character.toString(current)) + "' at " + line + ":" + offset);
            }
            consumeChar();
        }
        return new Token(kind, expected);
    }

    private Token createString() {
        consumeChar();
        StringBuilder value = new StringBuilder(16);
        while (current != '"') {
            if (eof) {
                throw new LexingException("Got EOF while reading string.");
            } else {
                if (Character.isISOControl(current)) {
                    throw new LexingException("Found invalid iso control value at " + line + ":" + offset + ".");
                }
                if (current == '\\') {
                    consumeChar();
                    if (current == 'n' || current == 'b' || current == 'f' || current == 'r' || current == 't' ||current == '/' || current == '\\' || current == 'u') {
                        value.append('\\').append(current);
                        consumeChar();
                    }
                }
                value.append(current);
                consumeChar();
            }
        }
        consumeChar();
        return new Token(TokenKind.STRING, value.toString());
    }

    private Token createNumber() {
        StringBuilder value = new StringBuilder(16);
        boolean integral = true;
        boolean exponent = false;
        while (Character.isDigit(current) || current == '-' || current == 'e' || current == 'E' || current == '+' || current == '.') {
            if (current == 'e' || current == 'E' || current == '+' || current == '.') {
                if (integral) {
                    integral = false;
                }
                if (current == '.' || current == 'e' || current == 'E') {
                    if (false == Character.isDigit(previous)) {
                        throw new LexingException("Unexpected value '" + current + "' at " + line + ":" + offset + ", expecting digit (0-9). ");
                    }
                    if (current == 'e' || current == 'E') {
                        if (exponent) {
                            throw new LexingException("Invalid exponent declaration at " + line + ":" + offset);
                        } else {
                            exponent = true;
                        }
                    }
                }
                if (current == '+') {
                    if (previous != 'e' && previous != 'E') {
                        throw new LexingException("Unexpected value '" + current + "' at " + line + ":" + offset + ", expected 'e' or 'E'. ");
                    }
                }
            }
            value.append(current);
            previous = current;
            consumeChar();
        }
        if (false == Character.isDigit(previous)) {
            throw new LexingException("Number did not end with a digit at " + line + ":" + offset);
        }
        return new Token(integral ? TokenKind.INTEGRAL : TokenKind.DECIMAL, value.toString());
    }

    private void consumeChar() {
        if (current == '\n') {
            line  += 1;
            offset = 0;
        } else {
            offset += 1;
        }
        int c = readChar();
        if (c == -1) {
            eof = true;
        }
        previous = current;
        current  = (char) c;
    }

    private int readChar() {
        try {
            if (index >= total) {
                total = 0;
            }
            if (total == 0) {
                total = reader.read(buffer, 0, buffer.length);
                if (total > 0) {
                    index = 0;
                }
            }
            return total == -1 ? -1 : buffer[index++];
        } catch (IOException error) {
            throw new LexingException("Error while trying to read from the input. ", error);
        }
    }

    /**
     * Clean the resources hold by this lexer.
     */
    public void dispose() {
        if (reader != null) {
            try {
                reader.close();
            } catch (IOException ignored) { } finally {
                reader = null;
                buffer = null;
            }
        }
    }

}
