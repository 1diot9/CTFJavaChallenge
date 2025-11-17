package org.jooq;

import org.jetbrains.annotations.NotNull;

/* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/DerivedColumnList13.class */
public interface DerivedColumnList13 extends QueryPart {
    @Support
    @NotNull
    <R extends Record13<?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?>> CommonTableExpression<R> as(ResultQuery<R> resultQuery);

    @Support
    @NotNull
    <R extends Record13<?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?>> CommonTableExpression<R> asMaterialized(ResultQuery<R> resultQuery);

    @Support
    @NotNull
    <R extends Record13<?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?>> CommonTableExpression<R> asNotMaterialized(ResultQuery<R> resultQuery);
}
