package org.jooq.impl;

import org.jooq.Case;
import org.jooq.CaseConditionStep;
import org.jooq.CaseValueStep;
import org.jooq.Condition;
import org.jooq.Field;
import org.jooq.Record1;
import org.jooq.Select;
import org.jooq.impl.QOM;

/* JADX INFO: Access modifiers changed from: package-private */
/* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/impl/CaseImpl.class */
public final class CaseImpl implements Case, QOM.UTransient {
    @Override // org.jooq.Case
    public final <V> CaseValueStep<V> value(V value) {
        return value((Field) Tools.field(value));
    }

    @Override // org.jooq.Case
    public final <V> CaseValueStep<V> value(Field<V> value) {
        return new CaseSimpleValueStep(value);
    }

    @Override // org.jooq.Case
    public final <T> CaseConditionStep<T> when(Condition condition, T result) {
        return when(condition, (Field) Tools.field(result));
    }

    @Override // org.jooq.Case
    public final <T> CaseConditionStep<T> when(Condition condition, Field<T> result) {
        return new CaseSearched(condition, result);
    }

    @Override // org.jooq.Case
    public final <T> CaseConditionStep<T> when(Condition condition, Select<? extends Record1<T>> result) {
        return when(condition, (Field) DSL.field(result));
    }

    @Override // org.jooq.Case
    public final <T> CaseConditionStep<T> when(Field<Boolean> condition, T result) {
        return when(DSL.condition(condition), (Condition) result);
    }

    @Override // org.jooq.Case
    public final <T> CaseConditionStep<T> when(Field<Boolean> condition, Field<T> result) {
        return when(DSL.condition(condition), (Field) result);
    }

    @Override // org.jooq.Case
    public final <T> CaseConditionStep<T> when(Field<Boolean> condition, Select<? extends Record1<T>> result) {
        return when(DSL.condition(condition), (Select) result);
    }
}
