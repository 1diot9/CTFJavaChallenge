package org.jooq.impl;

import org.jooq.Context;
import org.jooq.Field;
import org.jooq.Param;
import org.jooq.RenderContext;
import org.jooq.conf.ParamType;
import org.jooq.impl.QOM;

/* JADX INFO: Access modifiers changed from: package-private */
/* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/impl/Limit.class */
public final class Limit extends AbstractQueryPart implements QOM.UTransient {
    private static final Lazy<Param<Integer>> ZERO = Lazy.of(() -> {
        return DSL.zero();
    });
    private static final Lazy<Param<Integer>> ONE = Lazy.of(() -> {
        return DSL.one();
    });
    private static final Lazy<Param<Integer>> MAX = Lazy.of(() -> {
        return DSL.inline(Integer.MAX_VALUE);
    });
    Field<? extends Number> limit;
    Field<? extends Number> offset;
    boolean withTies;
    boolean percent;
    private Field<? extends Number> limitOrMax = MAX.get();
    private Field<? extends Number> offsetOrZero = ZERO.get();
    private Field<? extends Number> offsetPlusOne = ONE.get();

    /* JADX WARN: Type inference failed for: r0v16, types: [org.jooq.Context] */
    @Override // org.jooq.QueryPartInternal
    public final void accept(Context<?> ctx) {
        ctx.paramType();
        RenderContext.CastMode castMode = ctx.castMode();
        switch (ctx.family()) {
            case CUBRID:
                ctx.castMode(RenderContext.CastMode.NEVER).formatSeparator().visit(Keywords.K_LIMIT).sql(' ').visit((Field<?>) this.offsetOrZero).sql(", ").visit((Field<?>) this.limitOrMax).castMode(castMode);
                return;
            case FIREBIRD:
                acceptStandard(ctx, castMode);
                return;
            case H2:
                acceptStandard(ctx, castMode);
                return;
            case POSTGRES:
                acceptStandard(ctx, castMode);
                return;
            case DERBY:
            case TRINO:
                acceptStandard(ctx, castMode);
                return;
            case MARIADB:
                acceptStandard(ctx, castMode);
                return;
            case MYSQL:
            case SQLITE:
                acceptDefaultLimitMandatory(ctx, castMode);
                return;
            case DUCKDB:
                ctx.paramType(ParamType.INLINED, c -> {
                    acceptDefault(c, castMode);
                });
                return;
            default:
                acceptDefault(ctx, castMode);
                return;
        }
    }

    /* JADX WARN: Type inference failed for: r0v16, types: [org.jooq.Context] */
    /* JADX WARN: Type inference failed for: r0v19, types: [org.jooq.Context] */
    /* JADX WARN: Type inference failed for: r0v22, types: [org.jooq.Context] */
    /* JADX WARN: Type inference failed for: r0v9, types: [org.jooq.Context] */
    private final void acceptStandard(Context<?> ctx, RenderContext.CastMode castMode) {
        ctx.castMode(RenderContext.CastMode.NEVER);
        if (!offsetZero()) {
            ctx.formatSeparator().visit(Keywords.K_OFFSET).sql(' ').visit((Field<?>) this.offsetOrZero).sql(' ').visit(Keywords.K_ROWS);
        }
        if (!limitZero()) {
            ctx.formatSeparator().visit(Keywords.K_FETCH_NEXT).sql(' ').visit((Field<?>) this.limit);
            if (this.percent) {
                ctx.sql(' ').visit(Keywords.K_PERCENT);
            }
            ctx.sql(' ').visit(this.withTies ? Keywords.K_ROWS_WITH_TIES : Keywords.K_ROWS_ONLY);
        }
        ctx.castMode(castMode);
    }

