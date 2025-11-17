package org.jooq.tools;

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.jooq.Constants;
import org.jooq.Converter;
import org.jooq.Field;
import org.jooq.exception.DataTypeException;
import org.jooq.impl.Internal;
import org.springframework.beans.propertyeditors.CustomBooleanEditor;

@Deprecated(forRemoval = true, since = Constants.VERSION_3_15)
/* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/tools/Convert.class */
public final class Convert {
    public static final Set<String> TRUE_VALUES;
    public static final Set<String> FALSE_VALUES;

    static {
        Set<String> trueValues = new HashSet<>();
        Set<String> falseValues = new HashSet<>();
        trueValues.add(CustomBooleanEditor.VALUE_1);
        trueValues.add("1.0");
        trueValues.add("y");
        trueValues.add("Y");
        trueValues.add(CustomBooleanEditor.VALUE_YES);
        trueValues.add("YES");
        trueValues.add("true");
        trueValues.add("TRUE");
        trueValues.add("t");
        trueValues.add("T");
        trueValues.add(CustomBooleanEditor.VALUE_ON);
        trueValues.add("ON");
        trueValues.add("enabled");
        trueValues.add("ENABLED");
        falseValues.add(CustomBooleanEditor.VALUE_0);
        falseValues.add("0.0");
        falseValues.add("n");
        falseValues.add("N");
        falseValues.add("no");
        falseValues.add("NO");
        falseValues.add("false");
        falseValues.add("FALSE");
        falseValues.add("f");
        falseValues.add("F");
        falseValues.add(CustomBooleanEditor.VALUE_OFF);
        falseValues.add("OFF");
        falseValues.add("disabled");
        falseValues.add("DISABLED");
        TRUE_VALUES = Collections.unmodifiableSet(trueValues);
        FALSE_VALUES = Collections.unmodifiableSet(falseValues);
    }

    public static final Object[] convert(Object[] values, Field<?>[] fields) {
        return Internal.convert(values, fields);
    }

    public static final Object[] convert(Object[] values, Class<?>[] types) {
        return Internal.convert(values, types);
    }

    public static final <U> U[] convertArray(Object[] objArr, Converter<?, ? extends U> converter) throws DataTypeException {
        return (U[]) Internal.convertArray(objArr, converter);
    }

    public static final Object[] convertArray(Object[] from, Class<?> toClass) throws DataTypeException {
        return Internal.convertArray(from, toClass);
    }

    public static final <U> U[] convertCollection(Collection collection, Class<? extends U[]> cls) {
        return (U[]) Internal.convertCollection(collection, cls);
    }

    public static final <U> U convert(Object obj, Converter<?, ? extends U> converter) throws DataTypeException {
        return (U) Internal.convert(obj, converter);
    }

    public static final <T> T convert(Object obj, Class<? extends T> cls) throws DataTypeException {
        return (T) Internal.convert(obj, cls);
    }

    public static final <T> List<T> convert(Collection<?> collection, Class<? extends T> type) throws DataTypeException {
        return Internal.convert(collection, (Class) type);
    }

    public static final <U> List<U> convert(Collection<?> collection, Converter<?, ? extends U> converter) throws DataTypeException {
        return Internal.convert(collection, (Converter) converter);
    }
}
