package com.cuisse;

import java.io.InputStream;

public class Parser {

    private final Lexer lexer;

    public Parser(String input) {
        this.lexer = new Lexer(new StringInputStream(input));
    }

    public Parser(InputStream stream) {
        this.lexer = new Lexer(stream);
    }

    public Element parse() {
        Token token = lexer.token();
        if (token.kind() == TokenKind.ARRAY_OPEN || token.kind() == TokenKind.OBJECT_OPEN) {
            return parse(lexer.consume());
        } else {
            throw new ParsingException("Json cannot start with: " + lexer.token().kind(), null);
        }
    }

    private Element parse(Token token) {
        return switch (token.kind()) {
            case OBJECT_OPEN -> parseObject(token);
            case STRING      -> new JsonString(token.value());
            case ARRAY_OPEN  -> parseArray(token);
            case FALSE, TRUE -> new Boolean(java.lang.Boolean.parseBoolean(token.value()));
            case INTEGRAL    -> parseIntegral(token);
            case FLOATING    -> parseFloating(token);
            case NULL        -> new Null();
            default          -> throw new ParsingException("Unexpected token: " + token.kind(), null);
        };
    }

    private JsonObject parseObject(Token token) {
        JsonObject value = new JsonObject();
        while (true) {
            if (lexer.token().kind() != TokenKind.OBJECT_CLOSE) {
                Token key = consume(TokenKind.STRING);
                if (value.contains(key.value())) {
                    throw new IllegalStateException("duplicated key " + key.value());
                }
                consume(TokenKind.COLON);
                if (lexer.token().kind().valuable()) {
                    value.put(key.value(), parse(lexer.consume()));
                    if (lexer.token().kind() == TokenKind.COMMA) {
                        lexer.consume();
                    } else {
                        break;
                    }
                } else {
                    throw new ParsingException("Unexpected token " + lexer.token().kind() + " as value in object.", null);
                }
            } else {
                break;
            }
        }
        consume(TokenKind.OBJECT_CLOSE);
        return value;
    }

    private JsonArray parseArray(Token token) {
        JsonArray value = new JsonArray();
        while (true) {
            if (lexer.token().kind() != TokenKind.ARRAY_CLOSE) {
                if (lexer.token().kind().valuable()) {
                    value.add(parse(lexer.consume()));
                    if (lexer.token().kind() == TokenKind.COMMA) {
                        lexer.consume();
                    } else {
                        break;
                    }
                } else {
                    throw new ParsingException("Unexpected token " + lexer.token().kind() + " as value in array.", null);
                }
            } else {
                break;
            }
        }
        consume(TokenKind.ARRAY_CLOSE);
        return value;
    }

    private Integral parseIntegral(Token token) {
        try {
            return new Integral(Long.parseLong(token.value()));
        } catch (NumberFormatException error) {
            throw new ParsingException("Malformed integral value: " + token.value(), error);
        }
    }

    private Floating parseFloating(Token token) {
        try {
            return new Floating(Double.parseDouble(token.value()));
        } catch (NumberFormatException error) {
            throw new ParsingException("Malformed floating value: " + token.value(), error);
        }
    }

    private Token consume(TokenKind kind) {
        if (lexer.token().kind() != kind) {
            throw new ParsingException("Expecting " + kind + " but got " + lexer.token().kind() + " at " + lexer.token().line() + ":" + lexer.token().offset(), null);
        } else {
            return lexer.consume();
        }
    }

}
