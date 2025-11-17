package org.jooq;

import org.jooq.impl.DSL;

/* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/Comparator.class */
public enum Comparator {
    IN("in", false, true, false),
    NOT_IN("not in", false, true, false),
    EQUALS("=", true, true, false),
    NOT_EQUALS("<>", true, true, false),
    LESS("<", true, true, false),
    LESS_OR_EQUAL("<=", true, true, false),
    GREATER(">", true, true, false),
    GREATER_OR_EQUAL(">=", true, true, false),
    IS_DISTINCT_FROM("is distinct from", false, false, true),
    IS_NOT_DISTINCT_FROM("is not distinct from", false, false, true),
    LIKE("like", false, false, false),
    NOT_LIKE("not like", false, false, false),
    SIMILAR_TO("similar to", false, false, false),
    NOT_SIMILAR_TO("not similar to", false, false, false),
    LIKE_IGNORE_CASE("ilike", false, false, false),
    NOT_LIKE_IGNORE_CASE("not ilike", false, false, false);

    private final String sql;
    private final Keyword keyword;
    private final boolean supportsQuantifier;
    private final boolean supportsSubselect;
    private final boolean supportsNulls;

    Comparator(String sql, boolean supportsQuantifier, boolean supportsSubselect, boolean supportsNulls) {
        this.sql = sql;
        this.keyword = DSL.keyword(sql);
        this.supportsQuantifier = supportsQuantifier;
        this.supportsSubselect = supportsSubselect;
        this.supportsNulls = supportsNulls;
    }

    public String toSQL() {
        return this.sql;
    }

    public Keyword toKeyword() {
        return this.keyword;
    }

    public Comparator inverse() {
        switch (this) {
            case IN:
                return NOT_IN;
            case NOT_IN:
                return IN;
            case EQUALS:
                return NOT_EQUALS;
            case NOT_EQUALS:
                return EQUALS;
            case LESS:
                return GREATER_OR_EQUAL;
            case LESS_OR_EQUAL:
                return GREATER;
            case GREATER:
                return LESS_OR_EQUAL;
            case GREATER_OR_EQUAL:
                return LESS;
            case IS_DISTINCT_FROM:
                return IS_NOT_DISTINCT_FROM;
            case IS_NOT_DISTINCT_FROM:
                return IS_DISTINCT_FROM;
            case LIKE:
                return NOT_LIKE;
            case NOT_LIKE:
                return LIKE;
            case SIMILAR_TO:
                return NOT_SIMILAR_TO;
            case NOT_SIMILAR_TO:
                return SIMILAR_TO;
            case LIKE_IGNORE_CASE:
                return NOT_LIKE_IGNORE_CASE;
            case NOT_LIKE_IGNORE_CASE:
                return LIKE_IGNORE_CASE;
            default:
                throw new IllegalStateException();
        }
    }

    public Comparator mirror() {
        switch (ordinal()) {
            case 2:
                return EQUALS;
            case 3:
                return NOT_EQUALS;
            case 4:
                return GREATER;
            case 5:
                return GREATER_OR_EQUAL;
            case 6:
                return LESS;
            case 7:
                return LESS_OR_EQUAL;
            case 8:
                return IS_DISTINCT_FROM;
            case 9:
                return IS_NOT_DISTINCT_FROM;
            default:
                return null;
        }
    }

    @Deprecated(forRemoval = true, since = "3.14")
    public final boolean supportsNulls() {
        return this.supportsNulls;
    }

    @Deprecated(forRemoval = true, since = "3.14")
    public boolean supportsQuantifier() {
        return this.supportsQuantifier;
    }

    @Deprecated(forRemoval = true, since = "3.14")
    public boolean supportsSubselect() {
        return this.supportsSubselect;
    }
}
