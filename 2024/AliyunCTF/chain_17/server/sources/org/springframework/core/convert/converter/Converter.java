package org.springframework.core.convert.converter;

import org.springframework.lang.Nullable;
import org.springframework.util.Assert;

@FunctionalInterface
/* loaded from: server.jar:BOOT-INF/lib/spring-core-6.1.3.jar:org/springframework/core/convert/converter/Converter.class */
public interface Converter<S, T> {
    @Nullable
    T convert(S source);

    default <U> Converter<S, U> andThen(Converter<? super T, ? extends U> after) {
        Assert.notNull(after, "'after' Converter must not be null");
        return obj -> {
            T initialResult = convert(obj);
            if (initialResult != null) {
                return after.convert(initialResult);
            }
            return null;
        };
    }
}
