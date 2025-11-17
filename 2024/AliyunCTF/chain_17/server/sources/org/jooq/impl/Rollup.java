package org.jooq.impl;

import java.util.List;
import java.util.Set;
import org.jooq.Context;
import org.jooq.FieldOrRow;
import org.jooq.SQLDialect;
import org.jooq.impl.QOM;

/* JADX INFO: Access modifiers changed from: package-private */
/* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/impl/Rollup.class */
public final class Rollup extends AbstractField<Object> implements QOM.Rollup {
    static final Set<SQLDialect> SUPPORT_WITH_ROLLUP = SQLDialect.supportedBy(SQLDialect.CUBRID, SQLDialect.MARIADB, SQLDialect.MYSQL);
    private QueryPartList<FieldOrRow> arguments;

    /* JADX INFO: Access modifiers changed from: package-private */
    public Rollup(FieldOrRow... arguments) {
        super(Names.N_ROLLUP, SQLDataType.OTHER);
        this.arguments = new QueryPartList<>(arguments);
    }

    /* JADX WARN: Type inference failed for: r0v3, types: [org.jooq.Context] */
    /* JADX WARN: Type inference failed for: r0v8, types: [org.jooq.Context] */
    @Override // org.jooq.impl.AbstractField, org.jooq.QueryPartInternal
    public final void accept(Context<?> ctx) {
        if (SUPPORT_WITH_ROLLUP.contains(ctx.dialect())) {
            ctx.visit(this.arguments).formatSeparator().visit(Keywords.K_WITH_ROLLUP);
        } else {
            ctx.visit(Keywords.K_ROLLUP).sql(" (").visit(this.arguments).sql(')');
        }
    }

    /* JADX WARN: Can't rename method to resolve collision */
    @Override // org.jooq.impl.QOM.UOperator1
    public final QOM.UnmodifiableList<? extends FieldOrRow> $arg1() {
        return QOM.unmodifiable((List) this.arguments);
    }

    @Override // org.jooq.impl.QOM.UOperator1
    public final org.jooq.Function1<? super QOM.UnmodifiableList<? extends FieldOrRow>, ? extends QOM.Rollup> $constructor() {
        return l -> {
            return new Rollup((FieldOrRow[]) l.toArray(Tools.EMPTY_FIELD_OR_ROW));
        };
    }
}
