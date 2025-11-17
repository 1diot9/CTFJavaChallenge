package org.jooq.impl;

import java.util.LinkedHashMap;
import java.util.Map;
import org.jooq.CaseValueStep;
import org.jooq.CaseWhenStep;
import org.jooq.Field;
import org.jooq.Record1;
import org.jooq.Select;
import org.jooq.impl.QOM;

/* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/impl/CaseSimpleValueStep.class */
final class CaseSimpleValueStep<V> implements CaseValueStep<V>, QOM.UTransient {
    private final Field<V> value;

    /* JADX INFO: Access modifiers changed from: package-private */
    public CaseSimpleValueStep(Field<V> value) {
        this.value = value;
    }

    @Override // org.jooq.CaseValueStep
    public final <T> CaseWhenStep<V, T> when(V compareValue, T result) {
        return when((Field) Tools.field(compareValue, this.value), (Field) Tools.field(result));
    }

    @Override // org.jooq.CaseValueStep
    public final <T> CaseWhenStep<V, T> when(V compareValue, Field<T> result) {
        return when((Field) Tools.field(compareValue, this.value), (Field) result);
    }

    @Override // org.jooq.CaseValueStep
    public final <T> CaseWhenStep<V, T> when(V compareValue, Select<? extends Record1<T>> result) {
        return when((Field) Tools.field(compareValue, this.value), (Field) DSL.field(result));
    }

    @Override // org.jooq.CaseValueStep
    public final <T> CaseWhenStep<V, T> when(Field<V> compareValue, T result) {
        return when((Field) compareValue, (Field) Tools.field(result));
    }

    @Override // org.jooq.CaseValueStep
    public final <T> CaseWhenStep<V, T> when(Field<V> compareValue, Field<T> result) {
        return new CaseSimple(this.value, compareValue, result);
    }

    @Override // org.jooq.CaseValueStep
    public final <T> CaseWhenStep<V, T> when(Field<V> compareValue, Select<? extends Record1<T>> result) {
        return when((Field) compareValue, (Field) DSL.field(result));
    }

    @Override // org.jooq.CaseValueStep
    public final <T> CaseWhenStep<V, T> mapValues(Map<V, T> values) {
        Map<Field<V>, Field<T>> fields = new LinkedHashMap<>();
        values.forEach((k, v) -> {
            fields.put(Tools.field(k, this.value), Tools.field(v));
        });
        return mapFields(fields);
    }

    @Override // org.jooq.CaseValueStep
    public final <T> CaseWhenStep<V, T> mapFields(Map<? extends Field<V>, ? extends Field<T>> fields) {
        return new CaseSimple(this.value, fields);
    }
}
