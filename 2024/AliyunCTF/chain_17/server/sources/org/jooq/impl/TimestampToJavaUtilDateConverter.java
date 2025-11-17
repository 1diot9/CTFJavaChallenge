package org.jooq.impl;

import java.sql.Timestamp;
import java.util.Date;
import org.jooq.ConverterContext;

/* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/impl/TimestampToJavaUtilDateConverter.class */
final class TimestampToJavaUtilDateConverter extends AbstractContextConverter<Timestamp, Date> {
    static final TimestampToJavaUtilDateConverter INSTANCE = new TimestampToJavaUtilDateConverter();

    private TimestampToJavaUtilDateConverter() {
        super(Timestamp.class, Date.class);
    }

    @Override // org.jooq.ContextConverter
    public final Date from(Timestamp t, ConverterContext scope) {
        if (t == null) {
            return null;
        }
        return new Date(t.getTime());
    }

    @Override // org.jooq.ContextConverter
    public final Timestamp to(Date u, ConverterContext scope) {
        if (u == null) {
            return null;
        }
        return new Timestamp(u.getTime());
    }
}
