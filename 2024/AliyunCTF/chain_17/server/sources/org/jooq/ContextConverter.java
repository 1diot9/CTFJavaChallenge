package org.jooq;

import java.util.function.BiFunction;
import org.jetbrains.annotations.NotNull;
import org.jooq.impl.AbstractContextConverter;

/* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/ContextConverter.class */
public interface ContextConverter<T, U> extends Converter<T, U> {
    U from(T t, ConverterContext converterContext);

    T to(U u, ConverterContext converterContext);

    @Override // org.jooq.Converter
    default T to(U userObject) {
        return to(userObject, org.jooq.impl.Internal.converterContext());
    }

    @Override // org.jooq.Converter
    default U from(T databaseObject) {
        return from(databaseObject, org.jooq.impl.Internal.converterContext());
    }

    @Override // org.jooq.Converter
    @NotNull
    default ContextConverter<U, T> inverse() {
        return Converters.inverse((ContextConverter) this);
    }

    @Override // org.jooq.Converter
    @NotNull
    default <X> ContextConverter<T, X> andThen(Converter<? super U, X> converter) {
        return Converters.of(this, converter);
    }

    @Override // org.jooq.Converter
    @NotNull
    default ContextConverter<T[], U[]> forArrays() {
        return Converters.forArrays((ContextConverter) this);
    }

    @NotNull
    static <T, U> ContextConverter<T, U> scoped(final Converter<T, U> converter) {
        if (converter instanceof ContextConverter) {
            ContextConverter<T, U> s = (ContextConverter) converter;
            return s;
        }
        return new AbstractContextConverter<T, U>(converter.fromType(), converter.toType()) { // from class: org.jooq.ContextConverter.1
            @Override // org.jooq.ContextConverter
            public U from(T t, ConverterContext converterContext) {
                return (U) converter.from(t);
            }

            @Override // org.jooq.ContextConverter
            public T to(U u, ConverterContext converterContext) {
                return (T) converter.to(u);
            }
        };
    }

    @NotNull
    static <T, U> ContextConverter<T, U> from(Class<T> fromType, Class<U> toType, BiFunction<? super T, ? super ConverterContext, ? extends U> from) {
        return of(fromType, toType, from, Converters.notImplementedBiFunction());
    }

    @NotNull
    static <T, U> ContextConverter<T, U> to(Class<T> fromType, Class<U> toType, BiFunction<? super U, ? super ConverterContext, ? extends T> to) {
        return of(fromType, toType, Converters.notImplementedBiFunction(), to);
    }

    @NotNull
    static <T, U> ContextConverter<T, U> of(Class<T> fromType, Class<U> toType, final BiFunction<? super T, ? super ConverterContext, ? extends U> from, final BiFunction<? super U, ? super ConverterContext, ? extends T> to) {
        return new AbstractContextConverter<T, U>(fromType, toType) { // from class: org.jooq.ContextConverter.2
            @Override // org.jooq.ContextConverter
            public final U from(T t, ConverterContext converterContext) {
                return (U) from.apply(t, converterContext);
            }

            @Override // org.jooq.ContextConverter
            public final T to(U u, ConverterContext converterContext) {
                return (T) to.apply(u, converterContext);
            }
        };
    }

    @NotNull
    static <T, U> ContextConverter<T, U> ofNullable(Class<T> fromType, Class<U> toType, BiFunction<? super T, ? super ConverterContext, ? extends U> from, BiFunction<? super U, ? super ConverterContext, ? extends T> to) {
        return of(fromType, toType, Converters.nullable(from), Converters.nullable(to));
    }

    @NotNull
    static <T, U> Converter<T, U> fromNullable(Class<T> fromType, Class<U> toType, BiFunction<? super T, ? super ConverterContext, ? extends U> from) {
        return of(fromType, toType, Converters.nullable(from), Converters.notImplementedBiFunction());
    }

    @NotNull
    static <T, U> Converter<T, U> toNullable(Class<T> fromType, Class<U> toType, BiFunction<? super U, ? super ConverterContext, ? extends T> to) {
        return of(fromType, toType, Converters.notImplementedBiFunction(), Converters.nullable(to));
    }
}
