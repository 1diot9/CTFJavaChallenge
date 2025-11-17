package org.jooq;

import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jooq.impl.QOM;

/* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/SortField.class */
public interface SortField<T> extends OrderField<T> {
    @NotNull
    String getName();

    @NotNull
    SortOrder getOrder();

    @Support
    @NotNull
    SortField<T> nullsFirst();

    @Support
    @NotNull
    SortField<T> nullsLast();

    @ApiStatus.Experimental
    @NotNull
    Field<T> $field();

    @ApiStatus.Experimental
    @NotNull
    <U> SortField<U> $field(Field<U> field);

    @ApiStatus.Experimental
    @NotNull
    SortOrder $sortOrder();

    @ApiStatus.Experimental
    @NotNull
    SortField<T> $sortOrder(SortOrder sortOrder);

    @ApiStatus.Experimental
    @Nullable
    QOM.NullOrdering $nullOrdering();

    @ApiStatus.Experimental
    @NotNull
    SortField<T> $nullOrdering(QOM.NullOrdering nullOrdering);
}
