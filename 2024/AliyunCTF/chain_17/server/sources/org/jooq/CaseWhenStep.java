package org.jooq;

import java.util.Map;
import org.jetbrains.annotations.NotNull;

/* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/CaseWhenStep.class */
public interface CaseWhenStep<V, T> extends Field<T> {
    @Support
    @NotNull
    CaseWhenStep<V, T> when(V v, T t);

    @Support
    @NotNull
    CaseWhenStep<V, T> when(V v, Field<T> field);

    @Support
    @NotNull
    CaseWhenStep<V, T> when(Field<V> field, T t);

    @Support
    @NotNull
    CaseWhenStep<V, T> when(Field<V> field, Field<T> field2);

    @Support
    @NotNull
    CaseWhenStep<V, T> mapValues(Map<V, T> map);

    @Support
    @NotNull
    CaseWhenStep<V, T> mapFields(Map<? extends Field<V>, ? extends Field<T>> map);

    @Support
    @NotNull
    Field<T> otherwise(T t);

    @Support
    @NotNull
    Field<T> otherwise(Field<T> field);

    @Support
    @NotNull
    Field<T> else_(T t);

    @Support
    @NotNull
    Field<T> else_(Field<T> field);
}
