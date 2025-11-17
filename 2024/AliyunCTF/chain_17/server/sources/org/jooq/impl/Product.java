package org.jooq.impl;

import java.math.BigDecimal;
import java.util.function.Consumer;
import org.jooq.AggregateFunction;
import org.jooq.Context;
import org.jooq.Field;
import org.jooq.Function2;
import org.jooq.Name;
import org.jooq.impl.QOM;
import org.jooq.tools.StringUtils;

/* JADX INFO: Access modifiers changed from: package-private */
/* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/impl/Product.class */
public final class Product extends AbstractAggregateFunction<BigDecimal> implements QOM.Product {
    /* JADX INFO: Access modifiers changed from: package-private */
    public Product(Field<? extends Number> field, boolean distinct) {
        super(distinct, Names.N_PRODUCT, SQLDataType.NUMERIC, (Field<?>[]) new Field[]{Tools.nullSafeNotNull(field, SQLDataType.INTEGER)});
    }

    @Override // org.jooq.impl.AbstractAggregateFunction, org.jooq.impl.AbstractField, org.jooq.QueryPartInternal
    public final void accept(Context<?> ctx) {
        switch (ctx.family()) {
            case DUCKDB:
                acceptNative(ctx);
                return;
            default:
                acceptEmulation(ctx);
                return;
        }
    }

    private final void acceptNative(Context<?> ctx) {
        switch (ctx.family()) {
            default:
                Name name = Names.N_PRODUCT;
                ctx.visit((Field<?>) CustomField.of(name, SQLDataType.NUMERIC, (Consumer<? super Context<?>>) c -> {
                    AggregateFunction aggregate;
                    if (this.distinct) {
                        aggregate = DSL.aggregateDistinct(name, SQLDataType.NUMERIC, (Field<?>[]) this.arguments.toArray(Tools.EMPTY_FIELD));
                    } else {
                        aggregate = DSL.aggregate(name, SQLDataType.NUMERIC, (Field<?>[]) this.arguments.toArray(Tools.EMPTY_FIELD));
                    }
                    c.visit((Field<?>) aggregate);
                    acceptFilterClause(c);
                    acceptOverClause(c);
                }));
                return;
        }
    }

    private final void acceptEmulation(Context<?> ctx) {
        Field<?> field = this.arguments.get(0);
        Field<Integer> negatives = DSL.when(field.lt((Field<?>) DSL.zero()), (Field) DSL.inline(-1));
        Field<BigDecimal> negativesSum = CustomField.of("sum", SQLDataType.NUMERIC, (Consumer<? super Context<?>>) c -> {
            AggregateFunction<BigDecimal> sum;
            if (this.distinct) {
                sum = DSL.sumDistinct(negatives);
            } else {
                sum = DSL.sum(negatives);
            }
            c.visit((Field<?>) sum);
            acceptFilterClause(c);
            acceptOverClause(c);
        });
        Field<BigDecimal> zerosSum = CustomField.of("sum", SQLDataType.NUMERIC, (Consumer<? super Context<?>>) c2 -> {
            c2.visit((Field<?>) DSL.sum(DSL.choose(field).when((Field) DSL.zero(), (Field) DSL.one())));
            acceptFilterClause(c2);
            acceptOverClause(c2);
        });
        Field<BigDecimal> logarithmsSum = CustomField.of("sum", SQLDataType.NUMERIC, (Consumer<? super Context<?>>) c3 -> {
            AggregateFunction<BigDecimal> sum;
            Field<Integer> abs = DSL.abs(DSL.nullif(field, (Field) DSL.zero()));
            Field<BigDecimal> ln = DSL.ln(abs);
            if (this.distinct) {
                sum = DSL.sumDistinct(ln);
            } else {
                sum = DSL.sum(ln);
            }
            c3.visit((Field<?>) sum);
            acceptFilterClause(c3);
            acceptOverClause(c3);
        });
        ctx.visit(Internal.imul(DSL.when(zerosSum.gt(DSL.inline(BigDecimal.ZERO)), (Field) DSL.zero()).when(negativesSum.mod(DSL.inline(2)).lt(DSL.inline(BigDecimal.ZERO)), (Field) DSL.inline(-1)).otherwise((Field) DSL.one()), DSL.exp(logarithmsSum)));
    }

    @Override // org.jooq.impl.QOM.Product
    public final Field<? extends Number> $field() {
        return (Field) getArguments().get(0);
    }

    @Override // org.jooq.impl.QOM.Product
    public final QOM.Product $field(Field<? extends Number> newValue) {
        return $constructor().apply(newValue, Boolean.valueOf($distinct()));
    }

    @Override // org.jooq.impl.QOM.Product
    public final QOM.Product $distinct(boolean newValue) {
        return $constructor().apply($field(), Boolean.valueOf(newValue));
    }

    public final Function2<? super Field<? extends Number>, ? super Boolean, ? extends QOM.Product> $constructor() {
        return (a1, a2) -> {
            return new Product(a1, a2.booleanValue());
        };
    }

    @Override // org.jooq.impl.AbstractNamed, org.jooq.impl.AbstractQueryPart, org.jooq.QueryPart
    public boolean equals(Object that) {
        if (!(that instanceof QOM.Product)) {
            return super.equals(that);
        }
        QOM.Product o = (QOM.Product) that;
        return StringUtils.equals($field(), o.$field()) && $distinct() == o.$distinct();
    }
}
