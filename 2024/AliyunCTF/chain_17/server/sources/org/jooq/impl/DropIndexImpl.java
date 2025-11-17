package org.jooq.impl;

import java.util.Set;
import org.jooq.Clause;
import org.jooq.Configuration;
import org.jooq.Context;
import org.jooq.DropIndexCascadeStep;
import org.jooq.DropIndexFinalStep;
import org.jooq.DropIndexOnStep;
import org.jooq.Function4;
import org.jooq.Index;
import org.jooq.Name;
import org.jooq.SQLDialect;
import org.jooq.Table;
import org.jooq.impl.QOM;

/* JADX INFO: Access modifiers changed from: package-private */
/* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/impl/DropIndexImpl.class */
public final class DropIndexImpl extends AbstractDDLQuery implements QOM.DropIndex, DropIndexOnStep, DropIndexCascadeStep, DropIndexFinalStep {
    final Index index;
    final boolean ifExists;
    Table<?> on;
    QOM.Cascade cascade;
    private static final Clause[] CLAUSES = {Clause.DROP_INDEX};
    private static final Set<SQLDialect> NO_SUPPORT_IF_EXISTS = SQLDialect.supportedUntil(SQLDialect.CUBRID, SQLDialect.DERBY, SQLDialect.FIREBIRD, SQLDialect.MYSQL);
    private static final Set<SQLDialect> REQUIRES_ON = SQLDialect.supportedBy(SQLDialect.MARIADB, SQLDialect.MYSQL);

    @Override // org.jooq.DropIndexOnStep
    public /* bridge */ /* synthetic */ DropIndexCascadeStep on(Table table) {
        return on((Table<?>) table);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public DropIndexImpl(Configuration configuration, Index index, boolean ifExists) {
        this(configuration, index, ifExists, null, null);
    }

    DropIndexImpl(Configuration configuration, Index index, boolean ifExists, Table<?> on, QOM.Cascade cascade) {
        super(configuration);
        this.index = index;
        this.ifExists = ifExists;
        this.on = on;
        this.cascade = cascade;
    }

    @Override // org.jooq.DropIndexOnStep
    public final DropIndexImpl on(String on) {
        return on((Table<?>) DSL.table(DSL.name(on)));
    }

    @Override // org.jooq.DropIndexOnStep
    public final DropIndexImpl on(Name on) {
        return on((Table<?>) DSL.table(on));
    }

    @Override // org.jooq.DropIndexOnStep
    public final DropIndexImpl on(Table<?> on) {
        this.on = on;
        return this;
    }

    @Override // org.jooq.DropIndexCascadeStep
    public final DropIndexImpl cascade() {
        this.cascade = QOM.Cascade.CASCADE;
        return this;
    }

    @Override // org.jooq.DropIndexCascadeStep
    public final DropIndexImpl restrict() {
        this.cascade = QOM.Cascade.RESTRICT;
        return this;
    }

    private final boolean supportsIfExists(Context<?> ctx) {
        return !NO_SUPPORT_IF_EXISTS.contains(ctx.dialect());
    }

    @Override // org.jooq.QueryPartInternal
    public final void accept(Context<?> ctx) {
        if (this.ifExists && !supportsIfExists(ctx)) {
            Tools.tryCatch(ctx, DDLStatementType.DROP_INDEX, c -> {
                accept0(c);
            });
        } else {
            accept0(ctx);
        }
    }

    /* JADX WARN: Type inference failed for: r0v1, types: [org.jooq.Context] */
    /* JADX WARN: Type inference failed for: r0v18, types: [org.jooq.Context] */
    /* JADX WARN: Type inference failed for: r0v23, types: [org.jooq.Context] */
    /* JADX WARN: Type inference failed for: r0v30, types: [org.jooq.Context] */
    private void accept0(Context<?> ctx) {
        ctx.visit(Keywords.K_DROP).sql(' ').visit(Keywords.K_INDEX).sql(' ');
        if (this.ifExists && supportsIfExists(ctx)) {
            ctx.visit(Keywords.K_IF_EXISTS).sql(' ');
        }
        ctx.visit(this.index);
        if (REQUIRES_ON.contains(ctx.dialect())) {
            if (this.on != null) {
                ctx.sql(' ').visit(Keywords.K_ON).sql(' ').visit(this.on);
            } else if (this.index.getTable() != null) {
                ctx.sql(' ').visit(Keywords.K_ON).sql(' ').visit(this.index.getTable());
            }
        }
        acceptCascade(ctx, this.cascade);
    }

    @Override // org.jooq.impl.AbstractQueryPart, org.jooq.QueryPartInternal
    public final Clause[] clauses(Context<?> ctx) {
        return CLAUSES;
    }

    @Override // org.jooq.impl.QOM.DropIndex
    public final Index $index() {
        return this.index;
    }

    @Override // org.jooq.impl.QOM.DropIndex
    public final boolean $ifExists() {
        return this.ifExists;
    }

    @Override // org.jooq.impl.QOM.DropIndex
    public final Table<?> $on() {
        return this.on;
    }

    @Override // org.jooq.impl.QOM.DropIndex
    public final QOM.Cascade $cascade() {
        return this.cascade;
    }

    @Override // org.jooq.impl.QOM.DropIndex
    public final QOM.DropIndex $index(Index newValue) {
        return $constructor().apply(newValue, Boolean.valueOf($ifExists()), $on(), $cascade());
    }

    @Override // org.jooq.impl.QOM.DropIndex
    public final QOM.DropIndex $ifExists(boolean newValue) {
        return $constructor().apply($index(), Boolean.valueOf(newValue), $on(), $cascade());
    }

    @Override // org.jooq.impl.QOM.DropIndex
    public final QOM.DropIndex $on(Table<?> newValue) {
        return $constructor().apply($index(), Boolean.valueOf($ifExists()), newValue, $cascade());
    }

    @Override // org.jooq.impl.QOM.DropIndex
    public final QOM.DropIndex $cascade(QOM.Cascade newValue) {
        return $constructor().apply($index(), Boolean.valueOf($ifExists()), $on(), newValue);
    }

    public final Function4<? super Index, ? super Boolean, ? super Table<?>, ? super QOM.Cascade, ? extends QOM.DropIndex> $constructor() {
        return (a1, a2, a3, a4) -> {
            return new DropIndexImpl(configuration(), a1, a2.booleanValue(), a3, a4);
        };
    }
}
