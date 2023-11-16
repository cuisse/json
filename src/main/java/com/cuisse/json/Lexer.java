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
    private int line = 1;
    private int offset;
    private char current;
    private char previous;
    private Token token;
    private boolean eof;
    private char[] buffer;
    private int index;
    private int total;
    private TextBuilder builder;

    public Lexer(InputStream stream) {
        if (stream == null) {
            throw new NullPointerException("stream == null");
        } else {
            this.reader  = new InputStreamReader(stream);
            this.buffer  = new char[stream instanceof StringInputStream input ? input.length() : 1024];
            this.builder = new TextBuilder(512);
            this.current = readChar();
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
     * Get the current token without consuming it.
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
        while (current == ' ' || current == '\n' || current == '\t' || current == '\r') {
            consumeChar();
        }
        if (eof) {
            return Token.EOF;
        }
        if (current == '-' || current >= '0' && current <= '9') {
            return consumeNumber();
        }
        return switch (current) {
            case '{' -> consume(Token.OBJECT_OPEN);
            case '"' -> consumeString();
            case ':' -> consume(Token.COLON);
            case '[' -> consume(Token.ARRAY_OPEN);
            case ',' -> consume(Token.COMMA);
            case 't' -> consumeTrue();
            case 'f' -> consumeFalse();
            case 'n' -> consumeNull();
            case '}' -> consume(Token.OBJECT_CLOSE);
            case ']' -> consume(Token.ARRAY_CLOSE);
            default  -> throw new LexingException("Invalid token '" + current + "' at " + line + ":" + offset);
        };
    }

    private Token consume(Token token) {
        try {
            return token;
        } finally {
            consumeChar();
        }
    }

    private Token consumeTrue() {
        consumeChar(); // t
        expect(TokenKind.TRUE, 'r');
        expect(TokenKind.TRUE, 'u');
        expect(TokenKind.TRUE, 'e');
        return Token.TRUE;
    }

    private Token consumeFalse() {
        consumeChar(); // f
        expect(TokenKind.FALSE, 'a');
        expect(TokenKind.FALSE, 'l');
        expect(TokenKind.FALSE, 's');
        expect(TokenKind.FALSE, 'e');
        return Token.FALSE;
    }

    private Token consumeNull() {
        consumeChar(); // n
        expect(TokenKind.NULL, 'u');
        expect(TokenKind.NULL, 'l');
        expect(TokenKind.NULL, 'l');
        return Token.NULL;
    }

    private Token consumeString() {
        consumeChar();
        while (current != '"') {
            if (eof) {
                throw new LexingException("Got EOF while reading string.");
            } else {
                if (Character.isISOControl(current)) {
                    throw new LexingException("Found invalid iso control value at " + line + ":" + offset + ".");
                }
                if (current == '\\') {
                    consumeChar();
                    if (current == 'n' || current == 'b' || current == 'f' || current == 'r' || current == 't' || current == '/' || current == '\\' || current == 'u' || current == '"') {
                        builder.add('\\');
                    }
                }
                builder.add(current);
                consumeChar();
            }
        }
        consumeChar();
        try {
            return new Token(TokenKind.STRING, builder.toString());
        } finally {
            builder.reset();
        }
    }

    private Token consumeNumber() {
        boolean integral = true;
        boolean exponent = false;
        boolean decimal  = false;
        boolean isdigit  = false;
        boolean matchexp = false;

        while ((isdigit = Character.isDigit(current)) || (decimal = current == '-' || current == '.' || (matchexp = (current == 'e' || current == 'E')) || current == '+')) {
            if (decimal) {
                if (integral) {
                    integral = false;
                }
            }
            if ((false == isdigit) && matchexp || current == '+' || current == '.') {
                if (current == '.' || matchexp) {
                    if (false == Character.isDigit(previous)) {
                        throw new LexingException("Unexpected value '" + current + "' at " + line + ":" + offset + ", expecting digit (0-9). ");
                    }
                    if (matchexp) {
                        if (exponent) {
                            throw new LexingException("Invalid exponent declaration at " + line + ":" + offset);
                        } else {
                            exponent = true;
                        }
                    }
                }
                if (current == '+') {
                    if (false == matchexp) {
                        throw new LexingException("Unexpected value '" + current + "' at " + line + ":" + offset + ", expected 'e' or 'E'. ");
                    }
                }
            }
            builder.add(current);
            previous = current;
            consumeChar();
        }
        if (false == Character.isDigit(previous)) {
            throw new LexingException("Number did not end with a digit at " + line + ":" + offset);
        }
        try {
            return new Token(integral ? TokenKind.INTEGRAL : TokenKind.DECIMAL, builder.toString());
        } finally {
            builder.reset();
        }
    }

    private void consumeChar() {
        if (current == '\n') {
            line++;
            offset = 0;
        } else {
            offset++;
        }
        previous = current;
        current  = readChar();
    }

    private char readChar() {
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
        } catch (IOException error) {
            throw new LexingException("Error while trying to read from the input. ", error);
        }
    }

    private void expect(TokenKind kind, char value) {
        if (current != value) {
            throw new LexingException("Error while reading token '" + kind + "', unexpected value '" + (eof ? "EOF" : Character.toString(current)) + "' at " + line + ":" + offset);
        } else {
            consumeChar();
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
                reader  = null;
                buffer  = null;
                builder = null;
            }
        }
    }

}
