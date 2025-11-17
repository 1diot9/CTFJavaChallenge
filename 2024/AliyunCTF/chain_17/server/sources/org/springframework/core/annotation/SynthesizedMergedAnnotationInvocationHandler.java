package org.springframework.core.annotation;

import java.lang.annotation.Annotation;
import java.lang.reflect.Array;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.concurrent.ConcurrentHashMap;
import org.springframework.asm.Opcodes;
import org.springframework.jmx.export.naming.IdentityNamingStrategy;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;
import org.springframework.util.ClassUtils;
import org.springframework.util.ObjectUtils;
import org.springframework.util.ReflectionUtils;

/* JADX INFO: Access modifiers changed from: package-private */
/* loaded from: server.jar:BOOT-INF/lib/spring-core-6.1.3.jar:org/springframework/core/annotation/SynthesizedMergedAnnotationInvocationHandler.class */
public final class SynthesizedMergedAnnotationInvocationHandler<A extends Annotation> implements InvocationHandler {
    private final MergedAnnotation<?> annotation;
    private final Class<A> type;
    private final AttributeMethods attributes;
    private final Map<String, Object> valueCache = new ConcurrentHashMap(8);

    @Nullable
    private volatile Integer hashCode;

    @Nullable
    private volatile String string;

    /* JADX WARN: Multi-variable type inference failed */
    private SynthesizedMergedAnnotationInvocationHandler(MergedAnnotation<A> annotation, Class<A> type) {
        Assert.notNull(annotation, "MergedAnnotation must not be null");
        Assert.notNull(type, "Type must not be null");
        Assert.isTrue(type.isAnnotation(), "Type must be an annotation");
        this.annotation = annotation;
        this.type = type;
        this.attributes = AttributeMethods.forAnnotationType(type);
    }

    @Override // java.lang.reflect.InvocationHandler
    public Object invoke(Object proxy, Method method, Object[] args) {
        if (this.attributes.indexOf(method.getName()) != -1) {
            return getAttributeValue(method);
        }
        if (method.getParameterCount() == 0) {
            String name = method.getName();
            boolean z = -1;
            switch (name.hashCode()) {
                case -1776922004:
                    if (name.equals("toString")) {
                        z = 2;
                        break;
                    }
                    break;
                case 147696667:
                    if (name.equals(IdentityNamingStrategy.HASH_CODE_KEY)) {
                        z = true;
                        break;
                    }
                    break;
                case 1444986633:
                    if (name.equals("annotationType")) {
                        z = false;
                        break;
                    }
                    break;
            }
            switch (z) {
                case false:
                    return this.type;
                case true:
                    return Integer.valueOf(annotationHashCode());
                case true:
                    return annotationToString();
            }
        }
        if (ReflectionUtils.isEqualsMethod(method)) {
            return Boolean.valueOf(annotationEquals(args[0]));
        }
        throw new AnnotationConfigurationException(String.format("Method [%s] is unsupported for synthesized annotation type [%s]", method, this.type));
    }

    private boolean annotationEquals(Object other) {
        if (this == other) {
            return true;
        }
        if (!this.type.isInstance(other)) {
            return false;
        }
        for (int i = 0; i < this.attributes.size(); i++) {
            Method attribute = this.attributes.get(i);
            Object thisValue = getAttributeValue(attribute);
            Object otherValue = AnnotationUtils.invokeAnnotationMethod(attribute, other);
            if (!ObjectUtils.nullSafeEquals(thisValue, otherValue)) {
                return false;
            }
        }
        return true;
    }

    private int annotationHashCode() {
        Integer hashCode = this.hashCode;
        if (hashCode == null) {
            hashCode = computeHashCode();
            this.hashCode = hashCode;
        }
        return hashCode.intValue();
    }

    private Integer computeHashCode() {
        int hashCode = 0;
        for (int i = 0; i < this.attributes.size(); i++) {
            Method attribute = this.attributes.get(i);
            Object value = getAttributeValue(attribute);
            hashCode += (Opcodes.LAND * attribute.getName().hashCode()) ^ ObjectUtils.nullSafeHashCode(value);
        }
        return Integer.valueOf(hashCode);
    }

