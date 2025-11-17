package org.jooq;

import java.sql.CallableStatement;
import org.jetbrains.annotations.NotNull;

/* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/BindingGetStatementContext.class */
public interface BindingGetStatementContext<U> extends ExecuteScope, BindingScope {
    @NotNull
    CallableStatement statement();

    int index();

    void value(U u);

    @NotNull
    <T> BindingGetStatementContext<T> convert(Converter<? super T, ? extends U> converter);
}
