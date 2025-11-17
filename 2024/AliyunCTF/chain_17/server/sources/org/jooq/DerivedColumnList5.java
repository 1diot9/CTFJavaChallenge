package org.jooq;

import org.jetbrains.annotations.NotNull;

/* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/DerivedColumnList5.class */
public interface DerivedColumnList5 extends QueryPart {
    @Support
    @NotNull
    <R extends Record5<?, ?, ?, ?, ?>> CommonTableExpression<R> as(ResultQuery<R> resultQuery);

    @Support
    @NotNull
    <R extends Record5<?, ?, ?, ?, ?>> CommonTableExpression<R> asMaterialized(ResultQuery<R> resultQuery);

    @Support
    @NotNull
    <R extends Record5<?, ?, ?, ?, ?>> CommonTableExpression<R> asNotMaterialized(ResultQuery<R> resultQuery);
}
