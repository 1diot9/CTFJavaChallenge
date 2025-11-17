package org.jooq.impl;

import java.util.Collection;
import java.util.List;
import org.jooq.Record;
import org.jooq.SelectFieldOrAsterisk;
import org.jooq.UpdateResultStep;
import org.jooq.impl.QOM;

/* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/impl/UpdateAsResultQuery.class */
final class UpdateAsResultQuery<R extends Record> extends AbstractDMLQueryAsResultQuery<R, UpdateQueryImpl<R>> implements UpdateResultStep<R>, QOM.UpdateReturning<R> {
    /* JADX INFO: Access modifiers changed from: package-private */
    public UpdateAsResultQuery(UpdateQueryImpl<R> delegate, boolean returningResult) {
        super(delegate, returningResult);
    }

    @Override // org.jooq.impl.QOM.UpdateReturning
    public final QOM.Update<?> $update() {
        return getDelegate();
    }

    @Override // org.jooq.impl.QOM.UpdateReturning
    public final QOM.UpdateReturning<R> $update(QOM.Update<?> newUpdate) {
        return new UpdateAsResultQuery(Tools.updateQueryImpl(newUpdate).copy(d -> {
            d.setReturning($returning());
        }), this.returningResult);
    }

    @Override // org.jooq.impl.QOM.UpdateReturning
    public final QOM.UnmodifiableList<? extends SelectFieldOrAsterisk> $returning() {
        return QOM.unmodifiable((List) getDelegate().returning);
    }

    @Override // org.jooq.impl.QOM.UpdateReturning
    public final QOM.UpdateReturning<?> $returning(Collection<? extends SelectFieldOrAsterisk> returning) {
        return new UpdateAsResultQuery(getDelegate().copy(d -> {
            d.setReturning((Collection<? extends SelectFieldOrAsterisk>) returning);
        }), this.returningResult);
    }
}
