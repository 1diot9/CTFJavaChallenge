package org.jooq.impl;

import java.util.Set;
import org.jooq.Clause;
import org.jooq.Context;
import org.jooq.DataType;
import org.jooq.Field;
import org.jooq.Function2;
import org.jooq.Row;
import org.jooq.Row2;
import org.jooq.SQLDialect;
import org.jooq.impl.QOM;

/* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/impl/RowOverlaps.class */
final class RowOverlaps<T1, T2> extends AbstractCondition implements QOM.RowOverlaps {
    private static final Set<SQLDialect> EMULATE_NON_STANDARD_OVERLAPS = SQLDialect.supportedUntil(SQLDialect.CUBRID, SQLDialect.DERBY, SQLDialect.DUCKDB, SQLDialect.FIREBIRD, SQLDialect.H2, SQLDialect.MARIADB, SQLDialect.MYSQL, SQLDialect.SQLITE, SQLDialect.TRINO);
    private static final Set<SQLDialect> EMULATE_INTERVAL_OVERLAPS = SQLDialect.supportedBy(SQLDialect.DUCKDB, SQLDialect.HSQLDB, SQLDialect.TRINO);
    private final Row2<T1, T2> left;
    private final Row2<T1, T2> right;

    /* JADX INFO: Access modifiers changed from: package-private */
    /* JADX WARN: Multi-variable type inference failed */
    public RowOverlaps(Row2<T1, T2> row2, Row2<T1, T2> row22) {
        this.left = (Row2) ((AbstractRow) row2).convertTo(row22);
        this.right = (Row2) ((AbstractRow) row22).convertTo(row2);
    }

    /* JADX WARN: Type inference failed for: r0v34, types: [org.jooq.Context] */
    @Override // org.jooq.impl.AbstractField, org.jooq.QueryPartInternal
    public final void accept(Context<?> ctx) {
        Field<T1> left1 = this.left.field1();
        Field<?> field2 = this.left.field2();
        Field<T1> right1 = this.right.field1();
        Field<?> field22 = this.right.field2();
        DataType<T1> dataType = left1.getDataType();
        DataType<?> type1 = field2.getDataType();
        boolean standardOverlaps = dataType.isDateTime() && type1.isTemporal();
        boolean intervalOverlaps = dataType.isDateTime() && (type1.isInterval() || type1.isNumeric());
        if (!standardOverlaps || EMULATE_NON_STANDARD_OVERLAPS.contains(ctx.dialect())) {
            if (intervalOverlaps) {
                ctx.visit(right1.le(left1.plus(field2)).and(left1.le(right1.plus(field22))));
                return;
            } else {
                ctx.visit(right1.le(Tools.castIfNeeded(field2, right1)).and(left1.le(Tools.castIfNeeded(field22, left1))));
                return;
            }
        }
        if (intervalOverlaps && EMULATE_INTERVAL_OVERLAPS.contains(ctx.dialect())) {
            ctx.visit(right1.le(left1.plus(field2)).and(left1.le(right1.plus(field22))));
        } else {
            ctx.sql('(').visit(this.left).sql(' ').visit(Keywords.K_OVERLAPS).sql(' ').visit(this.right).sql(')');
        }
    }

    @Override // org.jooq.impl.AbstractCondition, org.jooq.impl.AbstractField, org.jooq.impl.AbstractQueryPart, org.jooq.QueryPartInternal
    public final Clause[] clauses(Context<?> ctx) {
        return null;
    }

    /* JADX WARN: Can't rename method to resolve collision */
    @Override // org.jooq.impl.QOM.UOperator2
    public final Row $arg1() {
        return this.left;
    }

    /* JADX WARN: Can't rename method to resolve collision */
    @Override // org.jooq.impl.QOM.UOperator2
    public final Row $arg2() {
        return this.right;
    }

    @Override // org.jooq.impl.QOM.UOperator2
    public final Function2<? super Row, ? super Row, ? extends QOM.RowOverlaps> $constructor() {
        return (r1, r2) -> {
            return new RowOverlaps((Row2) r1, (Row2) r2);
        };
    }
}
