package org.jooq.impl;

import java.sql.Date;
import java.time.LocalDate;
import org.jooq.ConverterContext;

@Deprecated
/* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/impl/DateToLocalDateConverter.class */
public final class DateToLocalDateConverter extends AbstractContextConverter<Date, LocalDate> {
    public DateToLocalDateConverter() {
        super(Date.class, LocalDate.class);
    }

    @Override // org.jooq.ContextConverter
    public final LocalDate from(Date t, ConverterContext scope) {
        if (t == null) {
            return null;
        }
        return t.toLocalDate();
    }

    @Override // org.jooq.ContextConverter
    public final Date to(LocalDate u, ConverterContext scope) {
        if (u == null) {
            return null;
        }
        return Date.valueOf(u);
    }
}
