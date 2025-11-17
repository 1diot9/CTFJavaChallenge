package org.jooq.impl;

import org.jooq.Context;
import org.jooq.Field;
import org.jooq.Function0;
import org.jooq.impl.QOM;

/* JADX INFO: Access modifiers changed from: package-private */
/* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/impl/CurrentSchema.class */
public final class CurrentSchema extends AbstractField<String> implements QOM.CurrentSchema {
    /* JADX INFO: Access modifiers changed from: package-private */
    public CurrentSchema() {
        super(Names.N_CURRENT_SCHEMA, Tools.allNotNull(SQLDataType.VARCHAR));
    }

    /* JADX WARN: Type inference failed for: r0v11, types: [org.jooq.Context] */
    /* JADX WARN: Type inference failed for: r0v14, types: [org.jooq.Context] */
    /* JADX WARN: Type inference failed for: r0v3, types: [org.jooq.Context] */
    /* JADX WARN: Type inference failed for: r0v8, types: [org.jooq.Context] */
    @Override // org.jooq.impl.AbstractField, org.jooq.QueryPartInternal
    public final void accept(Context<?> ctx) {
        switch (ctx.family()) {
            case CUBRID:
            case FIREBIRD:
            case SQLITE:
                ctx.visit((Field<?>) DSL.inline(""));
                return;
            case DERBY:
                ctx.visit(Keywords.K_CURRENT).sql(' ').visit(Keywords.K_SCHEMA);
                return;
            case H2:
                ctx.visit(Keywords.K_SCHEMA).sql("()");
                return;
            case MARIADB:
            case MYSQL:
                ctx.visit(Keywords.K_DATABASE).sql("()");
                return;
            case HSQLDB:
            case POSTGRES:
            case YUGABYTEDB:
                ctx.visit(Keywords.K_CURRENT_SCHEMA);
                return;
            default:
                ctx.visit(Keywords.K_CURRENT_SCHEMA).sql("()");
                return;
        }
    }

    @Override // org.jooq.impl.QOM.UOperator0
    public final Function0<? extends QOM.CurrentSchema> $constructor() {
        return () -> {
            return new CurrentSchema();
        };
    }

    @Override // org.jooq.impl.AbstractNamed, org.jooq.impl.AbstractQueryPart, org.jooq.QueryPart
    public boolean equals(Object that) {
        if (that instanceof QOM.CurrentSchema) {
            return true;
        }
        return super.equals(that);
    }
}
