package io.github.cuisse.json;

/**
 * A simple json lexer, also known as 'tokenizer'.
 *
 * @author Brayan Roman
 */
public class Lexer {

    private JsonReader reader;
    private JsonOptions options;
    private int line = 1;
    private int offset;
    private char current;
    private Token token;
    private StringBuilder builder;

    private static final char EOF = (char) -1;

    public Lexer() {
        this.builder = new StringBuilder(512);
    }

    public void initialize(JsonReader stream, JsonOptions options) {
        this.reader  = stream;
        this.options = options;
        this.current = reader.read();
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
        return switch (current) {
            case '"' -> consumeString();
            case ':' -> consume(Token.COLON);
            case '{' -> consume(Token.OBJECT_OPEN);
            case '[' -> consume(Token.ARRAY_OPEN);
            case ',' -> consume(Token.COMMA);
            case 't' -> consumeTrue();
            case 'f' -> consumeFalse();
            case 'n' -> consumeNull();
            case '}' -> consume(Token.OBJECT_CLOSE);
            case ']' -> consume(Token.ARRAY_CLOSE);
            case '/' -> consumeComment();
            case '0' -> consumeNumber();
            case '1' -> consumeNumber();
            case '2' -> consumeNumber();
            case '3' -> consumeNumber();
            case '4' -> consumeNumber();
            case '5' -> consumeNumber();
            case '6' -> consumeNumber();
            case '7' -> consumeNumber();
            case '8' -> consumeNumber();
            case '9' -> consumeNumber();
            case '-' -> consumeNumber();
            case (char) -1 -> Token.EOF;
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

    private Token consumeComment() {
        if (options.get("skipComments", Boolean.class, () -> false) == false) {
            throw new LexingException("Invalid comment start '/' at " + line + ":" + offset + ". Maybe try again with 'skipComments' set to true?");
        }
        consumeChar(); // '/'
        if (current == '/') { // line comment
            consumeChar();
            while (current != '\n') {
                consumeChar();
                if (reader.eof()) {
                    return next();
                }
            }
            consumeChar();
        } else {
            if (current == '*') { // block comment
                while (true) {
                    if (reader.eof()) {
                        throw new LexingException("Unexpected end of the file while in block comment at " + line + ":" + offset);
                    } else {
                        consumeChar();
                        if (current == '*') {
                            consumeChar();
                            if (current == '/') {
                                consumeChar();
                                return next();
                            }
                        }
                    }
                }
            }
        }
        return next();
    }

    private Token consumeString() {
        consumeChar(); // "
        while (current != '"') {
            if (current == EOF) {
                if (reader.eof()) {
                    throw new LexingException("Unexpected end of file while in string at " + line + ":" + offset);
                }
            } else {
                if (isocontrol(current)) {
                    throw new LexingException("Found invalid iso control value at " + line + ":" + offset + ".");
                }
                if (current == '\\') {
                    consumeChar();
                    if (current == 'n' || current == 'b' || current == 'f' || current == 'r' || current == 't' || current == '/' || current == '\\' || current == 'u' || current == '"') {
                        builder.append('\\');
                    }
                }
            }
            builder.append(current);
            consumeChar();
        }
        consumeChar(); // "
        try {
            return new Token(TokenKind.STRING, builder.toString());
        } finally {
            builder.setLength(0);
        }
    }

    private Token consumeNumber() {
        try {
            Token token = null;
            match('-');

            if (matchNumber()) {
                if (match('.')) {
                    if (matchNumber()) {
                        if (match('E') || match('e')) {
                            if (match('+') || match('-')) {
                                if (matchNumber() == false) {
                                    report("Expecting number at %d:%d");
                                }
                            } else {
                                if (matchNumber() == false) {
                                    report("Expecting number after 'E' or 'e' at %d:%d");
                                }
                            }
                        }
                    } else {
                        report("Expecting number after dot (.)");
                    }
                   token = new Token(TokenKind.DECIMAL, builder.toString());
                } else {
                   token = new Token(TokenKind.INTEGRAL, builder.toString());
                }
            } else {
                report("Expecting number at %d:%d");
            }
            return token;
        } finally {
            builder.setLength(0);
        }
    }

    private boolean isocontrol(char value) {
        return value >= 0 && value <= 31 || value == 127;
    }

    private boolean digit(char value) {
        return value >= '0' && value <= '9';
    }

    private boolean match(char c) {
        if (current == c) {
            builder.append(current);
            consumeChar();
            return true;
        }
        return false;
    }

    private boolean matchNumber() {
        if (digit(current)) {
            while (digit(current)) {
                builder.append(current);
                consumeChar();
            }
            return true;
        }
        return false;
    }

    private void consumeChar() {
        if (current == '\n') {
            line++;
            offset = 0;
        } else {
            offset++;
        }
        current  = reader.read();
    }

    private void report(String error) {
        throw new LexingException(String.format(error, line, offset));
    }

    /**
     * Clean the resources hold by this lexer.
     */
    public void dispose() {
        token = null;
    }

}
