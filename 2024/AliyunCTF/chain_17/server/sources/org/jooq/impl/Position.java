package org.jooq.impl;

import org.jooq.Context;
import org.jooq.Field;
import org.jooq.Function3;
import org.jooq.impl.QOM;
import org.jooq.tools.StringUtils;

/* JADX INFO: Access modifiers changed from: package-private */
/* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/impl/Position.class */
public final class Position extends AbstractField<Integer> implements QOM.Position {
    final Field<String> in;
    final Field<String> search;
    final Field<? extends Number> startIndex;

    /* JADX INFO: Access modifiers changed from: package-private */
    public Position(Field<String> in, Field<String> search) {
        super(Names.N_POSITION, Tools.allNotNull(SQLDataType.INTEGER, in, search));
        this.in = Tools.nullSafeNotNull(in, SQLDataType.VARCHAR);
        this.search = Tools.nullSafeNotNull(search, SQLDataType.VARCHAR);
        this.startIndex = null;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public Position(Field<String> in, Field<String> search, Field<? extends Number> startIndex) {
        super(Names.N_POSITION, Tools.allNotNull(SQLDataType.INTEGER, in, search, startIndex));
        this.in = Tools.nullSafeNotNull(in, SQLDataType.VARCHAR);
        this.search = Tools.nullSafeNotNull(search, SQLDataType.VARCHAR);
        this.startIndex = Tools.nullSafeNotNull(startIndex, SQLDataType.INTEGER);
    }

    /* JADX WARN: Type inference failed for: r0v14, types: [org.jooq.Context] */
    /* JADX WARN: Type inference failed for: r0v21, types: [org.jooq.Context] */
    /* JADX WARN: Type inference failed for: r0v32, types: [org.jooq.Context] */
    /* JADX WARN: Type inference failed for: r0v5, types: [org.jooq.Context] */
    @Override // org.jooq.impl.AbstractField, org.jooq.QueryPartInternal
    public final void accept(Context<?> ctx) {
        if (this.startIndex != null) {
            switch (ctx.family()) {
                case DERBY:
                case H2:
                    ctx.visit(Names.N_LOCATE).sql('(').visit((Field<?>) this.search).sql(", ").visit((Field<?>) this.in).sql(", ").visit((Field<?>) this.startIndex).sql(')');
                    return;
                default:
                    ctx.visit(DSL.case_((Field) DSL.position(DSL.substring(this.in, this.startIndex), this.search)).when((Field) DSL.inline(0), (Field) DSL.inline(0)).else_(Internal.iadd(DSL.position(DSL.substring(this.in, this.startIndex), this.search), Internal.isub(this.startIndex, DSL.one()))));
                    return;
            }
        }
        switch (ctx.family()) {
            case DERBY:
                ctx.visit(Names.N_LOCATE).sql('(').visit((Field<?>) this.search).sql(", ").visit((Field<?>) this.in).sql(')');
                return;
            case SQLITE:
                ctx.visit(Names.N_INSTR).sql('(').visit((Field<?>) this.in).sql(", ").visit((Field<?>) this.search).sql(')');
                return;
            default:
                ctx.visit(Names.N_POSITION).sql('(').visit((Field<?>) this.search).sql(' ').visit(Keywords.K_IN).sql(' ').visit((Field<?>) this.in).sql(')');
                return;
        }
    }

    /* JADX WARN: Can't rename method to resolve collision */
    @Override // org.jooq.impl.QOM.UOperator3
    public final Field<String> $arg1() {
        return this.in;
    }

    /* JADX WARN: Can't rename method to resolve collision */
    @Override // org.jooq.impl.QOM.UOperator3
    public final Field<String> $arg2() {
        return this.search;
    }

    /* JADX WARN: Can't rename method to resolve collision */
    @Override // org.jooq.impl.QOM.UOperator3
    public final Field<? extends Number> $arg3() {
        return this.startIndex;
    }

    @Override // org.jooq.impl.QOM.UOperator3
    public final QOM.Position $arg1(Field<String> newValue) {
        return $constructor().apply(newValue, $arg2(), $arg3());
    }

    @Override // org.jooq.impl.QOM.UOperator3
    public final QOM.Position $arg2(Field<String> newValue) {
        return $constructor().apply($arg1(), newValue, $arg3());
    }

    @Override // org.jooq.impl.QOM.UOperator3
    public final QOM.Position $arg3(Field<? extends Number> newValue) {
        return $constructor().apply($arg1(), $arg2(), newValue);
    }

    @Override // org.jooq.impl.QOM.UOperator3
    public final Function3<? super Field<String>, ? super Field<String>, ? super Field<? extends Number>, ? extends QOM.Position> $constructor() {
        return (a1, a2, a3) -> {
            return new Position(a1, a2, a3);
        };
    }

    @Override // org.jooq.impl.AbstractNamed, org.jooq.impl.AbstractQueryPart, org.jooq.QueryPart
    public boolean equals(Object that) {
        if (!(that instanceof QOM.Position)) {
            return super.equals(that);
        }
        QOM.Position o = (QOM.Position) that;
        return StringUtils.equals($in(), o.$in()) && StringUtils.equals($search(), o.$search()) && StringUtils.equals($startIndex(), o.$startIndex());
    }
}
