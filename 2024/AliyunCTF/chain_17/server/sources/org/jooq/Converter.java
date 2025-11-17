package org.jooq;

import java.io.Serializable;
import java.util.function.Function;
import org.jetbrains.annotations.NotNull;
import org.jooq.impl.AbstractContextConverter;

/* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/Converter.class */
public interface Converter<T, U> extends Serializable {
    U from(T t);

    T to(U u);

    @NotNull
    Class<T> fromType();

    @NotNull
    Class<U> toType();

    @NotNull
    default Converter<U, T> inverse() {
        return Converters.inverse(this);
    }

    @NotNull
    default <X> Converter<T, X> andThen(Converter<? super U, X> converter) {
        return Converters.of(this, converter);
    }

    @NotNull
    default Converter<T[], U[]> forArrays() {
        return Converters.forArrays(this);
    }

    @NotNull
    static <T, U> Converter<T, U> of(Class<T> fromType, Class<U> toType, final Function<? super T, ? extends U> from, final Function<? super U, ? extends T> to) {
        return new AbstractContextConverter<T, U>(fromType, toType) { // from class: org.jooq.Converter.1
            @Override // org.jooq.ContextConverter
            public final U from(T t, ConverterContext converterContext) {
                return (U) from.apply(t);
            }

            @Override // org.jooq.ContextConverter
            public final T to(U u, ConverterContext converterContext) {
                return (T) to.apply(u);
            }
        };
    }

    @NotNull
    static <T, U> Converter<T, U> from(Class<T> fromType, Class<U> toType, Function<? super T, ? extends U> from) {
        return of(fromType, toType, from, Converters.notImplemented());
    }

    @NotNull
    static <T, U> Converter<T, U> to(Class<T> fromType, Class<U> toType, Function<? super U, ? extends T> to) {
        return of(fromType, toType, Converters.notImplemented(), to);
    }

    @NotNull
    static <T, U> Converter<T, U> ofNullable(Class<T> fromType, Class<U> toType, Function<? super T, ? extends U> from, Function<? super U, ? extends T> to) {
        return of(fromType, toType, Converters.nullable(from), Converters.nullable(to));
    }

    @NotNull
    static <T, U> Converter<T, U> fromNullable(Class<T> fromType, Class<U> toType, Function<? super T, ? extends U> from) {
        return of(fromType, toType, Converters.nullable(from), Converters.notImplemented());
    }

    @NotNull
    static <T, U> Converter<T, U> toNullable(Class<T> fromType, Class<U> toType, Function<? super U, ? extends T> to) {
        return of(fromType, toType, Converters.notImplemented(), Converters.nullable(to));
    }
}
