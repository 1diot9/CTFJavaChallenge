package org.springframework.expression.spel.standard;

import org.springframework.lang.Nullable;

/* loaded from: server.jar:BOOT-INF/lib/spring-expression-6.1.3.jar:org/springframework/expression/spel/standard/Token.class */
class Token {
    TokenKind kind;

    @Nullable
    String data;
    int startPos;
    int endPos;

    /* JADX INFO: Access modifiers changed from: package-private */
    public Token(TokenKind tokenKind, int startPos, int endPos) {
        this.kind = tokenKind;
        this.startPos = startPos;
        this.endPos = endPos;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public Token(TokenKind tokenKind, char[] tokenData, int startPos, int endPos) {
        this(tokenKind, startPos, endPos);
        this.data = new String(tokenData);
    }

    public TokenKind getKind() {
        return this.kind;
    }

    public boolean isIdentifier() {
        return this.kind == TokenKind.IDENTIFIER;
    }

    public boolean isNumericRelationalOperator() {
        return this.kind == TokenKind.GT || this.kind == TokenKind.GE || this.kind == TokenKind.LT || this.kind == TokenKind.LE || this.kind == TokenKind.EQ || this.kind == TokenKind.NE;
    }

    public String stringValue() {
        return this.data != null ? this.data : "";
    }

    public Token asInstanceOfToken() {
        return new Token(TokenKind.INSTANCEOF, this.startPos, this.endPos);
    }

    public Token asMatchesToken() {
        return new Token(TokenKind.MATCHES, this.startPos, this.endPos);
    }

    public Token asBetweenToken() {
        return new Token(TokenKind.BETWEEN, this.startPos, this.endPos);
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append('[').append(this.kind);
        if (this.kind.hasPayload()) {
            sb.append(':').append(this.data);
        }
        sb.append(']');
        sb.append('(').append(this.startPos).append(',').append(this.endPos).append(')');
        return sb.toString();
    }
}
