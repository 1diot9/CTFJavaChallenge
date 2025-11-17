package org.jooq.impl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import org.jooq.Condition;
import org.jooq.ConditionProvider;
import org.jooq.DivideByOnConditionStep;
import org.jooq.DivideByOnStep;
import org.jooq.Field;
import org.jooq.GroupField;
import org.jooq.Operator;
import org.jooq.QueryPart;
import org.jooq.Record;
import org.jooq.SQL;
import org.jooq.Select;
import org.jooq.Table;

/* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/impl/DivideBy.class */
final class DivideBy implements DivideByOnStep, DivideByOnConditionStep {
    private final Table<?> dividend;
    private final Table<?> divisor;
    private final ConditionProviderImpl condition = new ConditionProviderImpl();
    private final QueryPartList<Field<?>> returning = new QueryPartList<>();

    /* JADX INFO: Access modifiers changed from: package-private */
    public DivideBy(Table<?> dividend, Table<?> divisor) {
        this.dividend = dividend;
        this.divisor = divisor;
    }

    private final Table<Record> table() {
        ConditionProviderImpl selfJoin = new ConditionProviderImpl();
        ArrayList arrayList = new ArrayList(this.returning.size());
        Table<?> outer = this.dividend.as("dividend");
        Iterator<Field<?>> it = this.returning.iterator();
        while (it.hasNext()) {
            Field<T> field = (Field) it.next();
            GroupField field2 = outer.field(field);
            if (field2 == null) {
                arrayList.add(field);
            } else {
                arrayList.add(field2);
                selfJoin(selfJoin, outer, this.dividend, field);
            }
        }
        return DSL.selectDistinct(arrayList).from(outer).whereNotExists(DSL.selectOne().from(this.divisor).whereNotExists(DSL.selectOne().from(this.dividend).where((Condition) selfJoin).and((Condition) this.condition))).asTable();
    }

    private final <T> void selfJoin(ConditionProvider selfJoin, Table<?> outer, Table<?> inner, Field<T> field) {
        Field<T> outerField = outer.field(field);
        Field<T> innerField = inner.field(field);
        if (outerField != null && innerField != null) {
            selfJoin.addConditions(outerField.equal((Field) innerField));
        }
    }

    @Override // org.jooq.DivideByOnStep
    public final DivideByOnConditionStep on(Condition conditions) {
        this.condition.addConditions(conditions);
        return this;
    }

    @Override // org.jooq.DivideByOnStep
    public final DivideByOnConditionStep on(Condition... conditions) {
        this.condition.addConditions(conditions);
        return this;
    }

    @Override // org.jooq.DivideByOnStep
    public final DivideByOnConditionStep on(Field<Boolean> c) {
        return on(DSL.condition(c));
    }

    @Override // org.jooq.DivideByOnStep
    public final DivideByOnConditionStep on(SQL sql) {
        and(sql);
        return this;
    }

    @Override // org.jooq.DivideByOnStep
    public final DivideByOnConditionStep on(String sql) {
        and(sql);
        return this;
    }

    @Override // org.jooq.DivideByOnStep
    public final DivideByOnConditionStep on(String sql, Object... bindings) {
        and(sql, bindings);
        return this;
    }

    @Override // org.jooq.DivideByOnStep
    public final DivideByOnConditionStep on(String sql, QueryPart... parts) {
        and(sql, parts);
        return this;
    }

    @Override // org.jooq.DivideByReturningStep
    public final Table<Record> returning(Field<?>... fields) {
        return returning(Arrays.asList(fields));
    }

    @Override // org.jooq.DivideByReturningStep
    public final Table<Record> returning(Collection<? extends Field<?>> fields) {
        this.returning.addAll(fields);
        return table();
    }

    @Override // org.jooq.DivideByOnConditionStep
    public final DivideByOnConditionStep and(Condition c) {
        this.condition.addConditions(c);
        return this;
    }

    @Override // org.jooq.DivideByOnConditionStep
    public final DivideByOnConditionStep and(Field<Boolean> c) {
        return and(DSL.condition(c));
    }

    @Override // org.jooq.DivideByOnConditionStep
    public final DivideByOnConditionStep and(SQL sql) {
        return and(DSL.condition(sql));
    }

    @Override // org.jooq.DivideByOnConditionStep
    public final DivideByOnConditionStep and(String sql) {
        return and(DSL.condition(sql));
    }

    @Override // org.jooq.DivideByOnConditionStep
    public final DivideByOnConditionStep and(String sql, Object... bindings) {
        return and(DSL.condition(sql, bindings));
    }

    @Override // org.jooq.DivideByOnConditionStep
    public final DivideByOnConditionStep and(String sql, QueryPart... parts) {
        return and(DSL.condition(sql, parts));
    }

    @Override // org.jooq.DivideByOnConditionStep
    public final DivideByOnConditionStep andNot(Condition c) {
        return and(c.not());
    }

    @Override // org.jooq.DivideByOnConditionStep
    public final DivideByOnConditionStep andNot(Field<Boolean> c) {
        return andNot(DSL.condition(c));
    }

    @Override // org.jooq.DivideByOnConditionStep
    public final DivideByOnConditionStep andExists(Select<?> select) {
        return and(DSL.exists(select));
    }

    @Override // org.jooq.DivideByOnConditionStep
    public final DivideByOnConditionStep andNotExists(Select<?> select) {
        return and(DSL.notExists(select));
    }

    @Override // org.jooq.DivideByOnConditionStep
    public final DivideByOnConditionStep or(Condition c) {
        this.condition.addConditions(Operator.OR, c);
        return this;
    }

    @Override // org.jooq.DivideByOnConditionStep
    public final DivideByOnConditionStep or(Field<Boolean> c) {
        return or(DSL.condition(c));
    }

    @Override // org.jooq.DivideByOnConditionStep
    public final DivideByOnConditionStep or(SQL sql) {
        return or(DSL.condition(sql));
    }

    @Override // org.jooq.DivideByOnConditionStep
    public final DivideByOnConditionStep or(String sql) {
        return or(DSL.condition(sql));
    }

    @Override // org.jooq.DivideByOnConditionStep
    public final DivideByOnConditionStep or(String sql, Object... bindings) {
        return or(DSL.condition(sql, bindings));
    }

    @Override // org.jooq.DivideByOnConditionStep
    public final DivideByOnConditionStep or(String sql, QueryPart... parts) {
        return or(DSL.condition(sql, parts));
    }

    @Override // org.jooq.DivideByOnConditionStep
    public final DivideByOnConditionStep orNot(Condition c) {
        return or(c.not());
    }

    @Override // org.jooq.DivideByOnConditionStep
    public final DivideByOnConditionStep orNot(Field<Boolean> c) {
        return orNot(DSL.condition(c));
    }

    @Override // org.jooq.DivideByOnConditionStep
    public final DivideByOnConditionStep orExists(Select<?> select) {
        return or(DSL.exists(select));
    }

    @Override // org.jooq.DivideByOnConditionStep
    public final DivideByOnConditionStep orNotExists(Select<?> select) {
        return or(DSL.notExists(select));
    }
}
