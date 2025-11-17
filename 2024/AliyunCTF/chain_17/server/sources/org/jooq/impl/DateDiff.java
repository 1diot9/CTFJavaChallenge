package org.jooq.impl;

import org.jooq.Context;
import org.jooq.DatePart;
import org.jooq.Field;
import org.jooq.Name;
import org.jooq.SQLDialect;
import org.jooq.impl.QOM;

/* JADX INFO: Access modifiers changed from: package-private */
/* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/impl/DateDiff.class */
public final class DateDiff<T> extends AbstractField<Integer> implements QOM.UNotYetImplemented {
    private final DatePart part;
    private final Field<T> startDate;
    private final Field<T> endDate;

    /* JADX INFO: Access modifiers changed from: package-private */
    public DateDiff(DatePart part, Field<T> startDate, Field<T> endDate) {
        super(Names.N_DATEDIFF, SQLDataType.INTEGER);
        this.part = part;
        this.startDate = startDate;
        this.endDate = endDate;
    }

    /* JADX WARN: Type inference failed for: r0v10, types: [org.jooq.Context] */
    /* JADX WARN: Type inference failed for: r0v105, types: [org.jooq.Context] */
    /* JADX WARN: Type inference failed for: r0v130, types: [org.jooq.Context] */
    /* JADX WARN: Type inference failed for: r0v143, types: [org.jooq.Context] */
    /* JADX WARN: Type inference failed for: r0v29, types: [org.jooq.Context] */
    /* JADX WARN: Type inference failed for: r0v39, types: [org.jooq.Context] */
    /* JADX WARN: Type inference failed for: r0v54, types: [org.jooq.Context] */
    /* JADX WARN: Type inference failed for: r0v64, types: [org.jooq.Context] */
    /* JADX WARN: Type inference failed for: r0v76, types: [org.jooq.Context] */
    /* JADX WARN: Type inference failed for: r0v87, types: [org.jooq.Context] */
    @Override // org.jooq.impl.AbstractField, org.jooq.QueryPartInternal
    public final void accept(Context<?> context) {
        DatePart datePart = this.part == null ? DatePart.DAY : this.part;
        switch (context.family()) {
            case POSTGRES:
            case YUGABYTEDB:
            case CUBRID:
                switch (datePart) {
                    case MILLENNIUM:
                    case CENTURY:
                    case DECADE:
                    case YEAR:
                        context.visit((Field<?>) partDiff(datePart));
                        return;
                    case QUARTER:
                    case MONTH:
                        context.visit((Field<?>) monthDiff(datePart));
                        return;
                    case DAY:
                        switch (context.family()) {
                            case POSTGRES:
                            case YUGABYTEDB:
                                if (this.endDate.getDataType().isDate() && this.startDate.getDataType().isDate()) {
                                    context.sql('(').visit(this.endDate).sql(" - ").visit((Field<?>) this.startDate).sql(')');
                                    return;
                                } else {
                                    context.visit(Names.N_EXTRACT).sql('(').visit(Keywords.K_DAY).sql(' ').visit(Keywords.K_FROM).sql(' ').visit((Field<?>) this.endDate).sql(" - ").visit((Field<?>) this.startDate).sql(')');
                                    return;
                                }
                            default:
                                context.sql('(').visit(this.endDate).sql(" - ").visit((Field<?>) this.startDate).sql(')');
                                return;
                        }
                    case MILLISECOND:
                    case NANOSECOND:
                    case MICROSECOND:
                        context.visit((Field<?>) partDiff(DatePart.EPOCH).times(datePart == DatePart.MILLISECOND ? DSL.inline(1000) : datePart == DatePart.MICROSECOND ? DSL.inline(1000000) : DSL.inline(1000000000)));
                        return;
                    case HOUR:
                    case MINUTE:
                        context.visit((Field<?>) partDiff(DatePart.EPOCH).div(datePart == DatePart.HOUR ? DSL.inline(3600) : DSL.inline(60)));
                        return;
                    case SECOND:
                        context.visit((Field<?>) partDiff(DatePart.EPOCH));
                        return;
                }
            case MARIADB:
            case MYSQL:
                switch (datePart) {
                    case MILLENNIUM:
                    case CENTURY:
                    case DECADE:
                    case YEAR:
                        context.visit((Field<?>) partDiff(datePart));
                        return;
                    case QUARTER:
                    case MONTH:
                        context.visit((Field<?>) monthDiff(datePart));
                        return;
                    case DAY:
                        context.visit(Names.N_DATEDIFF).sql('(').visit((Field<?>) this.endDate).sql(", ").visit((Field<?>) this.startDate).sql(')');
                        return;
                    case MILLISECOND:
                        context.visit((Field<?>) new DateDiff(DatePart.MICROSECOND, this.startDate, this.endDate).div(DSL.inline(1000)));
                        return;
                    case NANOSECOND:
                        context.visit((Field<?>) new DateDiff(DatePart.MICROSECOND, this.startDate, this.endDate).times(DSL.inline(1000)));
                        return;
                    default:
                        context.visit(Names.N_TIMESTAMPDIFF).sql('(').visit(datePart.toName()).sql(", ").visit((Field<?>) this.startDate).sql(", ").visit((Field<?>) this.endDate).sql(')');
                        return;
                }
            case DERBY:
                Name name = Names.N_SQL_TSI_DAY;
                switch (datePart) {
                    case MILLENNIUM:
                    case CENTURY:
                    case DECADE:
                    case YEAR:
                        context.visit((Field<?>) partDiff(datePart));
                        return;
                    case QUARTER:
                    case MONTH:
                        context.visit((Field<?>) monthDiff(datePart));
                        return;
                    case DAY:
                        name = Names.N_SQL_TSI_DAY;
                        break;
                    case MILLISECOND:
                        context.visit((Field<?>) new DateDiff(DatePart.NANOSECOND, this.startDate, this.endDate).div(DSL.inline(1000000L)));
                        return;
                    case NANOSECOND:
                        name = Names.N_SQL_TSI_FRAC_SECOND;
                        break;
                    case HOUR:
                        name = Names.N_SQL_TSI_HOUR;
                        break;
                    case MINUTE:
                        name = Names.N_SQL_TSI_MINUTE;
                        break;
                    case SECOND:
                        name = Names.N_SQL_TSI_SECOND;
                        break;
                    case MICROSECOND:
                        context.visit((Field<?>) new DateDiff(DatePart.NANOSECOND, this.startDate, this.endDate).div(DSL.inline(1000L)));
                        return;
                }
                context.sql("{fn ").visit(Names.N_TIMESTAMPDIFF).sql('(').visit(name).sql(", ").visit((Field<?>) this.startDate).sql(", ").visit((Field<?>) this.endDate).sql(") }");
                return;
            case FIREBIRD:
            case H2:
            case HSQLDB:
                switch (datePart) {
                    case MILLENNIUM:
                    case CENTURY:
                    case DECADE:
                        context.visit((Field<?>) partDiff(datePart));
                        return;
                    case QUARTER:
                        if (context.family() == SQLDialect.FIREBIRD) {
                            context.visit((Field<?>) monthDiff(DatePart.QUARTER));
                            return;
                        }
                        break;
                    case MILLISECOND:
                    case NANOSECOND:
                    case HOUR:
                    case MINUTE:
                    case SECOND:
                    case MICROSECOND:
                        if (context.family() == SQLDialect.HSQLDB) {
                            context.visit(Names.N_DATEDIFF).sql('(').visit(datePart.toKeyword()).sql(", ").visit(this.startDate.cast(SQLDataType.TIMESTAMP)).sql(", ").visit(this.endDate.cast(SQLDataType.TIMESTAMP)).sql(')');
                            return;
                        }
                        break;
                }
                context.visit(Names.N_DATEDIFF).sql('(').visit(datePart.toKeyword()).sql(", ").visit((Field<?>) this.startDate).sql(", ").visit((Field<?>) this.endDate).sql(')');
                return;
            case SQLITE:
                context.sql('(').visit(Names.N_STRFTIME).sql("('%s', ").visit((Field<?>) this.endDate).sql(") - ").visit(Names.N_STRFTIME).sql("('%s', ").visit((Field<?>) this.startDate).sql(")) / 86400");
                return;
            case TRINO:
                context.visit(Names.N_DATE_DIFF).sql('(').visit((Field<?>) DSL.inline(datePart.name().toLowerCase())).sql(", ").visit((Field<?>) this.startDate).sql(", ").visit((Field<?>) this.endDate).sql(')');
                return;
        }
        context.visit(Tools.castIfNeeded((Field<?>) this.endDate.minus((Field<?>) this.startDate), Integer.class));
    }

    private final Field<Integer> partDiff(DatePart p) {
        return DSL.extract((Field<?>) this.endDate, p).minus(DSL.extract((Field<?>) this.startDate, p));
    }

    private final Field<Integer> monthDiff(DatePart p) {
        return partDiff(DatePart.YEAR).times(p == DatePart.QUARTER ? DSL.inline(4) : DSL.inline(12)).plus(partDiff(p));
    }
}
