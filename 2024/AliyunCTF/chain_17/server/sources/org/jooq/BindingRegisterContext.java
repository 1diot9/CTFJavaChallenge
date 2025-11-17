package org.jooq;

import java.sql.CallableStatement;
import org.jetbrains.annotations.NotNull;

/* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/BindingRegisterContext.class */
public interface BindingRegisterContext<U> extends ExecuteScope, BindingScope {
    @NotNull
    CallableStatement statement();

    int index();

    @NotNull
    <T> BindingRegisterContext<T> convert(Converter<? super T, ? extends U> converter);
}
