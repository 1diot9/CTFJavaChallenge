package org.jooq.impl;

import org.jooq.Check;
import org.jooq.Condition;
import org.jooq.Constraint;
import org.jooq.ConstraintEnforcementStep;
import org.jooq.Context;
import org.jooq.Name;
import org.jooq.Record;
import org.jooq.Table;
import org.jooq.impl.QOM;

/* JADX INFO: Access modifiers changed from: package-private */
/* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/impl/CheckImpl.class */
public final class CheckImpl<R extends Record> extends AbstractNamed implements Check<R>, QOM.UNotYetImplemented {
    final Table<R> table;
    final Condition condition;
    final boolean enforced;

    CheckImpl(Table<R> table, Condition condition, boolean enforced) {
        this(table, null, condition, enforced);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public CheckImpl(Table<R> table, Name name, Condition condition, boolean enforced) {
        super(name, null);
        this.table = table;
        this.condition = condition;
        this.enforced = enforced;
    }

    @Override // org.jooq.Check
    public final Table<R> getTable() {
        return this.table;
    }

    @Override // org.jooq.Check
    public final Condition condition() {
        return this.condition;
    }

    @Override // org.jooq.Check
    public final boolean enforced() {
        return this.enforced;
    }

    private final Constraint enforced(ConstraintEnforcementStep key) {
        return enforced() ? key : key.notEnforced();
    }

    @Override // org.jooq.Check
    public final Constraint constraint() {
        return enforced(DSL.constraint(getName()).check(this.condition));
    }

    @Override // org.jooq.QueryPartInternal
    public final void accept(Context<?> ctx) {
        ctx.visit(getUnqualifiedName());
    }

    @Override // org.jooq.impl.AbstractNamed, org.jooq.impl.AbstractQueryPart, org.jooq.QueryPart
    public int hashCode() {
        int result = super.hashCode();
        return (31 * ((31 * result) + (this.condition == null ? 0 : this.condition.hashCode()))) + (this.table == null ? 0 : this.table.hashCode());
    }

    @Override // org.jooq.impl.AbstractNamed, org.jooq.impl.AbstractQueryPart, org.jooq.QueryPart
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!super.equals(obj) || getClass() != obj.getClass()) {
            return false;
        }
        CheckImpl<?> other = (CheckImpl) obj;
        if (!getQualifiedName().equals(other.getQualifiedName())) {
            return false;
        }
        if (this.condition == null) {
            if (other.condition != null) {
                return false;
            }
        } else if (!this.condition.equals(other.condition)) {
            return false;
        }
        if (this.table == null) {
            if (other.table != null) {
                return false;
            }
            return true;
        }
        if (!this.table.equals(other.table)) {
            return false;
        }
        return true;
    }

    @Override // org.jooq.impl.AbstractQueryPart, org.jooq.QueryPart
    public String toString() {
        return constraint().toString();
    }
}
