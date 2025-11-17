package org.jooq;

import java.util.Map;
import org.jetbrains.annotations.NotNull;

/* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/CaseValueStep.class */
public interface CaseValueStep<V> {
    @Support
    @NotNull
    <T> CaseWhenStep<V, T> when(V v, T t);

    @Support
    @NotNull
    <T> CaseWhenStep<V, T> when(V v, Field<T> field);

    @Support
    @NotNull
    <T> CaseWhenStep<V, T> when(V v, Select<? extends Record1<T>> select);

    @Support
    @NotNull
    <T> CaseWhenStep<V, T> when(Field<V> field, T t);

    @Support
    @NotNull
    <T> CaseWhenStep<V, T> when(Field<V> field, Field<T> field2);

    @Support
    @NotNull
    <T> CaseWhenStep<V, T> when(Field<V> field, Select<? extends Record1<T>> select);

    @Support
    @NotNull
    <T> CaseWhenStep<V, T> mapValues(Map<V, T> map);

    @Support
    @NotNull
    <T> CaseWhenStep<V, T> mapFields(Map<? extends Field<V>, ? extends Field<T>> map);
}
