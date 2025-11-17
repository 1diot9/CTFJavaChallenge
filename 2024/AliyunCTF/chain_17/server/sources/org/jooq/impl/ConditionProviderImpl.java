package org.jooq.impl;

import ch.qos.logback.core.joran.conditional.IfAction;
import java.util.Arrays;
import java.util.Collection;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jooq.Condition;
import org.jooq.ConditionProvider;
import org.jooq.Context;
import org.jooq.Field;
import org.jooq.Operator;
import org.jooq.QueryPart;
import org.jooq.SQL;
import org.jooq.Select;
import org.jooq.impl.QOM;

/* JADX INFO: Access modifiers changed from: package-private */
/* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/impl/ConditionProviderImpl.class */
public final class ConditionProviderImpl extends AbstractField<Boolean> implements ConditionProvider, Condition, QOM.UProxy<Condition> {
    private Condition condition;

    /* JADX INFO: Access modifiers changed from: package-private */
    public ConditionProviderImpl() {
        this(null);
    }

    ConditionProviderImpl(Condition condition) {
        super(DSL.name(IfAction.CONDITION_ATTRIBUTE), SQLDataType.BOOLEAN);
        this.condition = condition;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static final Condition extractCondition(Condition c) {
        if (!(c instanceof ConditionProviderImpl)) {
            return c;
        }
        ConditionProviderImpl cp = (ConditionProviderImpl) c;
        return cp.getWhere();
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    @Nullable
    public final Condition getWhereOrNull() {
        if (hasWhere()) {
            return this.condition;
        }
        return null;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    @NotNull
    public final Condition getWhere() {
        return hasWhere() ? extractCondition(this.condition) : DSL.noCondition();
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public final void setWhere(Condition newCondition) {
        this.condition = newCondition;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public final boolean hasWhere() {
        return (this.condition == null || (this.condition instanceof NoCondition)) ? false : true;
    }

    @Override // org.jooq.ConditionProvider
    public final void addConditions(Condition conditions) {
        addConditions(Operator.AND, conditions);
    }

    @Override // org.jooq.ConditionProvider
    public final void addConditions(Condition... conditions) {
        addConditions(Operator.AND, conditions);
    }

    @Override // org.jooq.ConditionProvider
    public final void addConditions(Collection<? extends Condition> conditions) {
        addConditions(Operator.AND, conditions);
    }

    @Override // org.jooq.ConditionProvider
    public final void addConditions(Operator operator, Condition conditions) {
        if (hasWhere()) {
            setWhere(DSL.condition(operator, getWhere(), conditions));
        } else {
            setWhere(conditions);
        }
    }

    @Override // org.jooq.ConditionProvider
    public final void addConditions(Operator operator, Condition... conditions) {
        addConditions(operator, Arrays.asList(conditions));
    }

    @Override // org.jooq.ConditionProvider
    public final void addConditions(Operator operator, Collection<? extends Condition> conditions) {
        Condition c;
        if (!conditions.isEmpty()) {
            if (conditions.size() == 1) {
                c = conditions.iterator().next();
            } else {
                c = DSL.condition(operator, conditions);
            }
            addConditions(operator, c);
        }
    }

    @Override // org.jooq.impl.AbstractField, org.jooq.QueryPartInternal
    public final void accept(Context<?> ctx) {
        ctx.visit(getWhere());
    }

    @Override // org.jooq.Condition
    public final Condition and(Condition other) {
        return getWhere().and(other);
    }

    @Override // org.jooq.Condition
    public final Condition and(Field<Boolean> other) {
        return getWhere().and(other);
    }

    @Override // org.jooq.Condition
    public final Condition and(SQL sql) {
        return getWhere().and(sql);
    }

    @Override // org.jooq.Condition
    public final Condition and(String sql) {
        return getWhere().and(sql);
    }

    @Override // org.jooq.Condition
    public final Condition and(String sql, Object... bindings) {
        return getWhere().and(sql, bindings);
    }

    @Override // org.jooq.Condition
    public final Condition and(String sql, QueryPart... parts) {
        return getWhere().and(sql, parts);
    }

    @Override // org.jooq.Condition
    public final Condition andNot(Condition other) {
        return getWhere().andNot(other);
    }

    @Override // org.jooq.Condition
    public final Condition andNot(Field<Boolean> other) {
        return getWhere().andNot(other);
    }

    @Override // org.jooq.Condition
    public final Condition andExists(Select<?> select) {
        return getWhere().andExists(select);
    }

    @Override // org.jooq.Condition
    public final Condition andNotExists(Select<?> select) {
        return getWhere().andNotExists(select);
    }

    @Override // org.jooq.Condition
    public final Condition or(Condition other) {
        return getWhere().or(other);
    }

    @Override // org.jooq.Condition
    public final Condition or(Field<Boolean> other) {
        return getWhere().or(other);
    }

    @Override // org.jooq.Condition
    public final Condition or(SQL sql) {
        return getWhere().or(sql);
    }

    @Override // org.jooq.Condition
    public final Condition or(String sql) {
        return getWhere().or(sql);
    }

    @Override // org.jooq.Condition
    public final Condition or(String sql, Object... bindings) {
        return getWhere().or(sql, bindings);
    }

    @Override // org.jooq.Condition
    public final Condition or(String sql, QueryPart... parts) {
        return getWhere().or(sql, parts);
    }

    @Override // org.jooq.Condition
    public final Condition orNot(Condition other) {
        return getWhere().orNot(other);
    }

    @Override // org.jooq.Condition
    public final Condition orNot(Field<Boolean> other) {
        return getWhere().orNot(other);
    }

    @Override // org.jooq.Condition
    public final Condition orExists(Select<?> select) {
        return getWhere().orExists(select);
    }

    @Override // org.jooq.Condition
    public final Condition orNotExists(Select<?> select) {
        return getWhere().orNotExists(select);
    }

    @Override // org.jooq.Condition
    public final Condition xor(Condition other) {
        return getWhere().xor(other);
    }

    @Override // org.jooq.Condition
    public final Condition xor(Field<Boolean> other) {
        return getWhere().xor(other);
    }

    @Override // org.jooq.Condition
    public final Condition xor(SQL sql) {
        return getWhere().xor(sql);
    }

    @Override // org.jooq.Condition
    public final Condition xor(String sql) {
        return getWhere().xor(sql);
    }

    @Override // org.jooq.Condition
    public final Condition xor(String sql, Object... bindings) {
        return getWhere().xor(sql, bindings);
    }

    @Override // org.jooq.Condition
    public final Condition xor(String sql, QueryPart... parts) {
        return getWhere().xor(sql, parts);
    }

    @Override // org.jooq.Condition
    public final Condition xorNot(Condition other) {
        return getWhere().xorNot(other);
    }

    @Override // org.jooq.Condition
    public final Condition xorNot(Field<Boolean> other) {
        return getWhere().xorNot(other);
    }

    @Override // org.jooq.Condition
    public final Condition xorExists(Select<?> select) {
        return getWhere().xorExists(select);
    }

    @Override // org.jooq.Condition
    public final Condition xorNotExists(Select<?> select) {
        return getWhere().xorNotExists(select);
    }

    @Override // org.jooq.Condition
    public final Condition not() {
        return getWhere().not();
    }

    /* JADX WARN: Can't rename method to resolve collision */
    @Override // org.jooq.impl.QOM.UProxy
    public final Condition $delegate() {
        return getWhere();
    }
}
