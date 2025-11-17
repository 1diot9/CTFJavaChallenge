package org.jooq.impl;

import java.util.Set;
import org.jooq.Context;
import org.jooq.Field;
import org.jooq.Function4;
import org.jooq.SQLDialect;
import org.jooq.impl.QOM;
import org.jooq.tools.StringUtils;

/* JADX INFO: Access modifiers changed from: package-private */
/* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/impl/Overlay.class */
public final class Overlay extends AbstractField<String> implements QOM.Overlay {
    final Field<String> in;
    final Field<String> placing;
    final Field<? extends Number> startIndex;
    final Field<? extends Number> length;
    private static final Set<SQLDialect> NO_SUPPORT = SQLDialect.supportedBy(SQLDialect.DERBY, SQLDialect.DUCKDB, SQLDialect.HSQLDB, SQLDialect.IGNITE, SQLDialect.MARIADB, SQLDialect.MYSQL, SQLDialect.SQLITE, SQLDialect.TRINO);
    private static final Set<SQLDialect> SUPPORT_INSERT = SQLDialect.supportedBy(SQLDialect.H2, SQLDialect.MARIADB, SQLDialect.MYSQL);

    /* JADX INFO: Access modifiers changed from: package-private */
    public Overlay(Field<String> in, Field<String> placing, Field<? extends Number> startIndex) {
        super(Names.N_OVERLAY, Tools.allNotNull(SQLDataType.VARCHAR, in, placing, startIndex));
        this.in = Tools.nullSafeNotNull(in, SQLDataType.VARCHAR);
        this.placing = Tools.nullSafeNotNull(placing, SQLDataType.VARCHAR);
        this.startIndex = Tools.nullSafeNotNull(startIndex, SQLDataType.INTEGER);
        this.length = null;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public Overlay(Field<String> in, Field<String> placing, Field<? extends Number> startIndex, Field<? extends Number> length) {
        super(Names.N_OVERLAY, Tools.allNotNull(SQLDataType.VARCHAR, (Field<?>[]) new Field[]{in, placing, startIndex, length}));
        this.in = Tools.nullSafeNotNull(in, SQLDataType.VARCHAR);
        this.placing = Tools.nullSafeNotNull(placing, SQLDataType.VARCHAR);
        this.startIndex = Tools.nullSafeNotNull(startIndex, SQLDataType.INTEGER);
        this.length = Tools.nullSafeNotNull(length, SQLDataType.INTEGER);
    }

    /* JADX WARN: Type inference failed for: r0v29, types: [org.jooq.Context] */
    /* JADX WARN: Type inference failed for: r0v8, types: [org.jooq.Context] */
    @Override // org.jooq.impl.AbstractField, org.jooq.QueryPartInternal
    public final void accept(Context<?> ctx) {
        Field<? extends Number> l = this.length;
        if (l != null) {
            if (SUPPORT_INSERT.contains(ctx.dialect())) {
                ctx.visit(DSL.function(Names.N_INSERT, getDataType(), (Field<?>[]) new Field[]{this.in, this.startIndex, l, this.placing}));
                return;
            } else if (NO_SUPPORT.contains(ctx.dialect())) {
                ctx.visit((Field<?>) DSL.substring(this.in, DSL.inline(1), (Field<? extends Number>) Internal.isub(this.startIndex, DSL.inline(1))).concat(this.placing).concat(DSL.substring(this.in, (Field<? extends Number>) Internal.iadd(this.startIndex, l))));
                return;
            } else {
                ctx.visit(Names.N_OVERLAY).sql('(').visit((Field<?>) this.in).sql(' ').visit(Keywords.K_PLACING).sql(' ').visit((Field<?>) this.placing).sql(' ').visit(Keywords.K_FROM).sql(' ').visit((Field<?>) this.startIndex).sql(' ').visit(Keywords.K_FOR).sql(' ').visit((Field<?>) l).sql(')');
                return;
            }
        }
        if (SUPPORT_INSERT.contains(ctx.dialect())) {
            ctx.visit(DSL.function(Names.N_INSERT, getDataType(), (Field<?>[]) new Field[]{this.in, this.startIndex, DSL.length(this.placing), this.placing}));
        } else if (NO_SUPPORT.contains(ctx.dialect())) {
            ctx.visit((Field<?>) DSL.substring(this.in, DSL.inline(1), (Field<? extends Number>) Internal.isub(this.startIndex, DSL.inline(1))).concat(this.placing).concat(DSL.substring(this.in, (Field<? extends Number>) Internal.iadd(this.startIndex, DSL.length(this.placing)))));
        } else {
            ctx.visit(Names.N_OVERLAY).sql('(').visit((Field<?>) this.in).sql(' ').visit(Keywords.K_PLACING).sql(' ').visit((Field<?>) this.placing).sql(' ').visit(Keywords.K_FROM).sql(' ').visit((Field<?>) this.startIndex).sql(')');
        }
    }

    /* JADX WARN: Can't rename method to resolve collision */
    @Override // org.jooq.impl.QOM.UOperator4
    public final Field<String> $arg1() {
        return this.in;
    }

    /* JADX WARN: Can't rename method to resolve collision */
    @Override // org.jooq.impl.QOM.UOperator4
    public final Field<String> $arg2() {
        return this.placing;
    }

    /* JADX WARN: Can't rename method to resolve collision */
    @Override // org.jooq.impl.QOM.UOperator4
    public final Field<? extends Number> $arg3() {
        return this.startIndex;
    }

    /* JADX WARN: Can't rename method to resolve collision */
    @Override // org.jooq.impl.QOM.UOperator4
    public final Field<? extends Number> $arg4() {
        return this.length;
    }

    @Override // org.jooq.impl.QOM.UOperator4
    public final QOM.Overlay $arg1(Field<String> newValue) {
        return $constructor().apply(newValue, $arg2(), $arg3(), $arg4());
    }

    @Override // org.jooq.impl.QOM.UOperator4
    public final QOM.Overlay $arg2(Field<String> newValue) {
        return $constructor().apply($arg1(), newValue, $arg3(), $arg4());
    }

    @Override // org.jooq.impl.QOM.UOperator4
    public final QOM.Overlay $arg3(Field<? extends Number> newValue) {
        return $constructor().apply($arg1(), $arg2(), newValue, $arg4());
    }

    @Override // org.jooq.impl.QOM.UOperator4
    public final QOM.Overlay $arg4(Field<? extends Number> newValue) {
        return $constructor().apply($arg1(), $arg2(), $arg3(), newValue);
    }

    @Override // org.jooq.impl.QOM.UOperator4
    public final Function4<? super Field<String>, ? super Field<String>, ? super Field<? extends Number>, ? super Field<? extends Number>, ? extends QOM.Overlay> $constructor() {
        return (a1, a2, a3, a4) -> {
            return new Overlay(a1, a2, a3, a4);
        };
    }

    @Override // org.jooq.impl.AbstractNamed, org.jooq.impl.AbstractQueryPart, org.jooq.QueryPart
    public boolean equals(Object that) {
        if (!(that instanceof QOM.Overlay)) {
            return super.equals(that);
        }
        QOM.Overlay o = (QOM.Overlay) that;
        return StringUtils.equals($in(), o.$in()) && StringUtils.equals($placing(), o.$placing()) && StringUtils.equals($startIndex(), o.$startIndex()) && StringUtils.equals($length(), o.$length());
    }
}
