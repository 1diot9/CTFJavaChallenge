package org.jooq;

import org.jooq.impl.DSL;

/* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/JoinType.class */
public enum JoinType {
    JOIN("join", "inner join", "join", true, false),
    CROSS_JOIN("cross join", false, false),
    LEFT_OUTER_JOIN("left outer join", "left outer join", "left join", true, false),
    RIGHT_OUTER_JOIN("right outer join", "right outer join", "right join", true, false),
    FULL_OUTER_JOIN("full outer join", "full outer join", "full join", true, false),
    NATURAL_JOIN("natural join", "natural inner join", "natural join", false, false),
    NATURAL_LEFT_OUTER_JOIN("natural left outer join", "natural left outer join", "natural left join", false, false),
    NATURAL_RIGHT_OUTER_JOIN("natural right outer join", "natural right outer join", "natural right join", false, false),
    NATURAL_FULL_OUTER_JOIN("natural full outer join", "natural full outer join", "natural full join", false, false),
    CROSS_APPLY("cross apply", false, true),
    OUTER_APPLY("outer apply", false, true),
    STRAIGHT_JOIN("straight_join", true, false),
    LEFT_SEMI_JOIN("left semi join", true, false),
    LEFT_ANTI_JOIN("left anti join", true, false);

    private final String defaultSql;
    private final Keyword defaultKeyword;
    private final Keyword includingOptionalKeywords;
    private final Keyword excludingOptionalKeywords;
    private final boolean qualified;
    private final boolean correlated;

    JoinType(String sql, boolean qualified, boolean correlated) {
        this(sql, sql, sql, qualified, correlated);
    }

    JoinType(String defaultSql, String includingOptionalKeywords, String excludingOptionalKeywords, boolean qualified, boolean correlated) {
        this.defaultSql = defaultSql;
        this.includingOptionalKeywords = DSL.keyword(includingOptionalKeywords);
        this.excludingOptionalKeywords = DSL.keyword(excludingOptionalKeywords);
        this.defaultKeyword = DSL.keyword(defaultSql);
        this.qualified = qualified;
        this.correlated = correlated;
    }

    public final String toSQL() {
        return this.defaultSql;
    }

    public final Keyword toKeyword() {
        return this.defaultKeyword;
    }

    public final Keyword toKeyword(boolean includeOptionalKeywords) {
        return includeOptionalKeywords ? this.includingOptionalKeywords : this.excludingOptionalKeywords;
    }

    public final boolean qualified() {
        return this.qualified;
    }

    public final boolean correlated() {
        return this.correlated;
    }
}
