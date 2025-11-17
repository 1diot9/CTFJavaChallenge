package org.jooq.impl;

import java.util.Arrays;
import java.util.List;
import org.jooq.Constraint;
import org.jooq.ConstraintEnforcementStep;
import org.jooq.Context;
import org.jooq.ForeignKey;
import org.jooq.Key;
import org.jooq.Name;
import org.jooq.Record;
import org.jooq.Table;
import org.jooq.TableField;
import org.jooq.UniqueKey;
import org.jooq.impl.QOM;

/* JADX INFO: Access modifiers changed from: package-private */
/* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/impl/AbstractKey.class */
public abstract class AbstractKey<R extends Record> extends AbstractNamed implements Key<R>, QOM.UEmpty {
    private final Table<R> table;
    private final TableField<R, ?>[] fields;
    private final boolean enforced;

    abstract ConstraintEnforcementStep constraint0();

    AbstractKey(Table<R> table, TableField<R, ?>[] fields, boolean enforced) {
        this(table, null, fields, enforced);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public AbstractKey(Table<R> table, Name name, TableField<R, ?>[] fields, boolean enforced) {
        super(qualify(table, name), null);
        this.table = table;
        this.fields = fields;
        this.enforced = enforced;
    }

    @Override // org.jooq.Key
    public final Table<R> getTable() {
        return this.table;
    }

    @Override // org.jooq.Key
    public final List<TableField<R, ?>> getFields() {
        return Arrays.asList(this.fields);
    }

    @Override // org.jooq.Key
    public final TableField<R, ?>[] getFieldsArray() {
        return this.fields;
    }

    @Override // org.jooq.Key
    public final boolean nullable() {
        return Tools.anyMatch(this.fields, f -> {
            return f.getDataType().nullable();
        });
    }

    @Override // org.jooq.Key
    public final boolean enforced() {
        return this.enforced;
    }

    private final Constraint enforced(ConstraintEnforcementStep key) {
        return enforced() ? key : key.notEnforced();
    }

    @Override // org.jooq.Key
    public final Constraint constraint() {
        return enforced(constraint0());
    }

    @Override // org.jooq.QueryPartInternal
    public final void accept(Context<?> ctx) {
        ctx.visit(getUnqualifiedName());
    }

    @Override // org.jooq.impl.AbstractNamed, org.jooq.impl.AbstractQueryPart, org.jooq.QueryPart
    public int hashCode() {
        int result = (31 * 1) + getQualifiedName().hashCode();
        return (31 * result) + (this.table == null ? 0 : this.table.hashCode());
    }

    @Override // org.jooq.impl.AbstractNamed, org.jooq.impl.AbstractQueryPart, org.jooq.QueryPart
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || !(obj instanceof AbstractKey)) {
            return false;
        }
        if (this instanceof ForeignKey) {
            if (!(obj instanceof ForeignKey)) {
                return false;
            }
        } else if ((this instanceof UniqueKey) && !(obj instanceof UniqueKey)) {
            return false;
        }
        AbstractKey<?> other = (AbstractKey) obj;
        if (!getQualifiedName().equals(other.getQualifiedName())) {
            return false;
        }
        if (this.table == null) {
            if (other.table != null) {
                return false;
            }
            return true;
        }
        if (!this.table.equals(other.table) || !Arrays.equals(this.fields, other.fields)) {
            return false;
        }
        return true;
    }

    @Override // org.jooq.impl.AbstractQueryPart, org.jooq.QueryPart
    public String toString() {
        return constraint().toString();
    }
}
