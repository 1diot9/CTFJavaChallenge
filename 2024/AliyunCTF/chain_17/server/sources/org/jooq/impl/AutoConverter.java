package org.jooq.impl;

import org.jooq.ContextConverter;
import org.jooq.ConverterContext;

/* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/impl/AutoConverter.class */
public class AutoConverter<T, U> extends AbstractContextConverter<T, U> {
    public AutoConverter(Class<T> fromType, Class<U> toType) {
        super(fromType, toType);
    }

    @Override // org.jooq.ContextConverter
    public U from(T t, ConverterContext converterContext) {
        return (U) ContextConverter.scoped(converterContext.configuration().converterProvider().provide(fromType(), toType())).from(t, converterContext);
    }

    @Override // org.jooq.ContextConverter
    public T to(U u, ConverterContext converterContext) {
        return (T) ContextConverter.scoped(converterContext.configuration().converterProvider().provide(fromType(), toType())).to(u, converterContext);
    }

    @Override // org.jooq.impl.AbstractContextConverter, org.jooq.impl.AbstractConverter
    public String toString() {
        return "AutoConverter [ " + fromType().getName() + " -> " + toType().getName() + " ]";
    }
}
