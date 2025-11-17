package org.springframework.expression.common;

import org.springframework.core.convert.TypeDescriptor;
import org.springframework.expression.EvaluationContext;
import org.springframework.expression.EvaluationException;
import org.springframework.expression.TypeConverter;
import org.springframework.expression.TypedValue;
import org.springframework.lang.Nullable;
import org.springframework.util.ClassUtils;

/* loaded from: agent.jar:BOOT-INF/lib/spring-expression-6.1.3.jar:org/springframework/expression/common/ExpressionUtils.class */
public abstract class ExpressionUtils {
    @Nullable
    public static <T> T convertTypedValue(@Nullable EvaluationContext evaluationContext, TypedValue typedValue, @Nullable Class<T> cls) {
        T t = (T) typedValue.getValue();
        if (cls == null) {
            return t;
        }
        if (evaluationContext != null) {
            return (T) evaluationContext.getTypeConverter().convertValue(t, typedValue.getTypeDescriptor(), TypeDescriptor.valueOf(cls));
        }
        if (ClassUtils.isAssignableValue(cls, t)) {
            return t;
        }
        throw new EvaluationException("Cannot convert value '" + t + "' to type '" + cls.getName() + "'");
    }

    public static int toInt(TypeConverter typeConverter, TypedValue typedValue) {
        return ((Integer) convertValue(typeConverter, typedValue, Integer.class)).intValue();
    }

    public static boolean toBoolean(TypeConverter typeConverter, TypedValue typedValue) {
        return ((Boolean) convertValue(typeConverter, typedValue, Boolean.class)).booleanValue();
    }

    public static double toDouble(TypeConverter typeConverter, TypedValue typedValue) {
        return ((Double) convertValue(typeConverter, typedValue, Double.class)).doubleValue();
    }

    public static long toLong(TypeConverter typeConverter, TypedValue typedValue) {
        return ((Long) convertValue(typeConverter, typedValue, Long.class)).longValue();
    }

    public static char toChar(TypeConverter typeConverter, TypedValue typedValue) {
        return ((Character) convertValue(typeConverter, typedValue, Character.class)).charValue();
    }

    public static short toShort(TypeConverter typeConverter, TypedValue typedValue) {
        return ((Short) convertValue(typeConverter, typedValue, Short.class)).shortValue();
    }

    public static float toFloat(TypeConverter typeConverter, TypedValue typedValue) {
        return ((Float) convertValue(typeConverter, typedValue, Float.class)).floatValue();
    }

    public static byte toByte(TypeConverter typeConverter, TypedValue typedValue) {
        return ((Byte) convertValue(typeConverter, typedValue, Byte.class)).byteValue();
    }

    private static <T> T convertValue(TypeConverter typeConverter, TypedValue typedValue, Class<T> cls) {
        T t = (T) typeConverter.convertValue(typedValue.getValue(), typedValue.getTypeDescriptor(), TypeDescriptor.valueOf(cls));
        if (t == null) {
            throw new IllegalStateException("Null conversion result for value [" + typedValue.getValue() + "]");
        }
        return t;
    }
}
