package com.cuisse;

public enum TokenKind {
    OBJECT_OPEN,  // {
    OBJECT_CLOSE, // }
    STRING,       // ""
    COLON,        // :
    ARRAY_OPEN,   // [
    ARRAY_CLOSE,  // ]
    COMMA,        // ,
    FALSE,        // false
    TRUE,         // true
    INTEGRAL,     // 0
    FLOATING,     // 0.0
    NULL,         // null
    EOF;          // end of the file

    public boolean valuable() {
        return this == OBJECT_OPEN ||
               this == STRING      ||
               this == ARRAY_OPEN  ||
               this == FALSE       ||
               this == TRUE        ||
               this == INTEGRAL    ||
               this == FLOATING    ||
               this == NULL;
    }

}
