package org.jooq.impl;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import org.jooq.ConverterContext;

@Deprecated
/* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/impl/TimestampToLocalDateTimeConverter.class */
public final class TimestampToLocalDateTimeConverter extends AbstractContextConverter<Timestamp, LocalDateTime> {
    public TimestampToLocalDateTimeConverter() {
        super(Timestamp.class, LocalDateTime.class);
    }

    @Override // org.jooq.ContextConverter
    public final LocalDateTime from(Timestamp t, ConverterContext scope) {
        if (t == null) {
            return null;
        }
        return t.toLocalDateTime();
    }

    @Override // org.jooq.ContextConverter
    public final Timestamp to(LocalDateTime u, ConverterContext scope) {
        if (u == null) {
            return null;
        }
        return Timestamp.valueOf(u);
    }
}
