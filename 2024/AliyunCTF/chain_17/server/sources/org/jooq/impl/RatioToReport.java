package org.jooq.impl;

import java.math.BigDecimal;
import org.jooq.Context;
import org.jooq.DataType;
import org.jooq.Field;
import org.jooq.impl.QOM;

/* JADX INFO: Access modifiers changed from: package-private */
/* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/impl/RatioToReport.class */
public final class RatioToReport extends AbstractAggregateFunction<BigDecimal> implements QOM.RatioToReport {
    private final Field<? extends Number> field;

    /* JADX INFO: Access modifiers changed from: package-private */
    public RatioToReport(Field<? extends Number> field) {
        super(Names.N_RATIO_TO_REPORT, SQLDataType.DECIMAL, (Field<?>[]) new Field[]{field});
        this.field = field;
    }

    /* JADX WARN: Type inference failed for: r0v14, types: [org.jooq.Context] */
    /* JADX WARN: Type inference failed for: r0v3, types: [org.jooq.Context] */
    @Override // org.jooq.impl.AbstractAggregateFunction, org.jooq.impl.AbstractField, org.jooq.QueryPartInternal
    public final void accept(Context<?> ctx) {
        DataType<?> cast;
        switch (ctx.family()) {
            case SQLITE:
            case FIREBIRD:
            case TRINO:
            case CUBRID:
            case DUCKDB:
            case MARIADB:
            case MYSQL:
            case POSTGRES:
            case YUGABYTEDB:
                switch (ctx.family()) {
                    case SQLITE:
                        cast = SQLDataType.DOUBLE;
                        break;
                    case FIREBIRD:
                    case TRINO:
                        cast = SQLDataType.DECIMAL(38, 19);
                        break;
                    default:
                        cast = SQLDataType.DECIMAL;
                        break;
                }
                ctx.visit(Tools.castIfNeeded(this.field, cast)).sql(" / ").visit((Field<?>) DSL.sum(this.field));
                acceptOverClause(ctx);
                return;
            default:
                ctx.visit(Names.N_RATIO_TO_REPORT).sql('(').visit((Field<?>) this.field).sql(')');
                acceptOverClause(ctx);
                return;
        }
    }

    @Override // org.jooq.impl.QOM.RatioToReport
    public final Field<? extends Number> $field() {
        return this.field;
    }
}
