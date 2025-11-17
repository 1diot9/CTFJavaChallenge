package org.jooq.impl;

import org.jooq.Converter;

/* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/impl/AbstractConverter.class */
public abstract class AbstractConverter<T, U> implements Converter<T, U> {
    private final Class<T> fromType;
    private final Class<U> toType;

    public AbstractConverter(Class<T> fromType, Class<U> toType) {
        this.fromType = fromType;
        this.toType = toType;
    }

    @Override // org.jooq.Converter
    public final Class<T> fromType() {
        return this.fromType;
    }

    @Override // org.jooq.Converter
    public final Class<U> toType() {
        return this.toType;
    }

    public String toString() {
        return "Converter [ " + fromType().getName() + " -> " + toType().getName() + " ]";
    }
}
