package org.jooq.impl;

import org.jooq.Name;
import org.slf4j.Marker;

/* JADX INFO: Access modifiers changed from: package-private */
/* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/impl/ExpressionOperator.class */
public enum ExpressionOperator {
    CONCAT("||", true, false),
    ADD(Marker.ANY_NON_NULL_MARKER, true, true),
    SUBTRACT("-"),
    MULTIPLY("*", true, true),
    DIVIDE("/");

    private final String sql;
    private final Name name;
    private final boolean associative;
    private final boolean commutative;

    ExpressionOperator(String sql) {
        this(sql, false, false);
    }

    ExpressionOperator(String sql, boolean associative, boolean commutative) {
        this.sql = sql;
        this.name = DSL.name(name().toLowerCase());
        this.associative = associative;
        this.commutative = commutative;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public final String toSQL() {
        return this.sql;
    }

    final Name toName() {
        return this.name;
    }

    final boolean associative() {
        return this.associative;
    }

    final boolean commutative() {
        return this.commutative;
    }
}
