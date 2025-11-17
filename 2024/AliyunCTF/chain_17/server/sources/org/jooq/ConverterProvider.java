package org.jooq;

import org.jetbrains.annotations.Nullable;

@FunctionalInterface
/* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/ConverterProvider.class */
public interface ConverterProvider {
    @Nullable
    <T, U> Converter<T, U> provide(Class<T> cls, Class<U> cls2);
}
