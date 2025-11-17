package org.jooq;

import java.sql.PreparedStatement;
import org.jetbrains.annotations.NotNull;

/* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/BindingSetStatementContext.class */
public interface BindingSetStatementContext<U> extends ResourceManagingScope, ExecuteScope, BindingScope {
    @NotNull
    PreparedStatement statement();

    int index();

    U value();

    @NotNull
    <T> BindingSetStatementContext<T> convert(Converter<? extends T, ? super U> converter);
}
