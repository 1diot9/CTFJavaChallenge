package org.jooq.impl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.jooq.ConstraintEnforcementStep;
import org.jooq.ForeignKey;
import org.jooq.Name;
import org.jooq.Record;
import org.jooq.Table;
import org.jooq.TableField;
import org.jooq.UniqueKey;

/* JADX INFO: Access modifiers changed from: package-private */
/* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/impl/UniqueKeyImpl.class */
public final class UniqueKeyImpl<R extends Record> extends AbstractKey<R> implements UniqueKey<R> {
    final List<ForeignKey<?, R>> references;

    UniqueKeyImpl(Table<R> table, TableField<R, ?>[] fields, boolean enforced) {
        this(table, null, fields, enforced);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public UniqueKeyImpl(Table<R> table, Name name, TableField<R, ?>[] fields, boolean enforced) {
        super(table, name, fields, enforced);
        this.references = new ArrayList();
    }

    @Override // org.jooq.UniqueKey
    public final boolean isPrimary() {
        return equals(getTable().getPrimaryKey());
    }

    @Override // org.jooq.UniqueKey
    public final List<ForeignKey<?, R>> getReferences() {
        return Collections.unmodifiableList(this.references);
    }

    @Override // org.jooq.impl.AbstractKey
    final ConstraintEnforcementStep constraint0() {
        if (isPrimary()) {
            return DSL.constraint(getName()).primaryKey(getFieldsArray());
        }
        return DSL.constraint(getName()).unique(getFieldsArray());
    }
}
