package org.jooq;

import java.sql.SQLOutput;
import org.jetbrains.annotations.NotNull;

/* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/BindingSetSQLOutputContext.class */
public interface BindingSetSQLOutputContext<U> extends ResourceManagingScope, ExecuteScope, BindingScope {
    @NotNull
    SQLOutput output();

    U value();

    @NotNull
    <T> BindingSetSQLOutputContext<T> convert(Converter<? extends T, ? super U> converter);
}
