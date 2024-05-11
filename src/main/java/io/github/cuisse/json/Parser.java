package io.github.cuisse.json;

import java.io.InputStream;

/**
 * @author Brayan Roman
 */
public class Parser {

    private Lexer lexer;

    public Parser(String input, JsonOptions options) {
        this.lexer = new Lexer(new StringInputStream(input), options);
    }

    public Parser(InputStream stream, JsonOptions options) {
        this.lexer = new Lexer(stream, options);
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

    /**
     * Parses the json value into the specified target.
     *
     * @param target The target type.
     * @return       The parsed json value.
     */
    @SuppressWarnings("unchecked")
    public<T> T parse(Class<T> target) {
        var converter = JsonConverters.instance().find(target);
        if (converter != null) {
            if (lexer.peek().kind().accepts(converter.type())) {
                return ((JsonConverter<T>) converter).convert(parse());
            } else {
                throw new ParsingException("Expected value '" + converter.type() + "' but the first token heads to " + lexer.peek().kind());
            }
        } else {
            if (JsonValue.class.isAssignableFrom(target)) {
                return (T) parse();
            }
        }
        throw new ParsingException("Could not parse " + target + " because a JsonConverter is missing.");
    }

    private JsonValue parse(Token token) {
        return switch (token.kind()) {
            case OBJECT_OPEN -> parseObject();
            case STRING      -> token.value().isEmpty() ? JsonString.EMPTY : new JsonString(token.value());
            case ARRAY_OPEN  -> parseArray();
            case FALSE       -> JsonBoolean.FALSE;
            case TRUE        -> JsonBoolean.TRUE;
            case INTEGRAL    -> parseIntegral(token);
            case DECIMAL     -> parseDecimal(token);
            case NULL        -> new JsonNull();
            default          -> throw new ParsingException("Unexpected token: " + token.kind());
        };
    }

    private JsonObject parseObject() {
        JsonObject object = new JsonObject();
        while (lexer.peek().kind() != TokenKind.OBJECT_CLOSE) {
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
        }
        consume(TokenKind.OBJECT_CLOSE);
        return object;
    }

    private JsonArray parseArray() {
        JsonArray array = new JsonArray();
        while (lexer.peek().kind() != TokenKind.ARRAY_CLOSE) {
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
        }
        consume(TokenKind.ARRAY_CLOSE);
        return array;
    }

    private JsonIntegral parseIntegral(Token token) {
        if (token.value().length() == 1) {
            return switch (token.value().charAt(0)) {
                case '0' -> JsonIntegral.INTEGRAL_0;
                case '1' -> JsonIntegral.INTEGRAL_1;
                case '2' -> JsonIntegral.INTEGRAL_2;
                case '3' -> JsonIntegral.INTEGRAL_3;
                case '4' -> JsonIntegral.INTEGRAL_4;
                case '5' -> JsonIntegral.INTEGRAL_5;
                case '6' -> JsonIntegral.INTEGRAL_6;
                case '7' -> JsonIntegral.INTEGRAL_7;
                case '8' -> JsonIntegral.INTEGRAL_8;
                case '9' -> JsonIntegral.INTEGRAL_9;
                default  -> throw new ParsingException("Unknown integral value: " + token.value());
            };
        }
        try {
            return new JsonIntegral(
                    Long.parseLong(token.value())
            );
        } catch (NumberFormatException error) {
            throw new ParsingException("Malformed integral value: " + token.value(), error);
        }
    }

    private JsonDecimal parseDecimal(Token token) {
        try {
            return new JsonDecimal(
                    Double.parseDouble(token.value())
            );
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