    /* JADX WARN: Type inference failed for: r0v14, types: [org.jooq.Context] */
    /* JADX WARN: Type inference failed for: r0v9, types: [org.jooq.Context] */
    private final void acceptDefault(Context<?> ctx, RenderContext.CastMode castMode) {
        ctx.castMode(RenderContext.CastMode.NEVER);
        if (!limitZero()) {
            ctx.formatSeparator().visit(Keywords.K_LIMIT).sql(' ').visit((Field<?>) this.limit);
        }
        if (!offsetZero()) {
            ctx.formatSeparator().visit(Keywords.K_OFFSET).sql(' ').visit((Field<?>) this.offsetOrZero);
        }
        ctx.castMode(castMode);
    }

    /* JADX WARN: Type inference failed for: r0v1, types: [org.jooq.Context] */
    /* JADX WARN: Type inference failed for: r0v11, types: [org.jooq.Context] */
    private void acceptDefaultLimitMandatory(Context<?> ctx, RenderContext.CastMode castMode) {
        ctx.castMode(RenderContext.CastMode.NEVER).formatSeparator().visit(Keywords.K_LIMIT).sql(' ').visit((Field<?>) this.limitOrMax);
        if (!offsetZero()) {
            ctx.formatSeparator().visit(Keywords.K_OFFSET).sql(' ').visit((Field<?>) this.offsetOrZero);
        }
        ctx.castMode(castMode);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public final boolean limitZero() {
        return this.limit == null;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public final boolean limitOne() {
        if (!limitZero() && !withTies() && !percent()) {
            Long l = 1L;
            if (l.equals(getLimit())) {
                return true;
            }
        }
        return false;
    }

    final boolean offsetZero() {
        return this.offset == null;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public final Field<?> getLowerRownum() {
        return this.offsetOrZero;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public final Field<?> getUpperRownum() {
        return Internal.iadd(this.offsetOrZero, this.limitOrMax);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public final boolean isApplicable() {
        return (this.offset == null && this.limit == null) ? false : true;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public final boolean isExpression() {
        return isApplicable() && !((this.limit == null || (this.limit instanceof Param)) && (this.offset == null || (this.offset instanceof Param)));
    }

    final boolean isSubquery() {
        return isApplicable() && (Tools.isScalarSubquery(this.limit) || Tools.isScalarSubquery(this.offset));
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public final void setOffset(Number offset) {
        this.offset = DSL.val(Long.valueOf(offset.longValue()), SQLDataType.BIGINT);
        this.offsetOrZero = this.offset;
        this.offsetPlusOne = DSL.val(Long.valueOf(offset.longValue() + 1), SQLDataType.BIGINT);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public final void setOffset(Field<? extends Number> offset) {
        if (offset instanceof NoField) {
            return;
        }
        this.offset = offset;
        this.offsetOrZero = offset == null ? ZERO.get() : offset;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public final void setLimit(Number l) {
        this.limit = DSL.val(Long.valueOf(l.longValue()), SQLDataType.BIGINT);
        this.limitOrMax = this.limit;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public final void setLimit(Field<? extends Number> l) {
        if (l instanceof NoField) {
            return;
        }
        this.limit = l;
        this.limitOrMax = l == null ? MAX.get() : l;
    }

    final Long getLimit() {
        Field<?> l = this.limit != null ? this.limit : this.limitOrMax;
        if (l instanceof Param) {
            Param<?> p = (Param) l;
            return (Long) Convert.convert(p.getValue(), Long.TYPE);
        }
        return (Long) Convert.convert(MAX.get().getValue(), Long.TYPE);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public final void setPercent(boolean percent) {
        this.percent = percent;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public final boolean percent() {
        return this.percent;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public final void setWithTies(boolean withTies) {
        this.withTies = withTies;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public final boolean withTies() {
        return this.withTies;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public final Limit from(Limit other) {
        if (other.limit != null) {
            if (this.limit == null) {
                setLimit(other.limit);
            } else {
                setLimit(((Val) other.limit).copy(Long.valueOf(Math.min(getLimit().longValue(), other.getLimit().longValue()))));
            }
        }
        if (other.offset != null) {
            setOffset(other.offset);
        }
        setPercent(other.percent);
        setWithTies(other.withTies);
        return this;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public final void clear() {
        this.offset = null;
        this.limit = null;
        this.withTies = false;
        this.percent = false;
    }
}
