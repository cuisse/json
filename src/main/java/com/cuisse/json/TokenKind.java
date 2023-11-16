package com.cuisse.json;

/**
 * @author Brayan Roman
 */
public enum TokenKind {
    OBJECT_OPEN,  // {
    OBJECT_CLOSE, // }
    STRING,       // "
    COLON,        // :
    ARRAY_OPEN,   // [
    ARRAY_CLOSE,  // ]
    COMMA,        // ,
    FALSE,        // false
    TRUE,         // true
    INTEGRAL,     // -, 0..9
    DECIMAL,      // -, +, ., 0..9, e, E
    NULL,         // null
    EOF;          // end of the file

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
        return this == OBJECT_OPEN ||
               this == STRING      ||
               this == ARRAY_OPEN  ||
               this == FALSE       ||
               this == TRUE        ||
               this == INTEGRAL    ||
               this == DECIMAL     ||
               this == NULL;
    }

}
