package org.jooq;

import java.util.function.Function;
import org.jetbrains.annotations.NotNull;

/* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/SelectField.class */
public interface SelectField<T> extends SelectFieldOrAsterisk, Named, Typed<T> {
    @Support
    @NotNull
    SelectField<T> as(String str);

    @Support
    @NotNull
    SelectField<T> as(Name name);

    @Support
    @NotNull
    SelectField<T> as(Field<?> field);

    @NotNull
    <U> SelectField<U> convert(Binding<T, U> binding);

    @NotNull
    <U> SelectField<U> convert(Converter<T, U> converter);

    @NotNull
    <U> SelectField<U> convert(Class<U> cls, Function<? super T, ? extends U> function, Function<? super U, ? extends T> function2);

    @NotNull
    <U> SelectField<U> convertFrom(Class<U> cls, Function<? super T, ? extends U> function);

    @NotNull
    <U> SelectField<U> convertFrom(Function<? super T, ? extends U> function);

    @NotNull
    <U> SelectField<U> convertTo(Class<U> cls, Function<? super U, ? extends T> function);

    @NotNull
    <U> SelectField<U> convertTo(Function<? super U, ? extends T> function);
}
