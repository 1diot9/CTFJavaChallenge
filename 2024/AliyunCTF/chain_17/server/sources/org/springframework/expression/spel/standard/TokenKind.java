package org.springframework.expression.spel.standard;

import ch.qos.logback.core.CoreConstants;
import org.slf4j.Marker;
import org.springframework.aop.framework.autoproxy.target.QuickTargetSourceCreator;
import org.springframework.beans.PropertyAccessor;
import org.springframework.beans.factory.BeanFactory;

/* loaded from: server.jar:BOOT-INF/lib/spring-expression-6.1.3.jar:org/springframework/expression/spel/standard/TokenKind.class */
enum TokenKind {
    LITERAL_INT,
    LITERAL_LONG,
    LITERAL_HEXINT,
    LITERAL_HEXLONG,
    LITERAL_STRING,
    LITERAL_REAL,
    LITERAL_REAL_FLOAT,
    LPAREN("("),
    RPAREN(")"),
    COMMA(","),
    IDENTIFIER,
    COLON(":"),
    HASH("#"),
    RSQUARE("]"),
    LSQUARE(PropertyAccessor.PROPERTY_KEY_PREFIX),
    LCURLY("{"),
    RCURLY("}"),
    DOT("."),
    PLUS(Marker.ANY_NON_NULL_MARKER),
    STAR("*"),
    MINUS("-"),
    SELECT_FIRST("^["),
    SELECT_LAST("$["),
    QMARK(CoreConstants.NA),
    PROJECT("!["),
    DIV("/"),
    GE(">="),
    GT(">"),
    LE("<="),
    LT("<"),
    EQ("=="),
    NE("!="),
    MOD(QuickTargetSourceCreator.PREFIX_THREAD_LOCAL),
    NOT("!"),
    ASSIGN("="),
    INSTANCEOF("instanceof"),
    MATCHES("matches"),
    BETWEEN("between"),
    SELECT("?["),
    POWER("^"),
    ELVIS("?:"),
    SAFE_NAVI("?."),
    BEAN_REF("@"),
    FACTORY_BEAN_REF(BeanFactory.FACTORY_BEAN_PREFIX),
    SYMBOLIC_OR("||"),
    SYMBOLIC_AND("&&"),
    INC("++"),
    DEC("--");

    final char[] tokenChars;
    private final boolean hasPayload;

    TokenKind(String tokenString) {
        this.tokenChars = tokenString.toCharArray();
        this.hasPayload = this.tokenChars.length == 0;
    }

    TokenKind() {
        this("");
    }

    @Override // java.lang.Enum
    public String toString() {
        return name() + (this.tokenChars.length != 0 ? "(" + new String(this.tokenChars) + ")" : "");
    }

    public boolean hasPayload() {
        return this.hasPayload;
    }

    public int getLength() {
        return this.tokenChars.length;
    }
}
