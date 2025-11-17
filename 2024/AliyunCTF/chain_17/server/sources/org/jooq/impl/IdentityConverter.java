package org.jooq.impl;

import org.jooq.ContextConverter;
import org.jooq.ConverterContext;

/* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/impl/IdentityConverter.class */
public final class IdentityConverter<T> implements ContextConverter<T, T> {
    private final Class<T> type;

    public IdentityConverter(Class<T> type) {
        this.type = type;
    }

    @Override // org.jooq.ContextConverter
    public final T from(T t, ConverterContext scope) {
        return t;
    }

    @Override // org.jooq.ContextConverter
    public final T to(T t, ConverterContext scope) {
        return t;
    }

    @Override // org.jooq.Converter
    public final Class<T> fromType() {
        return this.type;
    }

    @Override // org.jooq.Converter
    public final Class<T> toType() {
        return this.type;
    }

    public String toString() {
        return "IdentityConverter [ " + fromType().getName() + " ]";
    }
}
