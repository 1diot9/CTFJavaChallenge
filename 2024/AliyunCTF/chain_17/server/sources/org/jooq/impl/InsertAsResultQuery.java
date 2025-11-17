package org.jooq.impl;

import java.util.Collection;
import java.util.List;
import org.jooq.InsertResultStep;
import org.jooq.Record;
import org.jooq.SelectFieldOrAsterisk;
import org.jooq.impl.QOM;

/* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/impl/InsertAsResultQuery.class */
final class InsertAsResultQuery<R extends Record> extends AbstractDMLQueryAsResultQuery<R, InsertQueryImpl<R>> implements InsertResultStep<R>, QOM.InsertReturning<R> {
    /* JADX INFO: Access modifiers changed from: package-private */
    public InsertAsResultQuery(InsertQueryImpl<R> delegate, boolean returningResult) {
        super(delegate, returningResult);
    }

    @Override // org.jooq.impl.QOM.InsertReturning
    public final QOM.Insert<?> $insert() {
        return getDelegate();
    }

    @Override // org.jooq.impl.QOM.InsertReturning
    public final QOM.InsertReturning<R> $insert(QOM.Insert<?> newInsert) {
        return new InsertAsResultQuery(Tools.insertQueryImpl(newInsert).copy(i -> {
            i.setReturning($returning());
        }), this.returningResult);
    }

    @Override // org.jooq.impl.QOM.InsertReturning
    public final QOM.UnmodifiableList<? extends SelectFieldOrAsterisk> $returning() {
        return QOM.unmodifiable((List) getDelegate().returning);
    }

    @Override // org.jooq.impl.QOM.InsertReturning
    public final QOM.InsertReturning<?> $returning(Collection<? extends SelectFieldOrAsterisk> returning) {
        return new InsertAsResultQuery(getDelegate().copy(i -> {
            i.setReturning((Collection<? extends SelectFieldOrAsterisk>) returning);
        }), this.returningResult);
    }
}
