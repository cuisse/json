package io.github.cuisse.json;

/**
 * @author Brayan Roman
 */
public enum TokenKind {
    OBJECT_OPEN(true), // {
    OBJECT_CLOSE,               // }
    STRING(true),      // "
    COLON,                      // :
    ARRAY_OPEN(true),  // [
    ARRAY_CLOSE,                // ]
    COMMA,                      // ,
    FALSE(true),       // false
    TRUE(true),        // true
    INTEGRAL(true),    // -, 0..9
    DECIMAL(true),     // -, +, ., 0..9, e, E
    NULL(true),        // null
    EOF;                        // end of the file

    private final boolean valuable;

    TokenKind() {
        this.valuable = false;
    }

    TokenKind(boolean valuable) {
        this.valuable = valuable;
    }

    public boolean accepts(JsonType type) {
        return switch (type) {
            case OBJECT  -> this == OBJECT_OPEN;
            case ARRAY   -> this == ARRAY_OPEN;
            case NUMBER  -> this == INTEGRAL || this == DECIMAL;
            case STRING  -> this == STRING;
            case BOOLEAN -> this == TRUE || this == FALSE;
            case NULL    -> this == NULL;
        };
    }

    public boolean valuable() {
        return valuable;
    }

}
