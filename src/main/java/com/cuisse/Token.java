package com.cuisse;

public record Token(TokenKind kind, String value, int line, int offset) {

}
