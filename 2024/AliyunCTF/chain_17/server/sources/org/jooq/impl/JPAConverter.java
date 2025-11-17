package org.jooq.impl;

import jakarta.persistence.AttributeConverter;
import java.lang.reflect.Method;
import org.jooq.ConverterContext;
import org.jooq.exception.MappingException;
import org.jooq.tools.JooqLogger;
import org.jooq.tools.reflect.Reflect;

/* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/impl/JPAConverter.class */
public final class JPAConverter<T, U> extends AbstractContextConverter<T, U> {
    private static final JooqLogger log = JooqLogger.getLogger((Class<?>) JPAConverter.class);
    private final AttributeConverter<U, T> delegate;

    public JPAConverter(Class<? extends AttributeConverter<U, T>> klass) {
        super(fromType(klass), toType(klass));
        try {
            this.delegate = (AttributeConverter) Reflect.on((Class<?>) klass).create().get();
        } catch (Exception e) {
            throw new MappingException("Cannot instanciate AttributeConverter", e);
        }
    }

    private static final <T> Class<T> fromType(Class<? extends AttributeConverter<?, T>> cls) {
        Method method = null;
        for (Method method2 : cls.getDeclaredMethods()) {
            if ("convertToDatabaseColumn".equals(method2.getName())) {
                if (method2.getReturnType() == Object.class) {
                    method = method2;
                } else {
                    return (Class<T>) method2.getReturnType();
                }
            }
        }
        if (method != null) {
            log.warn("Couldn't find bridge method to detect generic type bound for " + cls.getName() + "::convertToDatabaseColumn");
            return (Class<T>) method.getReturnType();
        }
        throw new IllegalArgumentException();
    }

    private static final <U> Class<U> toType(Class<? extends AttributeConverter<U, ?>> cls) {
        Method method = null;
        for (Method method2 : cls.getDeclaredMethods()) {
            if ("convertToEntityAttribute".equals(method2.getName())) {
                if (method2.getReturnType() == Object.class) {
                    method = method2;
                } else {
                    return (Class<U>) method2.getReturnType();
                }
            }
        }
        if (method != null) {
            log.warn("Couldn't find bridge method to detect generic type bound for " + cls.getName() + "::convertToEntityAttribute");
            return (Class<U>) method.getReturnType();
        }
        throw new IllegalArgumentException();
    }

    @Override // org.jooq.ContextConverter
    public final U from(T t, ConverterContext converterContext) {
        return (U) this.delegate.convertToEntityAttribute(t);
    }

    @Override // org.jooq.ContextConverter
    public final T to(U u, ConverterContext converterContext) {
        return (T) this.delegate.convertToDatabaseColumn(u);
    }
}
