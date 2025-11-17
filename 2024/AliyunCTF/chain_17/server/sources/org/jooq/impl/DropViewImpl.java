package org.jooq.impl;

import java.util.Set;
import org.jooq.Clause;
import org.jooq.Configuration;
import org.jooq.Context;
import org.jooq.DropViewFinalStep;
import org.jooq.Function3;
import org.jooq.SQLDialect;
import org.jooq.Table;
import org.jooq.impl.QOM;

/* JADX INFO: Access modifiers changed from: package-private */
/* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/impl/DropViewImpl.class */
public final class DropViewImpl extends AbstractDDLQuery implements QOM.DropView, DropViewFinalStep {
    final Table<?> view;
    final boolean materialized;
    final boolean ifExists;
    private static final Clause[] CLAUSES = {Clause.DROP_VIEW};
    private static final Set<SQLDialect> NO_SUPPORT_IF_EXISTS = SQLDialect.supportedUntil(SQLDialect.DERBY, SQLDialect.FIREBIRD);

    /* JADX INFO: Access modifiers changed from: package-private */
    /* renamed from: org.jooq.impl.DropViewImpl$1, reason: invalid class name */
    /* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/impl/DropViewImpl$1.class */
    public static /* synthetic */ class AnonymousClass1 {
        static final /* synthetic */ int[] $SwitchMap$org$jooq$SQLDialect = new int[SQLDialect.values().length];
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public DropViewImpl(Configuration configuration, Table<?> view, boolean materialized, boolean ifExists) {
        super(configuration);
        this.view = view;
        this.materialized = materialized;
        this.ifExists = ifExists;
    }

    private final boolean supportsIfExists(Context<?> ctx) {
        return !NO_SUPPORT_IF_EXISTS.contains(ctx.dialect());
    }

    @Override // org.jooq.QueryPartInternal
    public final void accept(Context<?> ctx) {
        if (this.ifExists && !supportsIfExists(ctx)) {
            Tools.tryCatch(ctx, DDLStatementType.DROP_VIEW, c -> {
                accept0(c);
            });
        } else {
            accept0(ctx);
        }
    }

    /* JADX WARN: Code restructure failed: missing block: B:5:0x0038, code lost:            r4.visit(org.jooq.impl.Keywords.K_MATERIALIZED).sql(' ').visit(org.jooq.impl.Keywords.K_VIEW).sql(' ');     */
    /* JADX WARN: Type inference failed for: r0v1, types: [org.jooq.Context] */
    /* JADX WARN: Type inference failed for: r0v18, types: [org.jooq.Context] */
    /* JADX WARN: Type inference failed for: r0v23, types: [org.jooq.Context] */
    /* JADX WARN: Type inference failed for: r0v7, types: [org.jooq.Context] */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct code enable 'Show inconsistent code' option in preferences
    */
    private void accept0(org.jooq.Context<?> r4) {
        /*
            r3 = this;
            r0 = r4
            org.jooq.Clause r1 = org.jooq.Clause.DROP_VIEW_TABLE
            org.jooq.Context r0 = r0.start(r1)
            org.jooq.Keyword r1 = org.jooq.impl.Keywords.K_DROP
            org.jooq.Context r0 = r0.visit(r1)
            r1 = 32
            org.jooq.Context r0 = r0.sql(r1)
            r0 = r3
            boolean r0 = r0.materialized
            if (r0 == 0) goto L5b
            int[] r0 = org.jooq.impl.DropViewImpl.AnonymousClass1.$SwitchMap$org$jooq$SQLDialect
            r1 = r4
            org.jooq.SQLDialect r1 = r1.family()
            int r1 = r1.ordinal()
            r0 = r0[r1]
            switch(r0) {
                default: goto L38;
            }
        L38:
            r0 = r4
            org.jooq.Keyword r1 = org.jooq.impl.Keywords.K_MATERIALIZED
            org.jooq.Context r0 = r0.visit(r1)
            r1 = 32
            org.jooq.Context r0 = r0.sql(r1)
            org.jooq.Keyword r1 = org.jooq.impl.Keywords.K_VIEW
            org.jooq.Context r0 = r0.visit(r1)
            r1 = 32
            org.jooq.Context r0 = r0.sql(r1)
            goto L6c
        L5b:
            r0 = r4
            org.jooq.Keyword r1 = org.jooq.impl.Keywords.K_VIEW
            org.jooq.Context r0 = r0.visit(r1)
            r1 = 32
            org.jooq.Context r0 = r0.sql(r1)
        L6c:
            r0 = r3
            boolean r0 = r0.ifExists
            if (r0 == 0) goto L8c
            r0 = r3
            r1 = r4
            boolean r0 = r0.supportsIfExists(r1)
            if (r0 == 0) goto L8c
            r0 = r4
            org.jooq.Keyword r1 = org.jooq.impl.Keywords.K_IF_EXISTS
            org.jooq.Context r0 = r0.visit(r1)
            r1 = 32
            org.jooq.Context r0 = r0.sql(r1)
        L8c:
            r0 = r4
            r1 = r3
            org.jooq.Table<?> r1 = r1.view
            org.jooq.Context r0 = r0.visit(r1)
            r0 = r4
            org.jooq.Clause r1 = org.jooq.Clause.DROP_VIEW_TABLE
            org.jooq.Context r0 = r0.end(r1)
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: org.jooq.impl.DropViewImpl.accept0(org.jooq.Context):void");
    }

    @Override // org.jooq.impl.AbstractQueryPart, org.jooq.QueryPartInternal
    public final Clause[] clauses(Context<?> ctx) {
        return CLAUSES;
    }

    @Override // org.jooq.impl.QOM.DropView
    public final Table<?> $view() {
        return this.view;
    }

    @Override // org.jooq.impl.QOM.DropView
    public final boolean $materialized() {
        return this.materialized;
    }

    @Override // org.jooq.impl.QOM.DropView
    public final boolean $ifExists() {
        return this.ifExists;
    }

    @Override // org.jooq.impl.QOM.DropView
    public final QOM.DropView $view(Table<?> newValue) {
        return $constructor().apply(newValue, Boolean.valueOf($materialized()), Boolean.valueOf($ifExists()));
    }

    @Override // org.jooq.impl.QOM.DropView
    public final QOM.DropView $materialized(boolean newValue) {
        return $constructor().apply($view(), Boolean.valueOf(newValue), Boolean.valueOf($ifExists()));
    }

    @Override // org.jooq.impl.QOM.DropView
    public final QOM.DropView $ifExists(boolean newValue) {
        return $constructor().apply($view(), Boolean.valueOf($materialized()), Boolean.valueOf(newValue));
    }

    public final Function3<? super Table<?>, ? super Boolean, ? super Boolean, ? extends QOM.DropView> $constructor() {
        return (a1, a2, a3) -> {
            return new DropViewImpl(configuration(), a1, a2.booleanValue(), a3.booleanValue());
        };
    }
}
