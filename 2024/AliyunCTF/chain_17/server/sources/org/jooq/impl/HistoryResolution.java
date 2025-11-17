package org.jooq.impl;

import org.jooq.Catalog;
import org.jooq.EnumType;
import org.jooq.Schema;

/* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/impl/HistoryResolution.class */
public enum HistoryResolution implements EnumType {
    OPEN("OPEN"),
    RESOLVED("RESOLVED"),
    IGNORED("IGNORED");

    private final String literal;

    HistoryResolution(String literal) {
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

    public static HistoryResolution lookupLiteral(String literal) {
        return (HistoryResolution) EnumType.lookupLiteral(HistoryResolution.class, literal);
    }
}
