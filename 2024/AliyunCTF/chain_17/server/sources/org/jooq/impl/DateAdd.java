package org.jooq.impl;

import org.jooq.Context;
import org.jooq.DatePart;
import org.jooq.Field;
import org.jooq.Function3;
import org.jooq.Keyword;
import org.jooq.impl.QOM;
import org.jooq.tools.StringUtils;

/* JADX INFO: Access modifiers changed from: package-private */
/* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/impl/DateAdd.class */
public final class DateAdd<T> extends AbstractField<T> implements QOM.DateAdd<T> {
    final Field<T> date;
    final Field<? extends Number> interval;
    final DatePart datePart;

    /* JADX INFO: Access modifiers changed from: package-private */
    public DateAdd(Field<T> date, Field<? extends Number> interval) {
        super(Names.N_DATE_ADD, Tools.allNotNull(Tools.dataType(date), date, interval));
        this.date = Tools.nullSafeNotNull(date, SQLDataType.OTHER);
        this.interval = Tools.nullSafeNotNull(interval, SQLDataType.INTEGER);
        this.datePart = null;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public DateAdd(Field<T> date, Field<? extends Number> interval, DatePart datePart) {
        super(Names.N_DATE_ADD, Tools.allNotNull(Tools.dataType(date), date, interval));
        this.date = Tools.nullSafeNotNull(date, SQLDataType.OTHER);
        this.interval = Tools.nullSafeNotNull(interval, SQLDataType.INTEGER);
        this.datePart = datePart;
    }

    @Override // org.jooq.impl.AbstractField, org.jooq.QueryPartInternal
    public final void accept(Context<?> ctx) {
        if (this.datePart == null) {
            ctx.visit(this.date.add(this.interval));
        } else {
            accept0(ctx);
        }
    }

    /* JADX WARN: Type inference failed for: r0v102, types: [org.jooq.Context] */
    /* JADX WARN: Type inference failed for: r0v126, types: [org.jooq.Context] */
    /* JADX WARN: Type inference failed for: r0v138, types: [org.jooq.Context] */
    /* JADX WARN: Type inference failed for: r0v15, types: [org.jooq.Context] */
    /* JADX WARN: Type inference failed for: r0v33, types: [org.jooq.Context] */
    /* JADX WARN: Type inference failed for: r0v58, types: [org.jooq.Context] */
    /* JADX WARN: Type inference failed for: r0v6, types: [org.jooq.Context] */
    /* JADX WARN: Type inference failed for: r0v70, types: [org.jooq.Context] */
    /* JADX WARN: Type inference failed for: r0v80, types: [org.jooq.Context] */
    /* JADX WARN: Type inference failed for: r0v86, types: [org.jooq.Context] */
    private final void accept0(Context<?> ctx) {
        String string;
        String string2;
        String string3;
        Keyword keyword;
        switch (ctx.family()) {
            case CUBRID:
            case MARIADB:
            case MYSQL:
                ctx.visit(Names.N_DATE_ADD).sql('(').visit((Field<?>) this.date).sql(", ").visit(Keywords.K_INTERVAL).sql(' ').visit((Field<?>) this.interval).sql(' ').visit(standardKeyword()).sql(')');
                return;
            case DERBY:
            case HSQLDB:
                switch (this.datePart) {
                    case YEAR:
                        keyword = DSL.keyword("sql_tsi_year");
                        break;
                    case MONTH:
                        keyword = DSL.keyword("sql_tsi_month");
                        break;
                    case DAY:
                        keyword = DSL.keyword("sql_tsi_day");
                        break;
                    case HOUR:
                        keyword = DSL.keyword("sql_tsi_hour");
                        break;
                    case MINUTE:
                        keyword = DSL.keyword("sql_tsi_minute");
                        break;
                    case SECOND:
                        keyword = DSL.keyword("sql_tsi_second");
                        break;
                    default:
                        throw unsupported();
                }
                ctx.sql("{fn ").visit(Names.N_TIMESTAMPADD).sql('(').visit(keyword).sql(", ").visit((Field<?>) this.interval).sql(", ").visit((Field<?>) this.date).sql(") }");
                return;
            case H2:
                switch (this.datePart) {
                    case YEAR:
                        string3 = "year";
                        break;
                    case MONTH:
                        string3 = "month";
                        break;
                    case DAY:
                        string3 = "day";
                        break;
                    case HOUR:
                        string3 = "hour";
                        break;
                    case MINUTE:
                        string3 = "minute";
                        break;
                    case SECOND:
                        string3 = "second";
                        break;
                    default:
                        throw unsupported();
                }
                ctx.visit(Names.N_DATEADD).sql('(').visit((Field<?>) DSL.inline(string3)).sql(", ").visit((Field<?>) this.interval).sql(", ").visit((Field<?>) this.date).sql(')');
                return;
            case POSTGRES:
            case YUGABYTEDB:
                switch (this.datePart) {
                    case YEAR:
                        string2 = "1 year";
                        break;
                    case MONTH:
                        string2 = "1 month";
                        break;
                    case DAY:
                        string2 = "1 day";
                        break;
                    case HOUR:
                        string2 = "1 hour";
                        break;
                    case MINUTE:
                        string2 = "1 minute";
                        break;
                    case SECOND:
                        string2 = "1 second";
                        break;
                    default:
                        throw unsupported();
                }
                if (((AbstractField) this.interval).getExpressionDataType().isInterval()) {
                    ctx.sql('(').visit(this.date).sql(" + ").visit((Field<?>) this.interval).sql(')');
                    return;
                }
                if (getDataType().isDate()) {
                    if (this.datePart == DatePart.DAY) {
                        ctx.sql('(').visit(this.date).sql(" + ").visit((Field<?>) this.interval).sql(')');
                        return;
                    } else {
                        ctx.sql("cast((").visit(this.date).sql(" + ").visit((Field<?>) this.interval).sql(" * ").visit(Keywords.K_INTERVAL).sql(' ').visit((Field<?>) DSL.inline(string2)).sql(") as date)");
                        return;
                    }
                }
                ctx.sql('(').visit(this.date).sql(" + ").visit((Field<?>) this.interval).sql(" * ").visit(Keywords.K_INTERVAL).sql(' ').visit((Field<?>) DSL.inline(string2)).sql(")");
                return;
            case SQLITE:
                switch (this.datePart) {
                    case YEAR:
                        string = " year";
                        break;
                    case MONTH:
                        string = " month";
                        break;
                    case DAY:
                        string = " day";
                        break;
                    case HOUR:
                        string = " hour";
                        break;
                    case MINUTE:
                        string = " minute";
                        break;
                    case SECOND:
                        string = " second";
                        break;
                    default:
                        throw unsupported();
                }
                ctx.visit(Names.N_STRFTIME).sql("('%Y-%m-%d %H:%M:%f', ").visit((Field<?>) this.date).sql(", ").visit((Field<?>) this.interval.concat(DSL.inline(string))).sql(')');
                return;
            case TRINO:
                ctx.visit(Names.N_DATE_ADD).sql('(').visit((Field<?>) DSL.inline(this.datePart.toString().toLowerCase())).sql(", ").visit((Field<?>) this.interval).sql(", ").visit((Field<?>) this.date).sql(')');
                return;
            default:
                ctx.visit(Names.N_DATEADD).sql('(').visit(standardKeyword()).sql(", ").visit((Field<?>) this.interval).sql(", ").visit((Field<?>) this.date).sql(')');
                return;
        }
    }

    private final Keyword standardKeyword() {
        switch (this.datePart) {
            case YEAR:
            case MONTH:
            case DAY:
            case HOUR:
            case MINUTE:
            case SECOND:
                return this.datePart.toKeyword();
            default:
                throw unsupported();
        }
    }

    private final UnsupportedOperationException unsupported() {
        return new UnsupportedOperationException("Unknown date part : " + String.valueOf(this.datePart));
    }

    @Override // org.jooq.impl.QOM.UOperator3
    public final Field<T> $arg1() {
        return this.date;
    }

    /* JADX WARN: Can't rename method to resolve collision */
    @Override // org.jooq.impl.QOM.UOperator3
    public final Field<? extends Number> $arg2() {
        return this.interval;
    }

    /* JADX WARN: Can't rename method to resolve collision */
    @Override // org.jooq.impl.QOM.UOperator3
    public final DatePart $arg3() {
        return this.datePart;
    }

    @Override // org.jooq.impl.QOM.UOperator3
    public final QOM.DateAdd<T> $arg1(Field<T> newValue) {
        return $constructor().apply(newValue, $arg2(), $arg3());
    }

    @Override // org.jooq.impl.QOM.UOperator3
    public final QOM.DateAdd<T> $arg2(Field<? extends Number> newValue) {
        return $constructor().apply($arg1(), newValue, $arg3());
    }

    @Override // org.jooq.impl.QOM.UOperator3
    public final QOM.DateAdd<T> $arg3(DatePart newValue) {
        return $constructor().apply($arg1(), $arg2(), newValue);
    }

    @Override // org.jooq.impl.QOM.UOperator3
    public final Function3<? super Field<T>, ? super Field<? extends Number>, ? super DatePart, ? extends QOM.DateAdd<T>> $constructor() {
        return (a1, a2, a3) -> {
            return new DateAdd(a1, a2, a3);
        };
    }

    @Override // org.jooq.impl.AbstractNamed, org.jooq.impl.AbstractQueryPart, org.jooq.QueryPart
    public boolean equals(Object that) {
        if (!(that instanceof QOM.DateAdd)) {
            return super.equals(that);
        }
        QOM.DateAdd<?> o = (QOM.DateAdd) that;
        return StringUtils.equals($date(), o.$date()) && StringUtils.equals($interval(), o.$interval()) && StringUtils.equals($datePart(), o.$datePart());
    }
}
