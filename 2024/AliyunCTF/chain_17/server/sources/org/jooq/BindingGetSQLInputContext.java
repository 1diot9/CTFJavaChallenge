package org.jooq;

import java.sql.SQLInput;
import org.jetbrains.annotations.NotNull;

/* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/BindingGetSQLInputContext.class */
public interface BindingGetSQLInputContext<U> extends ExecuteScope, BindingScope {
    @NotNull
    SQLInput input();

    void value(U u);

    @NotNull
    <T> BindingGetSQLInputContext<T> convert(Converter<? super T, ? extends U> converter);
}
