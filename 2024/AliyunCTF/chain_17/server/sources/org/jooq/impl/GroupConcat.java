package org.jooq.impl;

import java.util.Arrays;
import java.util.Collection;
import java.util.Set;
import org.jooq.AggregateFilterStep;
import org.jooq.AggregateFunction;
import org.jooq.Context;
import org.jooq.Field;
import org.jooq.GroupConcatOrderByStep;
import org.jooq.GroupConcatSeparatorStep;
import org.jooq.OrderField;
import org.jooq.SQLDialect;
import org.jooq.WindowRowsStep;
import org.jooq.impl.QOM;

/* JADX INFO: Access modifiers changed from: package-private */
/* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/impl/GroupConcat.class */
public final class GroupConcat extends AbstractAggregateFunction<String> implements GroupConcatOrderByStep, QOM.UNotYetImplemented {
    final Set<SQLDialect> REQUIRE_WITHIN_GROUP;
    private final Field<?> field;
    private final SortFieldList orderBy;
    private String separator;

    @Override // org.jooq.impl.AbstractAggregateFunction, org.jooq.impl.AbstractWindowFunction, org.jooq.WindowOrderByStep, org.jooq.ArrayAggOrderByStep
    public /* bridge */ /* synthetic */ AbstractAggregateFunction orderBy(Collection collection) {
        return orderBy((Collection<? extends OrderField<?>>) collection);
    }

    @Override // org.jooq.impl.AbstractAggregateFunction, org.jooq.impl.AbstractWindowFunction, org.jooq.WindowOrderByStep, org.jooq.ArrayAggOrderByStep
    public /* bridge */ /* synthetic */ AbstractAggregateFunction orderBy(OrderField[] orderFieldArr) {
        return orderBy((OrderField<?>[]) orderFieldArr);
    }

    @Override // org.jooq.impl.AbstractAggregateFunction, org.jooq.ArrayAggOrderByStep
    public /* bridge */ /* synthetic */ AggregateFilterStep orderBy(Collection collection) {
        return orderBy((Collection<? extends OrderField<?>>) collection);
    }

    @Override // org.jooq.impl.AbstractAggregateFunction, org.jooq.ArrayAggOrderByStep
    public /* bridge */ /* synthetic */ AggregateFilterStep orderBy(OrderField[] orderFieldArr) {
        return orderBy((OrderField<?>[]) orderFieldArr);
    }

    @Override // org.jooq.impl.AbstractAggregateFunction, org.jooq.impl.AbstractWindowFunction, org.jooq.WindowOrderByStep, org.jooq.ArrayAggOrderByStep
    public /* bridge */ /* synthetic */ AbstractWindowFunction orderBy(Collection collection) {
        return orderBy((Collection<? extends OrderField<?>>) collection);
    }

    @Override // org.jooq.impl.AbstractAggregateFunction, org.jooq.impl.AbstractWindowFunction, org.jooq.WindowOrderByStep, org.jooq.ArrayAggOrderByStep
    public /* bridge */ /* synthetic */ AbstractWindowFunction orderBy(OrderField[] orderFieldArr) {
        return orderBy((OrderField<?>[]) orderFieldArr);
    }

    @Override // org.jooq.impl.AbstractAggregateFunction, org.jooq.impl.AbstractWindowFunction, org.jooq.WindowOrderByStep, org.jooq.ArrayAggOrderByStep
    public /* bridge */ /* synthetic */ WindowRowsStep orderBy(Collection collection) {
        return orderBy((Collection<? extends OrderField<?>>) collection);
    }

    @Override // org.jooq.impl.AbstractAggregateFunction, org.jooq.impl.AbstractWindowFunction, org.jooq.WindowOrderByStep, org.jooq.ArrayAggOrderByStep
    public /* bridge */ /* synthetic */ WindowRowsStep orderBy(OrderField[] orderFieldArr) {
        return orderBy((OrderField<?>[]) orderFieldArr);
    }

    @Override // org.jooq.impl.AbstractAggregateFunction, org.jooq.ArrayAggOrderByStep
    public /* bridge */ /* synthetic */ GroupConcatSeparatorStep orderBy(Collection collection) {
        return orderBy((Collection<? extends OrderField<?>>) collection);
    }

    @Override // org.jooq.impl.AbstractAggregateFunction, org.jooq.ArrayAggOrderByStep
    public /* bridge */ /* synthetic */ GroupConcatSeparatorStep orderBy(OrderField[] orderFieldArr) {
        return orderBy((OrderField<?>[]) orderFieldArr);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public GroupConcat(Field<?> field) {
        this(field, false);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public GroupConcat(Field<?> field, boolean distinct) {
        super(distinct, Names.N_GROUP_CONCAT, SQLDataType.VARCHAR, (Field<?>[]) new Field[]{field});
        this.REQUIRE_WITHIN_GROUP = SQLDialect.supportedBy(SQLDialect.TRINO);
        this.field = field;
        this.orderBy = new SortFieldList();
    }

    @Override // org.jooq.impl.AbstractAggregateFunction, org.jooq.impl.AbstractField, org.jooq.QueryPartInternal
    public final void accept(Context<?> context) {
        ListAgg result;
        if (this.separator == null) {
            result = new ListAgg(this.distinct, this.field, DSL.inline(","));
        } else {
            result = new ListAgg(this.distinct, this.field, DSL.inline(this.separator));
        }
        if (!this.orderBy.isEmpty()) {
            result.withinGroupOrderBy((Collection<? extends OrderField<?>>) this.orderBy);
        } else if (this.REQUIRE_WITHIN_GROUP.contains(context.dialect())) {
            result.withinGroupOrderBy((Collection<? extends OrderField<?>>) this.orderBy);
        }
        context.visit((Field<?>) fo(result));
    }

    @Override // org.jooq.GroupConcatSeparatorStep
    public final AggregateFunction<String> separator(String s) {
        this.separator = s;
        return this;
    }

    @Override // org.jooq.impl.AbstractAggregateFunction, org.jooq.impl.AbstractWindowFunction, org.jooq.WindowOrderByStep, org.jooq.ArrayAggOrderByStep
    public final GroupConcat orderBy(OrderField<?>... fields) {
        return orderBy((Collection<? extends OrderField<?>>) Arrays.asList(fields));
    }

    @Override // org.jooq.impl.AbstractAggregateFunction, org.jooq.impl.AbstractWindowFunction, org.jooq.WindowOrderByStep, org.jooq.ArrayAggOrderByStep
    public final GroupConcat orderBy(Collection<? extends OrderField<?>> fields) {
        this.orderBy.addAll(Tools.sortFields(fields));
        return this;
    }
}
