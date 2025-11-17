package org.jooq.impl;

import org.jooq.Keyword;
import org.jooq.SQLDialect;

/* JADX INFO: Access modifiers changed from: package-private */
/* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/impl/CombineOperator.class */
public enum CombineOperator {
    UNION("union", true),
    UNION_ALL("union all", false),
    EXCEPT("except", true),
    EXCEPT_ALL("except all", false),
    INTERSECT("intersect", true),
    INTERSECT_ALL("intersect all", false);

    private final String sql;
    private final Keyword keywordOptionalDistinct;
    private final Keyword keyword;

    CombineOperator(String sql, boolean distinct) {
        this.sql = sql;
        this.keyword = DSL.keyword(sql);
        this.keywordOptionalDistinct = distinct ? DSL.keyword(sql + " distinct") : this.keyword;
    }

    public final String toSQL(SQLDialect dialect) {
        return this.sql;
    }

    public final Keyword toKeyword(SQLDialect dialect) {
        return this.keyword;
    }
}