    private String annotationToString() {
        String string = this.string;
        if (string == null) {
            StringBuilder builder = new StringBuilder("@").append(getName(this.type)).append('(');
            for (int i = 0; i < this.attributes.size(); i++) {
                Method attribute = this.attributes.get(i);
                if (i > 0) {
                    builder.append(", ");
                }
                builder.append(attribute.getName());
                builder.append('=');
                builder.append(toString(getAttributeValue(attribute)));
            }
            builder.append(')');
            string = builder.toString();
            this.string = string;
        }
        return string;
    }

    private String toString(Object value) {
        if (value instanceof String) {
            String str = (String) value;
            return "\"" + str + "\"";
        }
        if (value instanceof Character) {
            return "'" + value.toString() + "'";
        }
        if (value instanceof Byte) {
            return String.format("(byte) 0x%02X", value);
        }
        if (value instanceof Long) {
            Long longValue = (Long) value;
            return Long.toString(longValue.longValue()) + "L";
        }
        if (value instanceof Float) {
            Float floatValue = (Float) value;
            return Float.toString(floatValue.floatValue()) + "f";
        }
        if (value instanceof Double) {
            Double doubleValue = (Double) value;
            return Double.toString(doubleValue.doubleValue()) + "d";
        }
        if (value instanceof Enum) {
            Enum<?> e = (Enum) value;
            return e.name();
        }
        if (value instanceof Class) {
            Class<?> clazz = (Class) value;
            return getName(clazz) + ".class";
        }
        if (value.getClass().isArray()) {
            StringBuilder builder = new StringBuilder("{");
            for (int i = 0; i < Array.getLength(value); i++) {
                if (i > 0) {
                    builder.append(", ");
                }
                builder.append(toString(Array.get(value, i)));
            }
            builder.append('}');
            return builder.toString();
        }
        return String.valueOf(value);
    }

    private Object getAttributeValue(Method method) {
        Object value = this.valueCache.computeIfAbsent(method.getName(), attributeName -> {
            Class<?> type = ClassUtils.resolvePrimitiveIfNecessary(method.getReturnType());
            return this.annotation.getValue(attributeName, type).orElseThrow(() -> {
                return new NoSuchElementException("No value found for attribute named '" + attributeName + "' in merged annotation " + this.annotation.getType().getName());
            });
        });
        if (value.getClass().isArray() && Array.getLength(value) > 0) {
            value = cloneArray(value);
        }
        return value;
    }

    private Object cloneArray(Object array) {
        if (array instanceof boolean[]) {
            boolean[] booleans = (boolean[]) array;
            return booleans.clone();
        }
        if (array instanceof byte[]) {
            byte[] bytes = (byte[]) array;
            return bytes.clone();
        }
        if (array instanceof char[]) {
            char[] chars = (char[]) array;
            return chars.clone();
        }
        if (array instanceof double[]) {
            double[] doubles = (double[]) array;
            return doubles.clone();
        }
        if (array instanceof float[]) {
            float[] floats = (float[]) array;
            return floats.clone();
        }
        if (array instanceof int[]) {
            int[] ints = (int[]) array;
            return ints.clone();
        }
        if (array instanceof long[]) {
            long[] longs = (long[]) array;
            return longs.clone();
        }
        if (array instanceof short[]) {
            short[] shorts = (short[]) array;
            return shorts.clone();
        }
        return ((Object[]) array).clone();
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static <A extends Annotation> A createProxy(MergedAnnotation<A> annotation, Class<A> type) {
        ClassLoader classLoader = type.getClassLoader();
        Class[] clsArr = {type};
        SynthesizedMergedAnnotationInvocationHandler handler = new SynthesizedMergedAnnotationInvocationHandler(annotation, type);
        return (A) Proxy.newProxyInstance(classLoader, clsArr, handler);
    }

    private static String getName(Class<?> clazz) {
        String canonicalName = clazz.getCanonicalName();
        return canonicalName != null ? canonicalName : clazz.getName();
    }
}
