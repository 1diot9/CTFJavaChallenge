package org.jooq.impl;

import java.util.Set;
import org.jooq.Comparator;
import org.jooq.Context;
import org.jooq.Field;
import org.jooq.Function3;
import org.jooq.LikeEscapeStep;
import org.jooq.SQLDialect;
import org.jooq.conf.ParamType;
import org.jooq.impl.QOM;
import org.jooq.tools.StringUtils;

/* JADX INFO: Access modifiers changed from: package-private */
/* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/impl/Like.class */
public final class Like extends AbstractCondition implements QOM.Like, LikeEscapeStep {
    final Field<?> value;
    final Field<String> pattern;
    Character escape;
    private static final Set<SQLDialect> REQUIRES_CAST_ON_LIKE = SQLDialect.supportedBy(SQLDialect.DERBY, SQLDialect.POSTGRES, SQLDialect.TRINO, SQLDialect.YUGABYTEDB);
    private static final Set<SQLDialect> NO_SUPPORT_ILIKE = SQLDialect.supportedBy(SQLDialect.CUBRID, SQLDialect.DERBY, SQLDialect.FIREBIRD, SQLDialect.HSQLDB, SQLDialect.MARIADB, SQLDialect.MYSQL, SQLDialect.SQLITE, SQLDialect.TRINO);

    /* JADX INFO: Access modifiers changed from: package-private */
    public Like(Field<?> value, Field<String> pattern) {
        this(value, pattern, null);
    }

    Like(Field<?> value, Field<String> pattern, Character escape) {
        this.value = Tools.nullableIf(false, Tools.nullSafe(value, pattern.getDataType()));
        this.pattern = Tools.nullableIf(false, Tools.nullSafe(pattern, value.getDataType()));
    }

    @Override // org.jooq.LikeEscapeStep
    public final Like escape(char escape) {
        this.escape = Character.valueOf(escape);
        return this;
    }

    @Override // org.jooq.impl.AbstractField, org.jooq.QueryPartInternal
    public final void accept(Context<?> ctx) {
        accept0(ctx, this.value, Comparator.LIKE, this.pattern, this.escape);
    }

    static final boolean castRhs(Context<?> ctx, Field<?> arg2) {
        return false;
    }

    static final ParamType forcedParamType(Context<?> ctx, Character escape) {
        ParamType forcedParamType = ctx.paramType();
        return forcedParamType;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* JADX WARN: Type inference failed for: r0v17, types: [org.jooq.Context] */
    /* JADX WARN: Type inference failed for: r0v22, types: [org.jooq.Context] */
    /* JADX WARN: Type inference failed for: r0v28, types: [org.jooq.Context] */
    /* JADX WARN: Type inference failed for: r0v7, types: [org.jooq.Context] */
    public static final void accept0(Context<?> ctx, Field<?> arg1, Comparator op, Field<?> arg2, Character escape) {
        switch (op) {
            case LIKE:
            case SIMILAR_TO:
            case NOT_LIKE:
            case NOT_SIMILAR_TO:
                if (arg1.getType() != String.class && REQUIRES_CAST_ON_LIKE.contains(ctx.dialect())) {
                    arg1 = Tools.castIfNeeded(arg1, String.class);
                }
                if (arg2.getType() != String.class && REQUIRES_CAST_ON_LIKE.contains(ctx.dialect())) {
                    arg2 = Tools.castIfNeeded(arg2, String.class);
                    break;
                }
                break;
            case LIKE_IGNORE_CASE:
            case NOT_LIKE_IGNORE_CASE:
                if (arg1.getType() != String.class) {
                    arg1 = Tools.castIfNeeded(arg1, String.class);
                }
                if (arg2.getType() != String.class) {
                    arg2 = Tools.castIfNeeded(arg2, String.class);
                    break;
                }
                break;
        }
        switch (op) {
            case LIKE_IGNORE_CASE:
            case NOT_LIKE_IGNORE_CASE:
                if (NO_SUPPORT_ILIKE.contains(ctx.dialect())) {
                    arg1 = DSL.lower((Field<String>) arg1);
                    arg2 = DSL.lower((Field<String>) arg2);
                    op = op == Comparator.LIKE_IGNORE_CASE ? Comparator.LIKE : Comparator.NOT_LIKE;
                    break;
                }
                break;
        }
        boolean castRhs = castRhs(ctx, arg2);
        ctx.visit(arg1).sql(' ').visit(op.toKeyword()).sql(' ');
        if (castRhs) {
            ctx.visit(Keywords.K_CAST).sql('(');
        }
        ctx.visit(arg2, forcedParamType(ctx, escape));
        if (castRhs) {
            ctx.sql(' ').visit(Keywords.K_AS).sql(' ').visit(Keywords.K_VARCHAR).sql("(4000))");
        }
        if (escape != null) {
            ctx.sql(' ').visit(Keywords.K_ESCAPE).sql(' ').visit((Field<?>) DSL.inline(escape));
        }
    }

    /* JADX WARN: Can't rename method to resolve collision */
    @Override // org.jooq.impl.QOM.UOperator3
    public final Field<?> $arg1() {
        return this.value;
    }

    /* JADX WARN: Can't rename method to resolve collision */
    @Override // org.jooq.impl.QOM.UOperator3
    public final Field<String> $arg2() {
        return this.pattern;
    }

    /* JADX WARN: Can't rename method to resolve collision */
    @Override // org.jooq.impl.QOM.UOperator3
    public final Character $arg3() {
        return this.escape;
    }

    @Override // org.jooq.impl.QOM.UOperator3
    public final QOM.Like $arg1(Field<?> newValue) {
        return $constructor().apply(newValue, $arg2(), $arg3());
    }

    @Override // org.jooq.impl.QOM.UOperator3
    public final QOM.Like $arg2(Field<String> newValue) {
        return $constructor().apply($arg1(), newValue, $arg3());
    }

    @Override // org.jooq.impl.QOM.UOperator3
    public final QOM.Like $arg3(Character newValue) {
        return $constructor().apply($arg1(), $arg2(), newValue);
    }

    @Override // org.jooq.impl.QOM.UOperator3
    public final Function3<? super Field<?>, ? super Field<String>, ? super Character, ? extends QOM.Like> $constructor() {
        return (a1, a2, a3) -> {
            return new Like(a1, a2, a3);
        };
    }

    @Override // org.jooq.impl.AbstractNamed, org.jooq.impl.AbstractQueryPart, org.jooq.QueryPart
    public boolean equals(Object that) {
        if (!(that instanceof QOM.Like)) {
            return super.equals(that);
        }
        QOM.Like o = (QOM.Like) that;
        return StringUtils.equals($value(), o.$value()) && StringUtils.equals($pattern(), o.$pattern()) && StringUtils.equals($escape(), o.$escape());
    }
}
