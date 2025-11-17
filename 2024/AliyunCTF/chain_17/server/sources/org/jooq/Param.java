package org.jooq;

import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jooq.conf.ParamType;
import org.jooq.exception.DataTypeException;

/* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/Param.class */
public interface Param<T> extends ParamOrVariable<T> {
    @Nullable
    String getParamName();

    @Nullable
    T getValue();

    @Deprecated(forRemoval = true, since = "3.8")
    void setValue(T t);

    @Deprecated(forRemoval = true, since = "3.8")
    void setConverted(Object obj) throws DataTypeException;

    @Deprecated(forRemoval = true, since = "3.8")
    void setInline(boolean z);

    boolean isInline();

    @NotNull
    ParamType getParamType();

    @NotNull
    ParamMode getParamMode();

    @ApiStatus.Experimental
    T $value();

    @ApiStatus.Experimental
    @NotNull
    Param<T> $value(T t);

    @ApiStatus.Experimental
    boolean $inline();

    @ApiStatus.Experimental
    @NotNull
    Param<T> $inline(boolean z);
}
