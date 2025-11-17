package org.jooq;

import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jooq.Record;
import org.jooq.impl.QOM;

/* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/CommonTableExpression.class */
public interface CommonTableExpression<R extends Record> extends Table<R> {
    @ApiStatus.Experimental
    @NotNull
    DerivedColumnList $derivedColumnList();

    @ApiStatus.Experimental
    @NotNull
    ResultQuery<R> $query();

    @ApiStatus.Experimental
    @Nullable
    QOM.Materialized $materialized();
}
