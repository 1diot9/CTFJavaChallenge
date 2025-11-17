package org.jooq;

import org.jetbrains.annotations.NotNull;

/* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/CaseConditionStep.class */
public interface CaseConditionStep<T> extends Field<T> {
    @Support
    @NotNull
    CaseConditionStep<T> when(Condition condition, T t);

    @Support
    @NotNull
    CaseConditionStep<T> when(Condition condition, Field<T> field);

    @Support
    @NotNull
    CaseConditionStep<T> when(Condition condition, Select<? extends Record1<T>> select);

    @Support
    @NotNull
    CaseConditionStep<T> when(Field<Boolean> field, T t);

    @Support
    @NotNull
    CaseConditionStep<T> when(Field<Boolean> field, Field<T> field2);

    @Support
    @NotNull
    CaseConditionStep<T> when(Field<Boolean> field, Select<? extends Record1<T>> select);

    @Support
    @NotNull
    Field<T> otherwise(T t);

    @Support
    @NotNull
    Field<T> otherwise(Field<T> field);

    @Support
    @NotNull
    Field<T> otherwise(Select<? extends Record1<T>> select);

    @Support
    @NotNull
    Field<T> else_(T t);

    @Support
    @NotNull
    Field<T> else_(Field<T> field);

    @Support
    @NotNull
    Field<T> else_(Select<? extends Record1<T>> select);
}
