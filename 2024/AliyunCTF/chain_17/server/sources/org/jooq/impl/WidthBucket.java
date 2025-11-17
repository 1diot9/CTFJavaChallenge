package org.jooq.impl;

import java.lang.Number;
import org.jooq.Context;
import org.jooq.Field;
import org.jooq.Function4;
import org.jooq.impl.QOM;
import org.jooq.tools.StringUtils;

/* JADX INFO: Access modifiers changed from: package-private */
/* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/impl/WidthBucket.class */
public final class WidthBucket<T extends Number> extends AbstractField<T> implements QOM.WidthBucket<T> {
    final Field<T> field;
    final Field<T> low;
    final Field<T> high;
    final Field<Integer> buckets;

    /* JADX INFO: Access modifiers changed from: package-private */
    public WidthBucket(Field<T> field, Field<T> low, Field<T> high, Field<Integer> buckets) {
        super(Names.N_WIDTH_BUCKET, Tools.allNotNull(Tools.dataType(SQLDataType.INTEGER, field, false), (Field<?>[]) new Field[]{field, low, high, buckets}));
        this.field = Tools.nullSafeNotNull(field, SQLDataType.INTEGER);
        this.low = Tools.nullSafeNotNull(low, SQLDataType.INTEGER);
        this.high = Tools.nullSafeNotNull(high, SQLDataType.INTEGER);
        this.buckets = Tools.nullSafeNotNull(buckets, SQLDataType.INTEGER);
    }

    /* JADX WARN: Type inference failed for: r0v3, types: [org.jooq.Context] */
    @Override // org.jooq.impl.AbstractField, org.jooq.QueryPartInternal
    public void accept(Context<?> ctx) {
        switch (ctx.family()) {
            case CUBRID:
            case DERBY:
            case DUCKDB:
            case FIREBIRD:
            case H2:
            case HSQLDB:
            case IGNITE:
            case MARIADB:
            case MYSQL:
            case SQLITE:
                ctx.visit(DSL.when(this.field.lt(this.low), (Field) DSL.zero()).when(this.field.ge(this.high), Internal.iadd(this.buckets, DSL.one())).otherwise(Internal.iadd(DSL.floor(Internal.idiv(Internal.imul(Internal.isub(this.field, this.low), this.buckets), Internal.isub(this.high, this.low))), DSL.one())));
                return;
            default:
                ctx.visit(Names.N_WIDTH_BUCKET).sql('(').visit((Field<?>) this.field).sql(", ").visit((Field<?>) this.low).sql(", ").visit((Field<?>) this.high).sql(", ").visit((Field<?>) this.buckets).sql(')');
                return;
        }
    }

    @Override // org.jooq.impl.QOM.UOperator4
    public final Field<T> $arg1() {
        return this.field;
    }

    @Override // org.jooq.impl.QOM.UOperator4
    public final Field<T> $arg2() {
        return this.low;
    }

    @Override // org.jooq.impl.QOM.UOperator4
    public final Field<T> $arg3() {
        return this.high;
    }

    /* JADX WARN: Can't rename method to resolve collision */
    @Override // org.jooq.impl.QOM.UOperator4
    public final Field<Integer> $arg4() {
        return this.buckets;
    }

    @Override // org.jooq.impl.QOM.UOperator4
    public final QOM.WidthBucket<T> $arg1(Field<T> newValue) {
        return $constructor().apply(newValue, $arg2(), $arg3(), $arg4());
    }

    @Override // org.jooq.impl.QOM.UOperator4
    public final QOM.WidthBucket<T> $arg2(Field<T> newValue) {
        return $constructor().apply($arg1(), newValue, $arg3(), $arg4());
    }

    @Override // org.jooq.impl.QOM.UOperator4
    public final QOM.WidthBucket<T> $arg3(Field<T> newValue) {
        return $constructor().apply($arg1(), $arg2(), newValue, $arg4());
    }

    @Override // org.jooq.impl.QOM.UOperator4
    public final QOM.WidthBucket<T> $arg4(Field<Integer> newValue) {
        return $constructor().apply($arg1(), $arg2(), $arg3(), newValue);
    }

    @Override // org.jooq.impl.QOM.UOperator4
    public final Function4<? super Field<T>, ? super Field<T>, ? super Field<T>, ? super Field<Integer>, ? extends QOM.WidthBucket<T>> $constructor() {
        return (a1, a2, a3, a4) -> {
            return new WidthBucket(a1, a2, a3, a4);
        };
    }

    @Override // org.jooq.impl.AbstractNamed, org.jooq.impl.AbstractQueryPart, org.jooq.QueryPart
    public boolean equals(Object that) {
        if (!(that instanceof QOM.WidthBucket)) {
            return super.equals(that);
        }
        QOM.WidthBucket<?> o = (QOM.WidthBucket) that;
        return StringUtils.equals($field(), o.$field()) && StringUtils.equals($low(), o.$low()) && StringUtils.equals($high(), o.$high()) && StringUtils.equals($buckets(), o.$buckets());
    }
}
