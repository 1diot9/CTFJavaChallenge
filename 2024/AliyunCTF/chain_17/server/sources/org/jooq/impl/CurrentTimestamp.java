package org.jooq.impl;

import java.util.Set;
import org.jooq.Context;
import org.jooq.DataType;
import org.jooq.Field;
import org.jooq.SQLDialect;
import org.jooq.conf.ParamType;
import org.jooq.impl.QOM;

/* JADX INFO: Access modifiers changed from: package-private */
/* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/impl/CurrentTimestamp.class */
public final class CurrentTimestamp<T> extends AbstractField<T> implements QOM.CurrentTimestamp<T> {
    private static final Set<SQLDialect> NO_SUPPORT_PRECISION = SQLDialect.supportedBy(SQLDialect.CUBRID, SQLDialect.DERBY, SQLDialect.SQLITE);
    private static final Set<SQLDialect> NO_SUPPORT_PRECISION_BIND = SQLDialect.supportedBy(SQLDialect.FIREBIRD, SQLDialect.H2, SQLDialect.HSQLDB, SQLDialect.POSTGRES);
    private final Field<Integer> precision;

    /* JADX INFO: Access modifiers changed from: package-private */
    public CurrentTimestamp(DataType<T> type) {
        this(type, null);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public CurrentTimestamp(DataType<T> type, Field<Integer> precision) {
        super(Names.N_CURRENT_TIMESTAMP, type);
        this.precision = precision;
    }

    /* JADX WARN: Type inference failed for: r0v13, types: [org.jooq.Context] */
    /* JADX WARN: Type inference failed for: r0v20, types: [org.jooq.Context] */
    /* JADX WARN: Type inference failed for: r0v29, types: [org.jooq.Context] */
    /* JADX WARN: Type inference failed for: r0v34, types: [org.jooq.Context] */
    /* JADX WARN: Type inference failed for: r0v5, types: [org.jooq.Context] */
    @Override // org.jooq.impl.AbstractField, org.jooq.QueryPartInternal
    public final void accept(Context<?> ctx) {
        switch (ctx.family()) {
            case MARIADB:
            case MYSQL:
                if (this.precision != null && !NO_SUPPORT_PRECISION.contains(ctx.dialect())) {
                    ctx.visit(Names.N_CURRENT_TIMESTAMP).sql('(').visit((Field<?>) this.precision).sql(')');
                    return;
                } else {
                    ctx.visit(Names.N_CURRENT_TIMESTAMP).sql("()");
                    return;
                }
            default:
                if (this.precision != null && !NO_SUPPORT_PRECISION.contains(ctx.dialect())) {
                    if (NO_SUPPORT_PRECISION_BIND.contains(ctx.dialect())) {
                        ctx.visit(Keywords.K_CURRENT).sql('_').visit(Keywords.K_TIMESTAMP).sql('(').paramType(ParamType.INLINED, c -> {
                            c.visit((Field<?>) this.precision);
                        }).sql(')');
                        return;
                    } else {
                        ctx.visit(Keywords.K_CURRENT).sql('_').visit(Keywords.K_TIMESTAMP).sql('(').visit((Field<?>) this.precision).sql(')');
                        return;
                    }
                }
                ctx.visit(Keywords.K_CURRENT).sql('_').visit(Keywords.K_TIMESTAMP);
                return;
        }
    }
}
