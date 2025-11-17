package org.jooq.impl;

import java.util.Collection;
import java.util.List;
import org.jooq.DeleteResultStep;
import org.jooq.Record;
import org.jooq.SelectFieldOrAsterisk;
import org.jooq.impl.QOM;

/* JADX INFO: Access modifiers changed from: package-private */
/* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/impl/DeleteAsResultQuery.class */
public final class DeleteAsResultQuery<R extends Record> extends AbstractDMLQueryAsResultQuery<R, DeleteQueryImpl<R>> implements DeleteResultStep<R>, QOM.DeleteReturning<R> {
    /* JADX INFO: Access modifiers changed from: package-private */
    public DeleteAsResultQuery(DeleteQueryImpl<R> delegate, boolean returningResult) {
        super(delegate, returningResult);
    }

    @Override // org.jooq.impl.QOM.DeleteReturning
    public final QOM.Delete<?> $delete() {
        return getDelegate();
    }

    @Override // org.jooq.impl.QOM.DeleteReturning
    public final QOM.DeleteReturning<R> $delete(QOM.Delete<?> newDelete) {
        return new DeleteAsResultQuery(Tools.deleteQueryImpl(newDelete).copy(d -> {
            d.setReturning($returning());
        }), this.returningResult);
    }

    @Override // org.jooq.impl.QOM.DeleteReturning
    public final QOM.UnmodifiableList<? extends SelectFieldOrAsterisk> $returning() {
        return QOM.unmodifiable((List) getDelegate().returning);
    }

    @Override // org.jooq.impl.QOM.DeleteReturning
    public final QOM.DeleteReturning<?> $returning(Collection<? extends SelectFieldOrAsterisk> returning) {
        return new DeleteAsResultQuery(getDelegate().copy(d -> {
            d.setReturning((Collection<? extends SelectFieldOrAsterisk>) returning);
        }), this.returningResult);
    }
}
