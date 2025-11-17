package org.jooq;

import java.lang.reflect.Array;
import org.jooq.impl.AbstractContextConverter;

/* JADX INFO: Access modifiers changed from: package-private */
/* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/ArrayComponentConverter.class */
public final class ArrayComponentConverter<T, U> extends AbstractContextConverter<T, U> {
    final ContextConverter<T[], U[]> converter;

    public ArrayComponentConverter(ContextConverter<T[], U[]> converter) {
        super(converter.fromType().getComponentType(), converter.toType().getComponentType());
        this.converter = converter;
    }

    @Override // org.jooq.ContextConverter
    public final U from(T t, ConverterContext converterContext) {
        if (t == null) {
            return null;
        }
        Object[] objArr = (Object[]) Array.newInstance((Class<?>) fromType(), 1);
        objArr[0] = t;
        return (U) ((Object[]) this.converter.from(objArr, converterContext))[0];
    }

    @Override // org.jooq.ContextConverter
    public final T to(U u, ConverterContext converterContext) {
        if (u == null) {
            return null;
        }
        Object[] objArr = (Object[]) Array.newInstance((Class<?>) fromType(), 1);
        objArr[0] = u;
        return (T) ((Object[]) this.converter.to(objArr, converterContext))[0];
    }
}
