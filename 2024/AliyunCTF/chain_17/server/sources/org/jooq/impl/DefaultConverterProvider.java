package org.jooq.impl;

import java.io.File;
import java.io.Serializable;
import java.net.URI;
import java.net.URL;
import java.nio.ByteBuffer;
import java.sql.Struct;
import java.time.temporal.Temporal;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.Optional;
import java.util.UUID;
import org.jetbrains.annotations.Nullable;
import org.jooq.Converter;
import org.jooq.ConverterProvider;
import org.jooq.EnumType;
import org.jooq.JSON;
import org.jooq.JSONB;
import org.jooq.QualifiedRecord;
import org.jooq.Record;
import org.jooq.XML;
import org.jooq.exception.DataTypeException;
import org.jooq.tools.reflect.Reflect;

/* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/impl/DefaultConverterProvider.class */
public final class DefaultConverterProvider implements ConverterProvider, Serializable {
    @Override // org.jooq.ConverterProvider
    @Nullable
    public final <T, U> Converter<T, U> provide(Class<T> tType, Class<U> uType) {
        Class<?> wrapper = Reflect.wrapper(tType);
        Class<?> wrapper2 = Reflect.wrapper(uType);
        if (wrapper == wrapper2 || wrapper2.isAssignableFrom(wrapper) || ((isCollection(wrapper) && isCollection(wrapper2)) || wrapper == Optional.class || wrapper2 == Optional.class || wrapper2 == String.class || wrapper2 == byte[].class || wrapper2 == ByteBuffer.class || Number.class.isAssignableFrom(wrapper2) || Boolean.class.isAssignableFrom(wrapper2) || Character.class.isAssignableFrom(wrapper2) || ((wrapper2 == URI.class && wrapper == String.class) || ((wrapper2 == URL.class && wrapper == String.class) || ((wrapper2 == File.class && wrapper == String.class) || ((isDate(wrapper) && isDate(wrapper2)) || ((isEnum(wrapper) && isEnum(wrapper2)) || ((isUUID(wrapper) && isUUID(wrapper2)) || isJSON(wrapper) || isXML(wrapper) || Record.class.isAssignableFrom(wrapper) || ((Struct.class.isAssignableFrom(wrapper) && QualifiedRecord.class.isAssignableFrom(wrapper2)) || Tools.findAny(wrapper2.getDeclaredConstructors(), c -> {
            Class<?>[] types = c.getParameterTypes();
            return (types.length != 1 || types[0] == wrapper2 || provide(tType, types[0]) == null) ? false : true;
        }) != null))))))))) {
            return Converter.of(tType, uType, t -> {
                return Convert.convert(t, uType);
            }, u -> {
                return Convert.convert(u, tType);
            });
        }
        if (wrapper.isAssignableFrom(wrapper2)) {
            return Converter.ofNullable(wrapper, wrapper2, t2 -> {
                if (wrapper2.isInstance(t2)) {
                    return wrapper2.cast(t2);
                }
                throw new DataTypeException("Cannot cast from " + String.valueOf(wrapper) + " (instance type: " + String.valueOf(t2.getClass()) + " to " + String.valueOf(wrapper));
            }, u2 -> {
                return wrapper.cast(u2);
            });
        }
        return null;
    }

    private final boolean isJSON(Class<?> type) {
        return type == JSON.class || type == JSONB.class;
    }

    private final boolean isXML(Class<?> type) {
        return type == XML.class;
    }

    private final boolean isUUID(Class<?> type) {
        return type == String.class || type == byte[].class || type == UUID.class;
    }

    private final boolean isEnum(Class<?> type) {
        return Enum.class.isAssignableFrom(type) || type == String.class || EnumType.class.isAssignableFrom(type);
    }

    private final boolean isDate(Class<?> type) {
        return Date.class.isAssignableFrom(type) || Calendar.class.isAssignableFrom(type) || Temporal.class.isAssignableFrom(type) || type == Long.class || type == String.class;
    }

    private final boolean isCollection(Class<?> type) {
        return type.isArray() || Collection.class.isAssignableFrom(type) || type == java.sql.Array.class;
    }
}
