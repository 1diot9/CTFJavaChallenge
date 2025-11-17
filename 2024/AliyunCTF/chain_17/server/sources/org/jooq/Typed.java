package org.jooq;

import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;

/* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/Typed.class */
public interface Typed<T> extends QueryPart {
    @NotNull
    ContextConverter<?, T> getConverter();

    @NotNull
    Binding<?, T> getBinding();

    @NotNull
    Class<T> getType();

    @NotNull
    DataType<T> getDataType();

    @NotNull
    DataType<T> getDataType(Configuration configuration);

    @ApiStatus.Experimental
    @NotNull
    DataType<T> $dataType();
}
