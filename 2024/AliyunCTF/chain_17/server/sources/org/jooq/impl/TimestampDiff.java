package org.jooq.impl;

import org.jooq.Context;
import org.jooq.Field;
import org.jooq.Function2;
import org.jooq.impl.QOM;
import org.jooq.types.DayToSecond;

/* JADX INFO: Access modifiers changed from: package-private */
/* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/impl/TimestampDiff.class */
public final class TimestampDiff<T> extends AbstractField<DayToSecond> implements QOM.TimestampDiff<T> {
    private final Field<T> timestamp1;
    private final Field<T> timestamp2;

    /* JADX INFO: Access modifiers changed from: package-private */
    public TimestampDiff(Field<T> timestamp1, Field<T> timestamp2) {
        super(Names.N_TIMESTAMPDIFF, SQLDataType.INTERVALDAYTOSECOND);
        this.timestamp1 = timestamp1;
        this.timestamp2 = timestamp2;
    }

    /* JADX WARN: Type inference failed for: r0v15, types: [org.jooq.Context] */
    /* JADX WARN: Type inference failed for: r0v24, types: [org.jooq.Context] */
    /* JADX WARN: Type inference failed for: r0v31, types: [org.jooq.Context] */
    /* JADX WARN: Type inference failed for: r0v40, types: [org.jooq.Context] */
    /* JADX WARN: Type inference failed for: r0v5, types: [org.jooq.Context] */
    /* JADX WARN: Type inference failed for: r0v52, types: [org.jooq.Context] */
    @Override // org.jooq.impl.AbstractField, org.jooq.QueryPartInternal
    public final void accept(Context<?> context) {
        switch (context.family()) {
            case POSTGRES:
            case YUGABYTEDB:
                context.sql('(').visit(this.timestamp1).sql(" - ").visit((Field<?>) this.timestamp2).sql(')');
                return;
            case CUBRID:
                context.visit(this.timestamp1.sub((Field<?>) this.timestamp2));
                return;
            case DERBY:
                context.sql("1000 * {fn ").visit(Names.N_TIMESTAMPDIFF).sql('(').visit(DSL.keyword("sql_tsi_second")).sql(", ").visit((Field<?>) this.timestamp2).sql(", ").visit((Field<?>) this.timestamp1).sql(") }");
                return;
            case FIREBIRD:
                context.visit(Names.N_DATEDIFF).sql('(').visit(Keywords.K_MILLISECOND).sql(", ").visit((Field<?>) this.timestamp2).sql(", ").visit((Field<?>) this.timestamp1).sql(')');
                return;
            case H2:
            case HSQLDB:
                context.visit(Names.N_DATEDIFF).sql("('ms', ").visit((Field<?>) this.timestamp2).sql(", ").visit((Field<?>) this.timestamp1).sql(')');
                return;
            case MARIADB:
            case MYSQL:
                context.visit(Names.N_TIMESTAMPDIFF).sql('(').visit(DSL.keyword("microsecond")).sql(", ").visit((Field<?>) this.timestamp2).sql(", ").visit((Field<?>) this.timestamp1).sql(") / 1000");
                return;
            case SQLITE:
                context.sql('(').visit(Names.N_STRFTIME).sql("('%s', ").visit((Field<?>) this.timestamp1).sql(") - ").visit(Names.N_STRFTIME).sql("('%s', ").visit((Field<?>) this.timestamp2).sql(")) * 1000");
                return;
            default:
                context.visit(Tools.castIfNeeded((Field<?>) this.timestamp1.sub((Field<?>) this.timestamp2), DayToSecond.class));
                return;
        }
    }

    @Override // org.jooq.impl.QOM.UOperator2
    public final Field<T> $arg1() {
        return this.timestamp1;
    }

    @Override // org.jooq.impl.QOM.UOperator2
    public final Field<T> $arg2() {
        return this.timestamp2;
    }

    @Override // org.jooq.impl.QOM.UOperator2
    public final Function2<? super Field<T>, ? super Field<T>, ? extends QOM.TimestampDiff<T>> $constructor() {
        return (t1, t2) -> {
            return new TimestampDiff(t1, t2);
        };
    }
}
