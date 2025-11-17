package org.jooq;

import java.sql.ResultSet;
import org.jetbrains.annotations.NotNull;

/* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/BindingGetResultSetContext.class */
public interface BindingGetResultSetContext<U> extends ExecuteScope, BindingScope {
    @NotNull
    ResultSet resultSet();

    int index();

    @NotNull
    Field<U> field();

    void value(U u);

    @NotNull
    <T> BindingGetResultSetContext<T> convert(Converter<? super T, ? extends U> converter);
}
