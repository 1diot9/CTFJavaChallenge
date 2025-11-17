package org.jooq;

import org.jetbrains.annotations.NotNull;
import org.jooq.impl.DSL;

/* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/SortOrder.class */
public enum SortOrder {
    ASC("asc"),
    DESC("desc"),
    DEFAULT("");

    private final String sql;
    private final Keyword keyword;

    SortOrder(String sql) {
        this.sql = sql;
        this.keyword = DSL.keyword(sql);
    }

    @NotNull
    public final String toSQL() {
        return this.sql;
    }

    @NotNull
    public final Keyword toKeyword() {
        return this.keyword;
    }
}
