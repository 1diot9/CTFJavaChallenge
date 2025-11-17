package org.jooq.impl;

import java.util.Set;
import org.jooq.AlterIndexFinalStep;
import org.jooq.AlterIndexOnStep;
import org.jooq.AlterIndexStep;
import org.jooq.Clause;
import org.jooq.Configuration;
import org.jooq.Context;
import org.jooq.Function4;
import org.jooq.Index;
import org.jooq.Name;
import org.jooq.SQLDialect;
import org.jooq.Table;
import org.jooq.impl.QOM;

/* JADX INFO: Access modifiers changed from: package-private */
/* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/impl/AlterIndexImpl.class */
public final class AlterIndexImpl extends AbstractDDLQuery implements QOM.AlterIndex, AlterIndexOnStep, AlterIndexStep, AlterIndexFinalStep {
    final Index index;
    final boolean ifExists;
    Table<?> on;
    Index renameTo;
    private static final Clause[] CLAUSES = {Clause.ALTER_INDEX};
    private static final Set<SQLDialect> NO_SUPPORT_IF_EXISTS = SQLDialect.supportedBy(SQLDialect.CUBRID, SQLDialect.DERBY, SQLDialect.FIREBIRD, SQLDialect.MARIADB, SQLDialect.MYSQL);
    private static final Set<SQLDialect> SUPPORT_RENAME_INDEX = SQLDialect.supportedBy(SQLDialect.DERBY);

    @Override // org.jooq.AlterIndexOnStep
    public /* bridge */ /* synthetic */ AlterIndexStep on(Table table) {
        return on((Table<?>) table);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public AlterIndexImpl(Configuration configuration, Index index, boolean ifExists) {
        this(configuration, index, ifExists, null, null);
    }

    AlterIndexImpl(Configuration configuration, Index index, boolean ifExists, Table<?> on, Index renameTo) {
        super(configuration);
        this.index = index;
        this.ifExists = ifExists;
        this.on = on;
        this.renameTo = renameTo;
    }

    @Override // org.jooq.AlterIndexOnStep
    public final AlterIndexImpl on(String on) {
        return on((Table<?>) DSL.table(DSL.name(on)));
    }

    @Override // org.jooq.AlterIndexOnStep
    public final AlterIndexImpl on(Name on) {
        return on((Table<?>) DSL.table(on));
    }

    @Override // org.jooq.AlterIndexOnStep
    public final AlterIndexImpl on(Table<?> on) {
        this.on = on;
        return this;
    }

    @Override // org.jooq.AlterIndexStep
    public final AlterIndexImpl renameTo(String renameTo) {
        return renameTo(DSL.index(DSL.name(renameTo)));
    }

    @Override // org.jooq.AlterIndexStep
    public final AlterIndexImpl renameTo(Name renameTo) {
        return renameTo(DSL.index(renameTo));
    }

    @Override // org.jooq.AlterIndexStep
    public final AlterIndexImpl renameTo(Index renameTo) {
        this.renameTo = renameTo;
        return this;
    }

    private final boolean supportsIfExists(Context<?> ctx) {
        return !NO_SUPPORT_IF_EXISTS.contains(ctx.dialect());
    }

    @Override // org.jooq.QueryPartInternal
    public final void accept(Context<?> ctx) {
        if (this.ifExists && !supportsIfExists(ctx)) {
            Tools.tryCatch(ctx, DDLStatementType.ALTER_INDEX, c -> {
                accept0(c);
            });
        } else {
            accept0(ctx);
        }
    }

    /* JADX WARN: Type inference failed for: r0v16, types: [org.jooq.Context] */
    /* JADX WARN: Type inference failed for: r0v21, types: [org.jooq.Context] */
    /* JADX WARN: Type inference failed for: r0v27, types: [org.jooq.Context] */
    /* JADX WARN: Type inference failed for: r0v33, types: [org.jooq.Context] */
    /* JADX WARN: Type inference failed for: r0v36, types: [org.jooq.Context] */
    /* JADX WARN: Type inference failed for: r0v5, types: [org.jooq.Context] */
    private final void accept0(Context<?> ctx) {
        boolean renameIndex = SUPPORT_RENAME_INDEX.contains(ctx.dialect());
        switch (ctx.family()) {
            case MARIADB:
            case MYSQL:
                ctx.visit(Keywords.K_ALTER_TABLE).sql(' ').visit(this.on).sql(' ').visit(Keywords.K_RENAME_INDEX).sql(' ').qualify(false, c -> {
                    c.visit(this.index);
                }).sql(' ').visit(Keywords.K_TO).sql(' ').qualify(false, c2 -> {
                    c2.visit(this.renameTo);
                });
                return;
            default:
                ctx.start(Clause.ALTER_INDEX_INDEX).visit(renameIndex ? Keywords.K_RENAME_INDEX : Keywords.K_ALTER_INDEX);
                if (this.ifExists && supportsIfExists(ctx)) {
                    ctx.sql(' ').visit(Keywords.K_IF_EXISTS);
                }
                ctx.sql(' ');
                if (this.on != null) {
                    ctx.visit(this.on).sql('.').visit(this.index.getUnqualifiedName());
                } else {
                    ctx.visit(this.index);
                }
                ctx.end(Clause.ALTER_INDEX_INDEX).sql(' ');
                if (this.renameTo != null) {
                    ctx.start(Clause.ALTER_INDEX_RENAME).visit(renameIndex ? Keywords.K_TO : Keywords.K_RENAME_TO).sql(' ').qualify(false, c3 -> {
                        c3.visit(this.renameTo);
                    }).end(Clause.ALTER_INDEX_RENAME);
                    return;
                }
                return;
        }
    }

    @Override // org.jooq.impl.AbstractQueryPart, org.jooq.QueryPartInternal
    public final Clause[] clauses(Context<?> ctx) {
        return CLAUSES;
    }

    @Override // org.jooq.impl.QOM.AlterIndex
    public final Index $index() {
        return this.index;
    }

    @Override // org.jooq.impl.QOM.AlterIndex
    public final boolean $ifExists() {
        return this.ifExists;
    }

    @Override // org.jooq.impl.QOM.AlterIndex
    public final Table<?> $on() {
        return this.on;
    }

    @Override // org.jooq.impl.QOM.AlterIndex
    public final Index $renameTo() {
        return this.renameTo;
    }

    @Override // org.jooq.impl.QOM.AlterIndex
    public final QOM.AlterIndex $index(Index newValue) {
        return $constructor().apply(newValue, Boolean.valueOf($ifExists()), $on(), $renameTo());
    }

    @Override // org.jooq.impl.QOM.AlterIndex
    public final QOM.AlterIndex $ifExists(boolean newValue) {
        return $constructor().apply($index(), Boolean.valueOf(newValue), $on(), $renameTo());
    }

    @Override // org.jooq.impl.QOM.AlterIndex
    public final QOM.AlterIndex $on(Table<?> newValue) {
        return $constructor().apply($index(), Boolean.valueOf($ifExists()), newValue, $renameTo());
    }

    @Override // org.jooq.impl.QOM.AlterIndex
    public final QOM.AlterIndex $renameTo(Index newValue) {
        return $constructor().apply($index(), Boolean.valueOf($ifExists()), $on(), newValue);
    }

    public final Function4<? super Index, ? super Boolean, ? super Table<?>, ? super Index, ? extends QOM.AlterIndex> $constructor() {
        return (a1, a2, a3, a4) -> {
            return new AlterIndexImpl(configuration(), a1, a2.booleanValue(), a3, a4);
        };
    }
}
