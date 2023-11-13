package com.cuisse;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;

public class Lexer {

    private final Reader reader;
    private int line;
    private int offset;
    private char current;
    private Token token;
    private boolean eof;

    private static final String TRUE  = "true";
    private static final String FALSE = "false";
    private static final String NULL  = "null";

    public Lexer(InputStream stream) {
        if (stream == null) {
            throw new NullPointerException();
        } else {
            this.reader  = new InputStreamReader(stream);
            this.current = (char) readChar();
            this.token   = next();
        }
    }

    public Token token() {
        return token;
    }

    public Token consume() {
        Token old = token;
        token = next();
        return old;
    }

    private Token next() {
        while (current == ' ' || current == '\t' || current == '\r' || current == '\n') {
            consumeChar();
        }
        if (eof) {
            return new Token(TokenKind.EOF, "", line, offset);
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
            default  -> throw new IllegalArgumentException("Invalid token '" + current + "' at " + line + ":" + offset);
        };
    }

    public Token create(TokenKind kind) {
        try {
            return new Token(kind, Character.toString(current), line, offset);
        } finally {
            consumeChar();
        }
    }

    private Token create(TokenKind kind, String expected) {
        consumeChar();
        for (int i = 1; i < expected.length(); i++) {
            if (current != expected.charAt(i)) {
                throw new IllegalStateException("Error while reading token '" + kind + "', unexpected value '" + (eof ? "EOF" : Character.toString(current)) + "' at " + line + ":" + offset);
            }
            consumeChar();
        }
        return new Token(kind, expected, line, offset);
    }

    private Token createString() {
        consumeChar();
        StringBuilder value = new StringBuilder();
        while (current != '"') {
            if (eof) {
                throw new IllegalStateException("eof");
            } else {
                // TODO: control characters
                if (current == '\\') {
                    consumeChar();
                }
                value.append(current);
                consumeChar();
            }
        }
        consumeChar();
        return new Token(TokenKind.STRING, value.toString(), line, offset);
    }

    private Token createNumber() {
        StringBuilder value = new StringBuilder();
        boolean integral = true;
        while (Character.isDigit(current) || current == '-' || current == 'e' || current == 'E' || current == '+' || current == '.') {
            if (current == 'e' || current == 'E' || current == '+' || current == '.') {
                if (integral) {
                    integral = false;
                }
            }
            value.append(current);
            consumeChar();
        }

        return new Token(integral ? TokenKind.INTEGRAL : TokenKind.FLOATING, value.toString(), line, offset);
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
        current = (char) c;
    }

    private int readChar() {
        try {
            return reader.read();
        } catch (IOException error) {
            throw new RuntimeException();
        }
    }

}
