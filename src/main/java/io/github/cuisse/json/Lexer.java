package io.github.cuisse.json;

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
            this.buffer  = new char[1024];
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
        if (token == null) {
            return peek();
        } else {
            Token consumed = token;
            token = next();
            return consumed;
        }
    }

    private Token next() {
        while (current == ' ' || current == '\n' || current == '\t' || current == '\r') {
            consumeChar();
        }
        if (eof) {
            return Token.EOF;
        }
        if (current <= '9') {
            if (current >= '0' || current == '-') {
                return consumeNumber();
            }
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
        if (current == 'r') { consumeChar();
            if (current == 'u') { consumeChar();
                if (current == 'e') { consumeChar();
                    return Token.TRUE;
                }
            }
        }
        throw new LexingException("Invalid token 't" + current + "' at " + line + ":" + offset);
    }

    private Token consumeFalse() {
        consumeChar(); // f
        if (current == 'a') { consumeChar();
            if (current == 'l') { consumeChar();
                if (current == 's') { consumeChar();
                    if (current == 'e') { consumeChar();
                        return Token.FALSE;
                    }
                }
            }
        }
        throw new LexingException("Invalid token 'f" + current + "' at " + line + ":" + offset);
    }

    private Token consumeNull() {
        consumeChar(); // n
        if (current == 'u') { consumeChar();
            if (current == 'l') { consumeChar();
                if (current == 'l') { consumeChar();
                    return Token.NULL;
                }
            }
        }
        throw new LexingException("Invalid token 'n" + current + "' at " + line + ":" + offset);
    }

    private Token consumeString() {
        consumeChar(); // "
        while (current != '"') {
            if (eof) {
                throw new LexingException("Unexpected end of file at " + line + ":" + offset);
            } else {
                if (isocontrol(current)) {
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
        consumeChar(); // "
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

        while ((isdigit = digit(current)) || (decimal = current == '-' || current == '.' || (matchexp = (current == 'e' || current == 'E')) || current == '+')) {
            if (decimal) {
                if (integral) {
                    integral = false;
                }
            }
            if ((false == isdigit) && matchexp || current == '+' || current == '.') {
                if (current == '.' || matchexp) {
                    if (false == digit(previous)) {
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
        if (false == digit(previous)) {
            throw new LexingException("Number did not end with a digit at " + line + ":" + offset);
        }
        try {
            return new Token(integral ? TokenKind.INTEGRAL : TokenKind.DECIMAL, builder.toString());
        } finally {
            builder.reset();
        }
    }

    private boolean isocontrol(char value) {
        return value >= 0 && value <= 31 || value == 127;
    }

    private boolean digit(char value) {
        return value >= '0' && value <= '9';
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
