package org.jooq.impl;

import ch.qos.logback.classic.encoder.JsonEncoder;
import org.jooq.Context;
import org.jooq.DataType;
import org.jooq.Field;
import org.jooq.Keyword;
import org.jooq.impl.QOM;

/* JADX INFO: Access modifiers changed from: package-private */
/* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/impl/DateOrTime.class */
public final class DateOrTime<T> extends AbstractField<T> implements QOM.UNotYetImplemented {
    private final Field<?> field;

    /* JADX INFO: Access modifiers changed from: package-private */
    public DateOrTime(Field<?> field, DataType<T> dataType) {
        super(DSL.name(name(dataType)), dataType);
        this.field = field;
    }

    private static String name(DataType<?> dataType) {
        if (dataType.isDate()) {
            return "date";
        }
        if (dataType.isTime()) {
            return "time";
        }
        return JsonEncoder.TIMESTAMP_ATTR_NAME;
    }

    private static Keyword keyword(DataType<?> dataType) {
        if (dataType.isDate()) {
            return Keywords.K_DATE;
        }
        if (dataType.isTime()) {
            return Keywords.K_TIME;
        }
        return Keywords.K_TIMESTAMP;
    }

    /* JADX WARN: Type inference failed for: r0v11, types: [org.jooq.Context] */
    /* JADX WARN: Type inference failed for: r0v16, types: [org.jooq.Context] */
    /* JADX WARN: Type inference failed for: r0v21, types: [org.jooq.Context] */
    /* JADX WARN: Type inference failed for: r0v26, types: [org.jooq.Context] */
    @Override // org.jooq.impl.AbstractField, org.jooq.QueryPartInternal
    public final void accept(Context<?> ctx) {
        switch (ctx.family()) {
            case DERBY:
            case MARIADB:
            case MYSQL:
                ctx.visit(keyword(getDataType())).sql('(').visit(this.field).sql(')');
                return;
            case SQLITE:
                if (getDataType().isDate()) {
                    ctx.visit(Keywords.K_DATE).sql('(').visit(this.field).sql(')');
                    return;
                } else if (getDataType().isTime()) {
                    ctx.visit(Keywords.K_TIME).sql('(').visit(this.field).sql(')');
                    return;
                } else {
                    ctx.visit(Names.N_STRFTIME).sql("('%Y-%m-%d %H:%M:%f', ").visit(this.field).sql(')');
                    return;
                }
            default:
                ctx.visit(Tools.castIfNeeded(this.field, getDataType()));
                return;
        }
    }
}
