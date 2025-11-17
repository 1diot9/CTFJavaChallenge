package org.jooq.impl;

import java.util.Set;
import org.jooq.Clause;
import org.jooq.Configuration;
import org.jooq.Context;
import org.jooq.DropTableFinalStep;
import org.jooq.DropTableStep;
import org.jooq.Function4;
import org.jooq.SQLDialect;
import org.jooq.Table;
import org.jooq.impl.QOM;

/* JADX INFO: Access modifiers changed from: package-private */
/* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/impl/DropTableImpl.class */
public final class DropTableImpl extends AbstractDDLQuery implements QOM.DropTable, DropTableStep, DropTableFinalStep {
    final boolean temporary;
    final Table<?> table;
    final boolean ifExists;
    QOM.Cascade cascade;
    private static final Clause[] CLAUSES = {Clause.DROP_TABLE};
    private static final Set<SQLDialect> NO_SUPPORT_IF_EXISTS = SQLDialect.supportedUntil(SQLDialect.DERBY, SQLDialect.FIREBIRD);
    private static final Set<SQLDialect> TEMPORARY_SEMANTIC = SQLDialect.supportedBy(SQLDialect.MARIADB, SQLDialect.MYSQL);

    /* JADX INFO: Access modifiers changed from: package-private */
    /* renamed from: org.jooq.impl.DropTableImpl$1, reason: invalid class name */
    /* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/impl/DropTableImpl$1.class */
    public static /* synthetic */ class AnonymousClass1 {
        static final /* synthetic */ int[] $SwitchMap$org$jooq$SQLDialect = new int[SQLDialect.values().length];
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public DropTableImpl(Configuration configuration, boolean temporary, Table<?> table, boolean ifExists) {
        this(configuration, temporary, table, ifExists, null);
    }

    DropTableImpl(Configuration configuration, boolean temporary, Table<?> table, boolean ifExists, QOM.Cascade cascade) {
        super(configuration);
        this.temporary = temporary;
        this.table = table;
        this.ifExists = ifExists;
        this.cascade = cascade;
    }

    @Override // org.jooq.DropTableStep
    public final DropTableImpl cascade() {
        this.cascade = QOM.Cascade.CASCADE;
        return this;
    }

    @Override // org.jooq.DropTableStep
    public final DropTableImpl restrict() {
        this.cascade = QOM.Cascade.RESTRICT;
        return this;
    }

    private final boolean supportsIfExists(Context<?> ctx) {
        return !NO_SUPPORT_IF_EXISTS.contains(ctx.dialect());
    }

    @Override // org.jooq.QueryPartInternal
    public final void accept(Context<?> ctx) {
        if (this.ifExists && !supportsIfExists(ctx)) {
            Tools.tryCatch(ctx, DDLStatementType.DROP_TABLE, c -> {
                accept0(c);
            });
        } else {
            accept0(ctx);
        }
    }

    /* JADX WARN: Type inference failed for: r0v20, types: [org.jooq.Context] */
    /* JADX WARN: Type inference failed for: r0v25, types: [org.jooq.Context] */
    /* JADX WARN: Type inference failed for: r0v3, types: [org.jooq.Context] */
    /* JADX WARN: Type inference failed for: r0v8, types: [org.jooq.Context] */
    private void accept0(Context<?> ctx) {
        ctx.start(Clause.DROP_TABLE_TABLE);
        ctx.visit(Keywords.K_DROP).sql(' ');
        if (this.temporary && TEMPORARY_SEMANTIC.contains(ctx.dialect())) {
            ctx.visit(Keywords.K_TEMPORARY).sql(' ');
        }
        ctx.visit(Keywords.K_TABLE).sql(' ');
        if (this.ifExists && supportsIfExists(ctx)) {
            ctx.visit(Keywords.K_IF_EXISTS).sql(' ');
        }
        ctx.visit(this.table);
        acceptCascade(ctx);
        ctx.end(Clause.DROP_TABLE_TABLE);
    }

    private final void acceptCascade(Context<?> ctx) {
        switch (AnonymousClass1.$SwitchMap$org$jooq$SQLDialect[ctx.family().ordinal()]) {
            default:
                acceptCascade(ctx, this.cascade);
                return;
        }
    }

    @Override // org.jooq.impl.AbstractQueryPart, org.jooq.QueryPartInternal
    public final Clause[] clauses(Context<?> ctx) {
        return CLAUSES;
    }

    @Override // org.jooq.impl.QOM.DropTable
    public final boolean $temporary() {
        return this.temporary;
    }

    @Override // org.jooq.impl.QOM.DropTable
    public final Table<?> $table() {
        return this.table;
    }

    @Override // org.jooq.impl.QOM.DropTable
    public final boolean $ifExists() {
        return this.ifExists;
    }

    @Override // org.jooq.impl.QOM.DropTable
    public final QOM.Cascade $cascade() {
        return this.cascade;
    }

    @Override // org.jooq.impl.QOM.DropTable
    public final QOM.DropTable $temporary(boolean newValue) {
        return $constructor().apply(Boolean.valueOf(newValue), $table(), Boolean.valueOf($ifExists()), $cascade());
    }

    @Override // org.jooq.impl.QOM.DropTable
    public final QOM.DropTable $table(Table<?> newValue) {
        return $constructor().apply(Boolean.valueOf($temporary()), newValue, Boolean.valueOf($ifExists()), $cascade());
    }

    @Override // org.jooq.impl.QOM.DropTable
    public final QOM.DropTable $ifExists(boolean newValue) {
        return $constructor().apply(Boolean.valueOf($temporary()), $table(), Boolean.valueOf(newValue), $cascade());
    }

    @Override // org.jooq.impl.QOM.DropTable
    public final QOM.DropTable $cascade(QOM.Cascade newValue) {
        return $constructor().apply(Boolean.valueOf($temporary()), $table(), Boolean.valueOf($ifExists()), newValue);
    }

    public final Function4<? super Boolean, ? super Table<?>, ? super Boolean, ? super QOM.Cascade, ? extends QOM.DropTable> $constructor() {
        return (a1, a2, a3, a4) -> {
            return new DropTableImpl(configuration(), a1.booleanValue(), a2, a3.booleanValue(), a4);
        };
    }
}
