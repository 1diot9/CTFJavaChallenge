package org.jooq.impl;

import org.jooq.ContextConverter;
import org.jooq.ConverterContext;

/* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/impl/DelegatingConverter.class */
public class DelegatingConverter<T, U> extends AbstractContextConverter<T, U> {
    private final ContextConverter<T, U> delegate;

    public DelegatingConverter(ContextConverter<T, U> delegate) {
        super(delegate.fromType(), delegate.toType());
        this.delegate = delegate;
    }

    @Override // org.jooq.ContextConverter
    public final U from(T t, ConverterContext scope) {
        return this.delegate.from(t, scope);
    }

    @Override // org.jooq.Converter
    public final U from(T t) {
        return this.delegate.from(t);
    }

    @Override // org.jooq.ContextConverter
    public final T to(U u, ConverterContext scope) {
        return this.delegate.to(u, scope);
    }

    @Override // org.jooq.Converter
    public final T to(U u) {
        return this.delegate.to(u);
    }

    @Override // org.jooq.impl.AbstractContextConverter, org.jooq.impl.AbstractConverter
    public String toString() {
        return "Converter [ " + fromType().getName() + " -> " + toType().getName() + " ]";
    }
}
