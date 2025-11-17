package org.jooq.impl;

import org.jooq.Context;
import org.jooq.DatePart;
import org.jooq.Field;
import org.jooq.impl.QOM;

/* JADX INFO: Access modifiers changed from: package-private */
/* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/impl/TruncDate.class */
public final class TruncDate<T> extends AbstractField<T> implements QOM.UNotYetImplemented {
    private final Field<T> date;
    private final DatePart part;

    /* JADX INFO: Access modifiers changed from: package-private */
    public TruncDate(Field<T> date, DatePart part) {
        super(Names.N_TRUNC, date.getDataType());
        this.date = date;
        this.part = part;
    }

    /* JADX WARN: Type inference failed for: r0v20, types: [org.jooq.Context] */
    /* JADX WARN: Type inference failed for: r0v36, types: [org.jooq.Context] */
    /* JADX WARN: Type inference failed for: r0v5, types: [org.jooq.Context] */
    /* JADX WARN: Type inference failed for: r0v56, types: [org.jooq.Context] */
    @Override // org.jooq.impl.AbstractField, org.jooq.QueryPartInternal
    public final void accept(Context<?> ctx) {
        String keyword = null;
        String format = null;
        switch (ctx.family()) {
            case CUBRID:
            case HSQLDB:
                switch (this.part) {
                    case YEAR:
                        keyword = "YY";
                        break;
                    case MONTH:
                        keyword = "MM";
                        break;
                    case DAY:
                        keyword = "DD";
                        break;
                    case HOUR:
                        keyword = "HH";
                        break;
                    case MINUTE:
                        keyword = "MI";
                        break;
                    case SECOND:
                        keyword = "SS";
                        break;
                    default:
                        throwUnsupported();
                        break;
                }
                ctx.visit(Names.N_TRUNC).sql('(').visit((Field<?>) this.date).sql(", ").visit((Field<?>) DSL.inline(keyword)).sql(')');
                return;
            case H2:
                switch (this.part) {
                    case YEAR:
                        format = "yyyy";
                        break;
                    case MONTH:
                        format = "yyyy-MM";
                        break;
                    case DAY:
                        format = "yyyy-MM-dd";
                        break;
                    case HOUR:
                        format = "yyyy-MM-dd HH";
                        break;
                    case MINUTE:
                        format = "yyyy-MM-dd HH:mm";
                        break;
                    case SECOND:
                        format = "yyyy-MM-dd HH:mm:ss";
                        break;
                    default:
                        throwUnsupported();
                        break;
                }
                ctx.visit(DSL.keyword("parsedatetime")).sql('(').visit(DSL.keyword("formatdatetime")).sql('(').visit((Field<?>) this.date).sql(", ").visit((Field<?>) DSL.inline(format)).sql("), ").visit((Field<?>) DSL.inline(format)).sql(')');
                return;
            case POSTGRES:
            case YUGABYTEDB:
                switch (this.part) {
                    case YEAR:
                        keyword = "year";
                        break;
                    case MONTH:
                        keyword = "month";
                        break;
                    case DAY:
                        keyword = "day";
                        break;
                    case HOUR:
                        keyword = "hour";
                        break;
                    case MINUTE:
                        keyword = "minute";
                        break;
                    case SECOND:
                        keyword = "second";
                        break;
                    default:
                        throwUnsupported();
                        break;
                }
                ctx.visit(Names.N_DATE_TRUNC).sql('(').visit((Field<?>) DSL.inline(keyword)).sql(", ").visit((Field<?>) this.date).sql(')');
                return;
            default:
                ctx.visit(Names.N_TRUNC).sql('(').visit((Field<?>) this.date).sql(", ").visit((Field<?>) DSL.inline((String) null)).sql(')');
                return;
        }
    }

    private final void throwUnsupported() {
        throw new UnsupportedOperationException("Unknown date part : " + String.valueOf(this.part));
    }
}
