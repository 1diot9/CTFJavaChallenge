package org.jooq.impl;

import org.jooq.Clause;
import org.jooq.Context;
import org.jooq.Field;
import org.jooq.impl.QOM;

/* JADX INFO: Access modifiers changed from: package-private */
/* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/impl/RegexpLike.class */
public final class RegexpLike extends AbstractCondition implements QOM.RegexpLike {
    private static final Clause[] CLAUSES = {Clause.CONDITION, Clause.CONDITION_COMPARISON};
    private final Field<?> search;
    private final Field<String> pattern;

    /* JADX INFO: Access modifiers changed from: package-private */
    public RegexpLike(Field<?> search, Field<String> pattern) {
        this.search = search;
        this.pattern = pattern;
    }

    /* JADX WARN: Type inference failed for: r0v11, types: [org.jooq.Context] */
    /* JADX WARN: Type inference failed for: r0v17, types: [org.jooq.Context] */
    /* JADX WARN: Type inference failed for: r0v24, types: [org.jooq.Context] */
    /* JADX WARN: Type inference failed for: r0v3, types: [org.jooq.Context] */
    @Override // org.jooq.impl.AbstractField, org.jooq.QueryPartInternal
    public final void accept(Context<?> ctx) {
        switch (ctx.family()) {
            case CUBRID:
            case H2:
            case MARIADB:
            case MYSQL:
            case SQLITE:
                ctx.visit(this.search).sql(' ').visit(Keywords.K_REGEXP).sql(' ').visit((Field<?>) this.pattern);
                return;
            case HSQLDB:
                ctx.visit(DSL.keyword("regexp_matches")).sql('(').visit(this.search).sql(", ").visit((Field<?>) this.pattern).sql(')');
                return;
            case POSTGRES:
            case YUGABYTEDB:
                ctx.sql('(').visit(this.search).sql(" ~ ").visit((Field<?>) this.pattern).sql(')');
                return;
            default:
                ctx.sql('(').visit(this.search).sql(' ').visit(Keywords.K_LIKE_REGEX).sql(' ').visit((Field<?>) this.pattern).sql(')');
                return;
        }
    }

    @Override // org.jooq.impl.AbstractCondition, org.jooq.impl.AbstractField, org.jooq.impl.AbstractQueryPart, org.jooq.QueryPartInternal
    public final Clause[] clauses(Context<?> ctx) {
        return CLAUSES;
    }

    @Override // org.jooq.impl.QOM.RegexpLike
    public final Field<?> $search() {
        return this.search;
    }

    @Override // org.jooq.impl.QOM.RegexpLike
    public final Field<String> $pattern() {
        return this.pattern;
    }
}
