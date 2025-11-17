package org.jooq.impl;

import org.jooq.ContextConverter;

/* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/impl/AbstractContextConverter.class */
public abstract class AbstractContextConverter<T, U> extends AbstractConverter<T, U> implements ContextConverter<T, U> {
    public AbstractContextConverter(Class<T> fromType, Class<U> toType) {
        super(fromType, toType);
    }

    @Override // org.jooq.impl.AbstractConverter
    public String toString() {
        return "ContextConverter [ " + fromType().getName() + " -> " + toType().getName() + " ]";
    }
}
