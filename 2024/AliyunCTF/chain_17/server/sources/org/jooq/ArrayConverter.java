package org.jooq;

import org.jooq.impl.AbstractContextConverter;
import org.jooq.tools.Convert;

/* JADX INFO: Access modifiers changed from: package-private */
/* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/ArrayConverter.class */
public final class ArrayConverter<T, U> extends AbstractContextConverter<T[], U[]> {
    final ContextConverter<T, U> converter;
    final ContextConverter<U, T> inverse;

    public ArrayConverter(ContextConverter<T, U> converter) {
        super(org.jooq.impl.Internal.arrayType(converter.fromType()), org.jooq.impl.Internal.arrayType(converter.toType()));
        this.converter = converter;
        this.inverse = Converters.inverse((ContextConverter) converter);
    }

    @Override // org.jooq.ContextConverter
    public final U[] from(T[] tArr, ConverterContext converterContext) {
        return (U[]) Convert.convertArray(tArr, this.converter);
    }

    @Override // org.jooq.ContextConverter
    public final T[] to(U[] uArr, ConverterContext converterContext) {
        return (T[]) Convert.convertArray(uArr, this.inverse);
    }
}
