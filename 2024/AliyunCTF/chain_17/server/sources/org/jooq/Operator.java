package org.jooq;

import org.jooq.impl.DSL;

/* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/Operator.class */
public enum Operator {
    AND("and"),
    OR("or"),
    XOR("xor");

    private final String sql;
    private final Keyword keyword;

    Operator(String sql) {
        this.sql = sql;
        this.keyword = DSL.keyword(sql);
    }

    public final String toSQL() {
        return this.sql;
    }

    public final Keyword toKeyword() {
        return this.keyword;
    }

    public final Condition identity() {
        return this == AND ? DSL.trueCondition() : DSL.falseCondition();
    }
}
