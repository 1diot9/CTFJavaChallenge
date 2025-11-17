package org.jooq.impl;

import java.util.Collection;
import java.util.List;
import org.jooq.Clause;
import org.jooq.Configuration;
import org.jooq.Context;
import org.jooq.Function3;
import org.jooq.Record;
import org.jooq.Table;
import org.jooq.Truncate;
import org.jooq.TruncateCascadeStep;
import org.jooq.TruncateFinalStep;
import org.jooq.TruncateIdentityStep;
import org.jooq.impl.QOM;

/* JADX INFO: Access modifiers changed from: package-private */
/* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/impl/TruncateImpl.class */
public final class TruncateImpl<R extends Record> extends AbstractDDLQuery implements QOM.Truncate<R>, TruncateIdentityStep<R>, TruncateCascadeStep<R>, TruncateFinalStep<R>, Truncate<R> {
    final QueryPartListView<? extends Table<?>> table;
    QOM.IdentityRestartOption restartIdentity;
    QOM.Cascade cascade;
    private static final Clause[] CLAUSES = {Clause.TRUNCATE};

    /* JADX INFO: Access modifiers changed from: package-private */
    public TruncateImpl(Configuration configuration, Collection<? extends Table<?>> table) {
        this(configuration, table, null, null);
    }

    TruncateImpl(Configuration configuration, Collection<? extends Table<?>> table, QOM.IdentityRestartOption restartIdentity, QOM.Cascade cascade) {
        super(configuration);
        this.table = new QueryPartList(table);
        this.restartIdentity = restartIdentity;
        this.cascade = cascade;
    }

    @Override // org.jooq.TruncateIdentityStep
    public final TruncateImpl<R> restartIdentity() {
        this.restartIdentity = QOM.IdentityRestartOption.RESTART_IDENTITY;
        return this;
    }

    @Override // org.jooq.TruncateIdentityStep
    public final TruncateImpl<R> continueIdentity() {
        this.restartIdentity = QOM.IdentityRestartOption.CONTINUE_IDENTITY;
        return this;
    }

    @Override // org.jooq.TruncateCascadeStep
    public final TruncateImpl<R> cascade() {
        this.cascade = QOM.Cascade.CASCADE;
        return this;
    }

    @Override // org.jooq.TruncateCascadeStep
    public final TruncateImpl<R> restrict() {
        this.cascade = QOM.Cascade.RESTRICT;
        return this;
    }

    @Override // org.jooq.QueryPartInternal
    public final void accept(Context<?> ctx) {
        switch (ctx.family()) {
            case FIREBIRD:
            case IGNITE:
            case SQLITE:
                if (this.table.size() == 1) {
                    ctx.visit(DSL.delete(this.table.get(0)));
                    return;
                } else {
                    accept0(ctx);
                    return;
                }
            default:
                accept0(ctx);
                return;
        }
    }

    /* JADX WARN: Type inference failed for: r0v1, types: [org.jooq.Context] */
    /* JADX WARN: Type inference failed for: r0v14, types: [org.jooq.Context] */
    /* JADX WARN: Type inference failed for: r0v17, types: [org.jooq.Context] */
    final void accept0(Context<?> ctx) {
        ctx.start(Clause.TRUNCATE_TRUNCATE).visit(Keywords.K_TRUNCATE).sql(' ').visit(Keywords.K_TABLE).sql(' ').visit(this.table);
        if (this.restartIdentity != null) {
            ctx.formatSeparator().visit(this.restartIdentity.keyword);
        }
        if (this.cascade != null) {
            ctx.formatSeparator().visit(this.cascade == QOM.Cascade.CASCADE ? Keywords.K_CASCADE : Keywords.K_RESTRICT);
        }
        ctx.end(Clause.TRUNCATE_TRUNCATE);
    }

    @Override // org.jooq.impl.AbstractQueryPart, org.jooq.QueryPartInternal
    public final Clause[] clauses(Context<?> ctx) {
        return CLAUSES;
    }

    @Override // org.jooq.impl.QOM.Truncate
    public final QOM.UnmodifiableList<? extends Table<?>> $table() {
        return QOM.unmodifiable((List) this.table);
    }

    @Override // org.jooq.impl.QOM.Truncate
    public final QOM.IdentityRestartOption $restartIdentity() {
        return this.restartIdentity;
    }

    @Override // org.jooq.impl.QOM.Truncate
    public final QOM.Cascade $cascade() {
        return this.cascade;
    }

    @Override // org.jooq.impl.QOM.Truncate
    public final QOM.Truncate<R> $table(Collection<? extends Table<?>> newValue) {
        return $constructor().apply(newValue, $restartIdentity(), $cascade());
    }

    @Override // org.jooq.impl.QOM.Truncate
    public final QOM.Truncate<R> $restartIdentity(QOM.IdentityRestartOption newValue) {
        return $constructor().apply($table(), newValue, $cascade());
    }

    @Override // org.jooq.impl.QOM.Truncate
    public final QOM.Truncate<R> $cascade(QOM.Cascade newValue) {
        return $constructor().apply($table(), $restartIdentity(), newValue);
    }

    public final Function3<? super Collection<? extends Table<?>>, ? super QOM.IdentityRestartOption, ? super QOM.Cascade, ? extends QOM.Truncate<R>> $constructor() {
        return (a1, a2, a3) -> {
            return new TruncateImpl(configuration(), a1, a2, a3);
        };
    }
}
