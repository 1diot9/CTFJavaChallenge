package org.jooq;

import org.jooq.impl.DSL;

/* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/DatePart.class */
public enum DatePart {
    YEAR("year"),
    MONTH("month"),
    DAY("day"),
    HOUR("hour"),
    MINUTE("minute"),
    SECOND("second"),
    MILLISECOND("millisecond"),
    MICROSECOND("microsecond"),
    NANOSECOND("nanosecond"),
    MILLENNIUM("millennium"),
    CENTURY("century"),
    DECADE("decade"),
    EPOCH("epoch"),
    QUARTER("quarter"),
    WEEK("week"),
    DAY_OF_YEAR("day_of_year"),
    DAY_OF_WEEK("day_of_week"),
    ISO_DAY_OF_WEEK("iso_day_of_week"),
    TIMEZONE("timezone"),
    TIMEZONE_HOUR("timezone_hour"),
    TIMEZONE_MINUTE("timezone_minute");

    private final String sql;
    private final Keyword keyword;
    private final Name name;

    DatePart(String sql) {
        this.sql = sql;
        this.keyword = DSL.keyword(sql);
        this.name = DSL.unquotedName(sql);
    }

    public final String toSQL() {
        return this.sql;
    }

    public final Keyword toKeyword() {
        return this.keyword;
    }

    public final Name toName() {
        return this.name;
    }
}
