package org.jooq;

import org.jetbrains.annotations.NotNull;

/* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/BindingSQLContext.class */
public interface BindingSQLContext<U> extends BindingScope {
    @NotNull
    RenderContext render();

    U value();

    @NotNull
    String variable();

    @NotNull
    <T> BindingSQLContext<T> convert(Converter<? extends T, ? super U> converter);
}
