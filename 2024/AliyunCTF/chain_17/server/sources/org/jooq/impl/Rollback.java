package org.jooq.impl;

import org.jooq.Configuration;
import org.jooq.Context;
import org.jooq.Name;
import org.jooq.RollbackToSavepointStep;
import org.jooq.SQLDialect;
import org.jooq.impl.QOM;

/* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/impl/Rollback.class */
final class Rollback extends AbstractRowCountQuery implements QOM.Rollback, RollbackToSavepointStep {
    Name toSavepoint;

    /* renamed from: org.jooq.impl.Rollback$1, reason: invalid class name */
    /* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/impl/Rollback$1.class */
    static /* synthetic */ class AnonymousClass1 {
        static final /* synthetic */ int[] $SwitchMap$org$jooq$SQLDialect = new int[SQLDialect.values().length];
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public Rollback(Configuration configuration) {
        this(configuration, null);
    }

    Rollback(Configuration configuration, Name toSavepoint) {
        super(configuration);
        this.toSavepoint = toSavepoint;
    }

    @Override // org.jooq.RollbackToSavepointStep
    public final Rollback toSavepoint(String toSavepoint) {
        return toSavepoint(DSL.name(toSavepoint));
    }

    @Override // org.jooq.RollbackToSavepointStep
    public final Rollback toSavepoint(Name toSavepoint) {
        this.toSavepoint = toSavepoint;
        return this;
    }

    /* JADX WARN: Type inference failed for: r0v7, types: [org.jooq.Context] */
    @Override // org.jooq.QueryPartInternal
    public final void accept(Context<?> ctx) {
        switch (AnonymousClass1.$SwitchMap$org$jooq$SQLDialect[ctx.family().ordinal()]) {
            default:
                ctx.visit(Keywords.K_ROLLBACK);
                if (this.toSavepoint != null) {
                    ctx.sql(' ').visit(Keywords.K_TO).sql(' ').visit(Keywords.K_SAVEPOINT).sql(' ').visit(this.toSavepoint);
                    return;
                }
                return;
        }
    }

    @Override // org.jooq.impl.QOM.Rollback
    public final Name $toSavepoint() {
        return this.toSavepoint;
    }

    @Override // org.jooq.impl.QOM.Rollback
    public final QOM.Rollback $toSavepoint(Name newValue) {
        return $constructor().apply(newValue);
    }

    public final org.jooq.Function1<? super Name, ? extends QOM.Rollback> $constructor() {
        return a1 -> {
            return new Rollback(configuration(), a1);
        };
    }
}
