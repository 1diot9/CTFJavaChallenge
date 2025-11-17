package org.jooq.impl;

import java.util.Set;
import org.jooq.Clause;
import org.jooq.Context;
import org.jooq.Row;
import org.jooq.SQLDialect;
import org.jooq.impl.QOM;

/* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/impl/RowIsNull.class */
final class RowIsNull extends AbstractCondition implements QOM.RowIsNull {
    static final Set<SQLDialect> EMULATE_NULL_ROW = SQLDialect.supportedBy(SQLDialect.CUBRID, SQLDialect.DERBY, SQLDialect.DUCKDB, SQLDialect.FIREBIRD, SQLDialect.HSQLDB, SQLDialect.MARIADB, SQLDialect.MYSQL, SQLDialect.SQLITE, SQLDialect.TRINO);
    private final Row row;

    /* JADX INFO: Access modifiers changed from: package-private */
    /* renamed from: org.jooq.impl.RowIsNull$1, reason: invalid class name */
    /* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/impl/RowIsNull$1.class */
    public static /* synthetic */ class AnonymousClass1 {
        static final /* synthetic */ int[] $SwitchMap$org$jooq$SQLDialect = new int[SQLDialect.values().length];
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public RowIsNull(Row row) {
        this.row = row;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    @Override // org.jooq.impl.AbstractField
    public final boolean isNullable() {
        return false;
    }

    @Override // org.jooq.impl.AbstractField, org.jooq.QueryPartInternal
    public final void accept(Context<?> ctx) {
        if (EMULATE_NULL_ROW.contains(ctx.dialect())) {
            ctx.visit(Tools.allNull(this.row.fields()));
        } else {
            acceptStandard(ctx);
        }
    }

    /* JADX WARN: Type inference failed for: r0v5, types: [org.jooq.Context] */
    private final void acceptStandard(Context<?> ctx) {
        ctx.visit(this.row);
        switch (AnonymousClass1.$SwitchMap$org$jooq$SQLDialect[ctx.family().ordinal()]) {
            default:
                ctx.sql(' ').visit(Keywords.K_IS_NULL);
                return;
        }
    }

    @Override // org.jooq.impl.AbstractCondition, org.jooq.impl.AbstractField, org.jooq.impl.AbstractQueryPart, org.jooq.QueryPartInternal
    public final Clause[] clauses(Context<?> ctx) {
        return null;
    }

    /* JADX WARN: Can't rename method to resolve collision */
    @Override // org.jooq.impl.QOM.UOperator1
    public final Row $arg1() {
        return this.row;
    }

    @Override // org.jooq.impl.QOM.UOperator1
    public final org.jooq.Function1<? super Row, ? extends QOM.RowIsNull> $constructor() {
        return r -> {
            return new RowIsNull(r);
        };
    }
}
