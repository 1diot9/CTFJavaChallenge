package org.jooq;

import java.util.List;
import org.jetbrains.annotations.Blocking;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jooq.exception.DataAccessException;

/* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/Routine.class */
public interface Routine<T> extends Qualified, Attachable {
    @Nullable
    Parameter<T> getReturnParameter();

    @NotNull
    List<Parameter<?>> getOutParameters();

    @NotNull
    List<Parameter<?>> getInParameters();

    @NotNull
    List<Parameter<?>> getParameters();

    @Blocking
    int execute(Configuration configuration) throws DataAccessException;

    @Blocking
    int execute() throws DataAccessException;

    <Z> void setValue(Parameter<Z> parameter, Z z);

    <Z> void set(Parameter<Z> parameter, Z z);

    <Z> Z getValue(Parameter<Z> parameter);

    <Z> Z getInValue(Parameter<Z> parameter);

    <Z> Z get(Parameter<Z> parameter);

    @Nullable
    T getReturnValue();

    @NotNull
    Results getResults();
}
