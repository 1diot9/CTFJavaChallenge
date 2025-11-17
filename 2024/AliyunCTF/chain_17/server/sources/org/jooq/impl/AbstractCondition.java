package org.jooq.impl;

import ch.qos.logback.core.joran.conditional.IfAction;
import org.jooq.Clause;
import org.jooq.Condition;
import org.jooq.Context;
import org.jooq.Field;
import org.jooq.Operator;
import org.jooq.QueryPart;
import org.jooq.SQL;
import org.jooq.Select;

/* JADX INFO: Access modifiers changed from: package-private */
/* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/impl/AbstractCondition.class */
public abstract class AbstractCondition extends AbstractField<Boolean> implements Condition {
    private static final Clause[] CLAUSES = {Clause.CONDITION};

    /* JADX INFO: Access modifiers changed from: package-private */
    public AbstractCondition() {
        super(DSL.name(IfAction.CONDITION_ATTRIBUTE), SQLDataType.BOOLEAN);
    }

    @Override // org.jooq.impl.AbstractField, org.jooq.impl.AbstractQueryPart, org.jooq.QueryPartInternal
    public Clause[] clauses(Context<?> ctx) {
        return CLAUSES;
    }

    @Override // org.jooq.Condition
    public Condition and(Field<Boolean> other) {
        return and(DSL.condition(other));
    }

    @Override // org.jooq.Condition
    public final Condition or(Field<Boolean> other) {
        return or(DSL.condition(other));
    }

    @Override // org.jooq.Condition
    public final Condition xor(Field<Boolean> other) {
        return xor(DSL.condition(other));
    }

    @Override // org.jooq.Condition
    public final Condition and(SQL sql) {
        return and(DSL.condition(sql));
    }

    @Override // org.jooq.Condition
    public final Condition and(String sql) {
        return and(DSL.condition(sql));
    }

    @Override // org.jooq.Condition
    public final Condition and(String sql, Object... bindings) {
        return and(DSL.condition(sql, bindings));
    }

    @Override // org.jooq.Condition
    public final Condition and(String sql, QueryPart... parts) {
        return and(DSL.condition(sql, parts));
    }

    @Override // org.jooq.Condition
    public final Condition or(SQL sql) {
        return or(DSL.condition(sql));
    }

    @Override // org.jooq.Condition
    public final Condition or(String sql) {
        return or(DSL.condition(sql));
    }

    @Override // org.jooq.Condition
    public final Condition or(String sql, Object... bindings) {
        return or(DSL.condition(sql, bindings));
    }

    @Override // org.jooq.Condition
    public final Condition or(String sql, QueryPart... parts) {
        return or(DSL.condition(sql, parts));
    }

    @Override // org.jooq.Condition
    public final Condition xor(SQL sql) {
        return xor(DSL.condition(sql));
    }

    @Override // org.jooq.Condition
    public final Condition xor(String sql) {
        return xor(DSL.condition(sql));
    }

    @Override // org.jooq.Condition
    public final Condition xor(String sql, Object... bindings) {
        return xor(DSL.condition(sql, bindings));
    }

    @Override // org.jooq.Condition
    public final Condition xor(String sql, QueryPart... parts) {
        return xor(DSL.condition(sql, parts));
    }

    @Override // org.jooq.Condition
    public final Condition andNot(Condition other) {
        return and(other.not());
    }

    @Override // org.jooq.Condition
    public final Condition andNot(Field<Boolean> other) {
        return andNot(DSL.condition(other));
    }

    @Override // org.jooq.Condition
    public final Condition orNot(Condition other) {
        return or(other.not());
    }

    @Override // org.jooq.Condition
    public final Condition orNot(Field<Boolean> other) {
        return orNot(DSL.condition(other));
    }

    @Override // org.jooq.Condition
    public final Condition xorNot(Condition other) {
        return xor(other.not());
    }

    @Override // org.jooq.Condition
    public final Condition xorNot(Field<Boolean> other) {
        return xorNot(DSL.condition(other));
    }

    @Override // org.jooq.Condition
    public final Condition andExists(Select<?> select) {
        return and(DSL.exists(select));
    }

    @Override // org.jooq.Condition
    public final Condition andNotExists(Select<?> select) {
        return and(DSL.notExists(select));
    }

    @Override // org.jooq.Condition
    public final Condition orExists(Select<?> select) {
        return or(DSL.exists(select));
    }

    @Override // org.jooq.Condition
    public final Condition orNotExists(Select<?> select) {
        return or(DSL.notExists(select));
    }

    @Override // org.jooq.Condition
    public final Condition xorExists(Select<?> select) {
        return xor(DSL.exists(select));
    }

    @Override // org.jooq.Condition
    public final Condition xorNotExists(Select<?> select) {
        return xor(DSL.notExists(select));
    }

    @Override // org.jooq.Condition
    public final Condition and(Condition arg2) {
        return DSL.condition(Operator.AND, this, arg2);
    }

    @Override // org.jooq.Condition
    public final Condition not() {
        return DSL.not((Condition) this);
    }

    @Override // org.jooq.Condition
    public final Condition or(Condition arg2) {
        return DSL.condition(Operator.OR, this, arg2);
    }

    @Override // org.jooq.Condition
    public final Condition xor(Condition arg2) {
        return DSL.condition(Operator.XOR, this, arg2);
    }
}
