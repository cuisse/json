package io.github.cuisse.json;

/**
 * @author Brayan Roman
 */
public record Token(TokenKind kind, String value) {

    public static final Token OBJECT_OPEN  = new Token(TokenKind.OBJECT_OPEN, null);
    public static final Token OBJECT_CLOSE = new Token(TokenKind.OBJECT_CLOSE, null);
    public static final Token COLON        = new Token(TokenKind.COLON, null);
    public static final Token ARRAY_OPEN   = new Token(TokenKind.ARRAY_OPEN, null);
    public static final Token ARRAY_CLOSE  = new Token(TokenKind.ARRAY_CLOSE, null);
    public static final Token COMMA        = new Token(TokenKind.COMMA, null);
    public static final Token EOF          = new Token(TokenKind.EOF, null);
    public static final Token TRUE         = new Token(TokenKind.TRUE, null);
    public static final Token FALSE        = new Token(TokenKind.FALSE, null);
    public static final Token NULL         = new Token(TokenKind.NULL, null);

}
