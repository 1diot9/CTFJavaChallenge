package org.jooq.impl;

import java.util.List;
import org.jooq.ConstraintEnforcementStep;
import org.jooq.ForeignKey;
import org.jooq.InverseForeignKey;
import org.jooq.Record;
import org.jooq.TableField;
import org.jooq.impl.QOM;

/* JADX INFO: Access modifiers changed from: package-private */
/* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/impl/InverseReferenceImpl.class */
public class InverseReferenceImpl<R extends Record, O extends Record> extends AbstractKey<R> implements InverseForeignKey<R, O>, QOM.UTransient {
    private final ForeignKey<O, R> foreignKey;

    /* JADX INFO: Access modifiers changed from: package-private */
    public InverseReferenceImpl(ForeignKey<O, R> foreignKey) {
        super(foreignKey.getKey().getTable(), foreignKey.getQualifiedName(), foreignKey.getKeyFieldsArray(), foreignKey.enforced());
        this.foreignKey = foreignKey;
    }

    @Override // org.jooq.InverseForeignKey
    public final ForeignKey<O, R> getForeignKey() {
        return this.foreignKey;
    }

    @Override // org.jooq.InverseForeignKey
    public final List<TableField<O, ?>> getForeignKeyFields() {
        return (List<TableField<O, ?>>) this.foreignKey.getFields();
    }

    /* JADX WARN: Multi-variable type inference failed */
    @Override // org.jooq.InverseForeignKey
    public final TableField<O, ?>[] getForeignKeyFieldsArray() {
        return (TableField<O, ?>[]) this.foreignKey.getFieldsArray();
    }

    @Override // org.jooq.impl.AbstractKey
    ConstraintEnforcementStep constraint0() {
        return ((ReferenceImpl) this.foreignKey).constraint0();
    }
}
