package org.jooq.impl;

import java.util.Set;
import org.jooq.Context;
import org.jooq.SQLDialect;
import org.jooq.impl.QOM;

/* JADX INFO: Access modifiers changed from: package-private */
/* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/impl/EmptyGroupingSet.class */
public final class EmptyGroupingSet extends AbstractField<Object> implements QOM.EmptyGroupingSet {
    static final Set<SQLDialect> EMULATE_EMPTY_GROUP_BY_CONSTANT = SQLDialect.supportedUntil(SQLDialect.DERBY, SQLDialect.HSQLDB, SQLDialect.IGNITE);
    static final Set<SQLDialect> EMULATE_EMPTY_GROUP_BY_OTHER = SQLDialect.supportedUntil(SQLDialect.FIREBIRD, SQLDialect.MARIADB, SQLDialect.MYSQL, SQLDialect.SQLITE, SQLDialect.YUGABYTEDB);
    static final EmptyGroupingSet INSTANCE = new EmptyGroupingSet();

    private EmptyGroupingSet() {
        super(DSL.name("()"), SQLDataType.OTHER);
    }

    /* JADX WARN: Type inference failed for: r0v9, types: [org.jooq.Context] */
    @Override // org.jooq.impl.AbstractField, org.jooq.QueryPartInternal
    public final void accept(Context<?> ctx) {
        if (EMULATE_EMPTY_GROUP_BY_CONSTANT.contains(ctx.dialect())) {
            ctx.sql('0');
            return;
        }
        if (ctx.family() == SQLDialect.CUBRID) {
            ctx.sql("1 + 0");
        } else if (EMULATE_EMPTY_GROUP_BY_OTHER.contains(ctx.dialect())) {
            ctx.sql('(').visit(DSL.select(DSL.one())).sql(')');
        } else {
            ctx.sql("()");
        }
    }
}
