package com.cuisse.json;

import java.io.InputStream;

/**
 * @author Brayan Roman
 */
public class Parser {

    private Lexer lexer;

    public Parser(String input) {
        this.lexer = new Lexer(new StringInputStream(input));
    }

    public Parser(InputStream stream) {
        this.lexer = new Lexer(stream);
    }

    /**
     * Parses the json value.
     *
     * @return The parsed json value.
     */
    public JsonValue parse() {
        try {
            if (lexer.peek().kind() == TokenKind.ARRAY_OPEN || lexer.peek().kind() == TokenKind.OBJECT_OPEN) {
                return parse(lexer.consume());
            } else {
                throw new ParsingException("Json cannot start with: " + lexer.peek().kind());
            }
        } finally {
            lexer.dispose();
            lexer = null;
        }
    }

    private JsonValue parse(Token token) {
        return switch (token.kind()) {
            case OBJECT_OPEN -> parseObject();
            case STRING      -> new JsonString(token.value());
            case ARRAY_OPEN  -> parseArray();
            case FALSE, TRUE -> new JsonBoolean(token.value().equals("true"));
            case INTEGRAL    -> parseIntegral(token);
            case DECIMAL     -> parseDecimal(token);
            case NULL        -> new Null();
            default          -> throw new ParsingException("Unexpected token: " + token.kind());
        };
    }

    private JsonObject parseObject() {
        JsonObject object = new JsonObject();
        while (true) {
            if (lexer.peek().kind() != TokenKind.OBJECT_CLOSE) {
                Token key = consume(TokenKind.STRING);
                if (object.containsKey(key.value())) {
                    throw new ParsingException("Duplicated key " + key.value() + " in object.");
                }
                consume(TokenKind.COLON);
                if (lexer.peek().kind().valuable()) {
                    object.put(key.value(), parse(lexer.consume()));
                    if (lexer.peek().kind() == TokenKind.COMMA) {
                        lexer.consume();
                    } else {
                        break;
                    }
                } else {
                    throw new ParsingException("Unexpected token " + lexer.peek().kind() + " as value in object.");
                }
            } else {
                break;
            }
        }
        consume(TokenKind.OBJECT_CLOSE);
        return object;
    }

    private JsonArray parseArray() {
        JsonArray array = new JsonArray();
        while (true) {
            if (lexer.peek().kind() != TokenKind.ARRAY_CLOSE) {
                if (lexer.peek().kind().valuable()) {
                    array.add(parse(lexer.consume()));
                    if (lexer.peek().kind() == TokenKind.COMMA) {
                        lexer.consume();
                    } else {
                        break;
                    }
                } else {
                    throw new ParsingException("Unexpected token " + lexer.peek().kind() + " as value in array.");
                }
            } else {
                break;
            }
        }
        consume(TokenKind.ARRAY_CLOSE);
        return array;
    }

    private JsonIntegral parseIntegral(Token token) {
        try {
            return new JsonIntegral(Long.parseLong(token.value()));
        } catch (NumberFormatException error) {
            throw new ParsingException("Malformed integral value: " + token.value(), error);
        }
    }

    private JsonDecimal parseDecimal(Token token) {
        try {
            return new JsonDecimal(Double.parseDouble(token.value()));
        } catch (NumberFormatException error) {
            throw new ParsingException("Malformed decimal value: " + token.value(), error);
        }
    }

    private Token consume(TokenKind kind) {
        if (lexer.peek().kind() != kind) {
            throw new ParsingException("Expecting " + kind + " but got " + lexer.peek().kind() + " at " + lexer.line() + ":" + lexer.offset());
        } else {
            return lexer.consume();
        }
    }

}
