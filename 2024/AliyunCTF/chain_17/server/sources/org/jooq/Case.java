package org.jooq;

import org.jetbrains.annotations.NotNull;

/* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/Case.class */
public interface Case {
    @Support
    @NotNull
    <V> CaseValueStep<V> value(V v);

    @Support
    @NotNull
    <V> CaseValueStep<V> value(Field<V> field);

    @Support
    @NotNull
    <T> CaseConditionStep<T> when(Condition condition, T t);

    @Support
    @NotNull
    <T> CaseConditionStep<T> when(Condition condition, Field<T> field);

    @Support
    @NotNull
    <T> CaseConditionStep<T> when(Condition condition, Select<? extends Record1<T>> select);

    @Support
    @NotNull
    <T> CaseConditionStep<T> when(Field<Boolean> field, T t);

    @Support
    @NotNull
    <T> CaseConditionStep<T> when(Field<Boolean> field, Field<T> field2);

    @Support
    @NotNull
    <T> CaseConditionStep<T> when(Field<Boolean> field, Select<? extends Record1<T>> select);
}
