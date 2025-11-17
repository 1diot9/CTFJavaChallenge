package org.jooq.impl;

import java.util.Set;
import org.jooq.Context;
import org.jooq.Field;
import org.jooq.SQLDialect;
import org.jooq.impl.QOM;
import org.jooq.impl.Tools;

/* JADX INFO: Access modifiers changed from: package-private */
/* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/impl/ListAgg.class */
public final class ListAgg extends AbstractAggregateFunction<String> implements QOM.UNotYetImplemented {
    private static final Set<SQLDialect> SET_GROUP_CONCAT_MAX_LEN = SQLDialect.supportedBy(SQLDialect.MARIADB, SQLDialect.MYSQL);
    private static final Set<SQLDialect> SUPPORT_GROUP_CONCAT = SQLDialect.supportedBy(SQLDialect.CUBRID, SQLDialect.H2, SQLDialect.HSQLDB, SQLDialect.MARIADB, SQLDialect.MYSQL, SQLDialect.SQLITE);
    private static final Set<SQLDialect> SUPPORT_STRING_AGG = SQLDialect.supportedBy(SQLDialect.POSTGRES);

    /* JADX INFO: Access modifiers changed from: package-private */
    public ListAgg(boolean distinct, Field<?> arg) {
        super(distinct, Names.N_LISTAGG, SQLDataType.VARCHAR, (Field<?>[]) new Field[]{arg});
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public ListAgg(boolean distinct, Field<?> arg, Field<String> separator) {
        super(distinct, Names.N_LISTAGG, SQLDataType.VARCHAR, (Field<?>[]) new Field[]{arg, separator});
    }

    /* JADX WARN: Type inference failed for: r0v16, types: [org.jooq.Context] */
    @Override // org.jooq.impl.AbstractAggregateFunction, org.jooq.impl.AbstractField, org.jooq.QueryPartInternal
    public final void accept(Context<?> ctx) {
        if (SUPPORT_GROUP_CONCAT.contains(ctx.dialect())) {
            if (SET_GROUP_CONCAT_MAX_LEN.contains(ctx.dialect()) && !Boolean.FALSE.equals(ctx.settings().isRenderGroupConcatMaxLenSessionVariable()) && ctx.data(Tools.BooleanDataKey.DATA_GROUP_CONCAT_MAX_LEN_SET) == null) {
                ctx.skipUpdateCounts(2).data(Tools.BooleanDataKey.DATA_GROUP_CONCAT_MAX_LEN_SET, true);
                Tools.prependSQL(ctx, DSL.query("{set} @t = @@group_concat_max_len"), DSL.query("{set} @@group_concat_max_len = 4294967295"));
                acceptGroupConcat(ctx);
                Tools.appendSQL(ctx, DSL.query("{set} @@group_concat_max_len = @t"));
                return;
            }
            acceptGroupConcat(ctx);
            return;
        }
        if (SUPPORT_STRING_AGG.contains(ctx.dialect())) {
            acceptStringAgg(ctx);
            acceptFilterClause(ctx);
            acceptOverClause(ctx);
            return;
        }
        super.accept(ctx);
    }

    @Override // org.jooq.impl.AbstractAggregateFunction
    void acceptFunctionName(Context<?> ctx) {
        super.acceptFunctionName(ctx);
    }

    @Override // org.jooq.impl.AbstractAggregateFunction
    final Field<?> applyMap(Context<?> ctx, Field<?> arg) {
        switch (ctx.family()) {
            case TRINO:
                return arg.getDataType().isString() ? arg : arg.cast(SQLDataType.VARCHAR);
            default:
                return arg;
        }
    }

    @Override // org.jooq.impl.AbstractAggregateFunction
    final boolean applyFilter(Context<?> ctx, Field<?> arg, int i) {
        return i == 0;
    }

    @Override // org.jooq.impl.AbstractAggregateFunction
    boolean supportsFilter(Context<?> ctx) {
        switch (ctx.family()) {
            case TRINO:
                return false;
            default:
                return super.supportsFilter(ctx);
        }
    }

    /* JADX WARN: Type inference failed for: r0v1, types: [org.jooq.Context] */
    /* JADX WARN: Type inference failed for: r0v15, types: [org.jooq.Context] */
    /* JADX WARN: Type inference failed for: r0v20, types: [org.jooq.Context] */
    private final void acceptGroupConcat(Context<?> ctx) {
        ctx.visit(Names.N_GROUP_CONCAT).sql('(');
        acceptArguments1(ctx, new QueryPartListView(this.arguments.get(0)));
        acceptOrderBy(ctx);
        if (this.arguments.size() > 1) {
            if (ctx.family() == SQLDialect.SQLITE) {
                ctx.sql(", ").visit(this.arguments.get(1));
            } else {
                ctx.sql(' ').visit(Keywords.K_SEPARATOR).sql(' ').visit(this.arguments.get(1));
            }
        }
        ctx.sql(')');
        acceptFilterClause(ctx);
        acceptOverClause(ctx);
    }

    /* JADX WARN: Type inference failed for: r0v14, types: [org.jooq.Context] */
    /* JADX WARN: Type inference failed for: r0v20, types: [org.jooq.Context] */
    private final void acceptStringAgg(Context<?> ctx) {
        switch (ctx.family()) {
            default:
                ctx.visit(Names.N_STRING_AGG);
                ctx.sql('(');
                QueryPartListView<Field<?>> args = QueryPartListView.wrap(Tools.castIfNeeded(this.arguments.get(0), String.class));
                acceptArguments1(ctx, args);
                if (this.arguments.size() > 1) {
                    ctx.sql(", ").visit(this.arguments.get(1));
                } else {
                    ctx.sql(", ").visit(DSL.inline(""));
                }
                acceptOrderBy(ctx);
                ctx.sql(')');
                return;
        }
    }
}
