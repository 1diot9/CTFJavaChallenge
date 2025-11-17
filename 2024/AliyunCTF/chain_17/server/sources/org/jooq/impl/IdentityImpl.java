package org.jooq.impl;

import org.jooq.Identity;
import org.jooq.Record;
import org.jooq.Table;
import org.jooq.TableField;

/* JADX INFO: Access modifiers changed from: package-private */
/* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/impl/IdentityImpl.class */
public final class IdentityImpl<R extends Record, T> implements Identity<R, T> {
    static final IdentityImpl<?, ?> NULL = new IdentityImpl<>(null, null);
    private final Table<R> table;
    private final TableField<R, T> field;

    /* JADX INFO: Access modifiers changed from: package-private */
    public IdentityImpl(Table<R> table, TableField<R, T> field) {
        this.table = table;
        this.field = field;
    }

    @Override // org.jooq.Identity
    public final Table<R> getTable() {
        return this.table;
    }

    @Override // org.jooq.Identity
    public final TableField<R, T> getField() {
        return this.field;
    }

    public int hashCode() {
        return toString().hashCode();
    }

    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj instanceof Identity) {
            return toString().equals(obj.toString());
        }
        return false;
    }

    public String toString() {
        return this.field.toString();
    }
}
