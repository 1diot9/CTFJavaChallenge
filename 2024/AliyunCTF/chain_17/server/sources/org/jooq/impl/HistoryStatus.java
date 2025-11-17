package org.jooq.impl;

import org.jooq.Catalog;
import org.jooq.EnumType;
import org.jooq.Schema;

/* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/impl/HistoryStatus.class */
public enum HistoryStatus implements EnumType {
    STARTING("STARTING"),
    REVERTING("REVERTING"),
    MIGRATING("MIGRATING"),
    SUCCESS("SUCCESS"),
    FAILURE("FAILURE");

    private final String literal;

    HistoryStatus(String literal) {
        this.literal = literal;
    }

    @Override // org.jooq.EnumType
    public Catalog getCatalog() {
        return null;
    }

    @Override // org.jooq.EnumType
    public Schema getSchema() {
        return null;
    }

    @Override // org.jooq.EnumType
    public String getName() {
        return null;
    }

    @Override // org.jooq.EnumType
    public String getLiteral() {
        return this.literal;
    }

    public static HistoryStatus lookupLiteral(String literal) {
        return (HistoryStatus) EnumType.lookupLiteral(HistoryStatus.class, literal);
    }
}
