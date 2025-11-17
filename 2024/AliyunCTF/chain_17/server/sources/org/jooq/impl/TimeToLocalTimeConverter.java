package org.jooq.impl;

import java.sql.Time;
import java.time.LocalTime;
import org.jooq.ConverterContext;

@Deprecated
/* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/impl/TimeToLocalTimeConverter.class */
public final class TimeToLocalTimeConverter extends AbstractContextConverter<Time, LocalTime> {
    public TimeToLocalTimeConverter() {
        super(Time.class, LocalTime.class);
    }

    @Override // org.jooq.ContextConverter
    public final LocalTime from(Time t, ConverterContext scope) {
        if (t == null) {
            return null;
        }
        return t.toLocalTime();
    }

    @Override // org.jooq.ContextConverter
    public final Time to(LocalTime u, ConverterContext scope) {
        if (u == null) {
            return null;
        }
        return Time.valueOf(u);
    }
}
