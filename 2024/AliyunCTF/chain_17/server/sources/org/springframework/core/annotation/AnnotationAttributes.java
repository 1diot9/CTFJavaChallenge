package org.springframework.core.annotation;

import java.lang.annotation.Annotation;
import java.lang.reflect.Array;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.function.Supplier;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

/* loaded from: server.jar:BOOT-INF/lib/spring-core-6.1.3.jar:org/springframework/core/annotation/AnnotationAttributes.class */
public class AnnotationAttributes extends LinkedHashMap<String, Object> {
    private static final String UNKNOWN = "unknown";

    @Nullable
    private final Class<? extends Annotation> annotationType;
    final String displayName;
    final boolean validated;

    public AnnotationAttributes() {
        this.annotationType = null;
        this.displayName = UNKNOWN;
        this.validated = false;
    }

    public AnnotationAttributes(int initialCapacity) {
        super(initialCapacity);
        this.annotationType = null;
        this.displayName = UNKNOWN;
        this.validated = false;
    }

    public AnnotationAttributes(Map<String, Object> map) {
        super(map);
        this.annotationType = null;
        this.displayName = UNKNOWN;
        this.validated = false;
    }

    public AnnotationAttributes(AnnotationAttributes other) {
        super(other);
        this.annotationType = other.annotationType;
        this.displayName = other.displayName;
        this.validated = other.validated;
    }

    public AnnotationAttributes(Class<? extends Annotation> annotationType) {
        this(annotationType, false);
    }

    public AnnotationAttributes(String annotationType, @Nullable ClassLoader classLoader) {
        this(getAnnotationType(annotationType, classLoader), false);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public AnnotationAttributes(Class<? extends Annotation> annotationType, boolean validated) {
        Assert.notNull(annotationType, "'annotationType' must not be null");
        this.annotationType = annotationType;
        this.displayName = annotationType.getName();
        this.validated = validated;
    }

    @Nullable
    private static Class<? extends Annotation> getAnnotationType(String annotationType, @Nullable ClassLoader classLoader) {
        if (classLoader != null) {
            try {
                return classLoader.loadClass(annotationType);
            } catch (ClassNotFoundException e) {
                return null;
            }
        }
        return null;
    }

    @Nullable
    public Class<? extends Annotation> annotationType() {
        return this.annotationType;
    }

    public String getString(String attributeName) {
        return (String) getRequiredAttribute(attributeName, String.class);
    }

    public String[] getStringArray(String attributeName) {
        return (String[]) getRequiredAttribute(attributeName, String[].class);
    }

    public boolean getBoolean(String attributeName) {
        return ((Boolean) getRequiredAttribute(attributeName, Boolean.class)).booleanValue();
    }

    public <N extends Number> N getNumber(String attributeName) {
        return (N) getRequiredAttribute(attributeName, Number.class);
    }

    public <E extends Enum<?>> E getEnum(String attributeName) {
        return (E) getRequiredAttribute(attributeName, Enum.class);
    }

    public <T> Class<? extends T> getClass(String attributeName) {
        return (Class) getRequiredAttribute(attributeName, Class.class);
    }

    public Class<?>[] getClassArray(String attributeName) {
        return (Class[]) getRequiredAttribute(attributeName, Class[].class);
    }

    public AnnotationAttributes getAnnotation(String attributeName) {
        return (AnnotationAttributes) getRequiredAttribute(attributeName, AnnotationAttributes.class);
    }

    public <A extends Annotation> A getAnnotation(String attributeName, Class<A> annotationType) {
        return (A) getRequiredAttribute(attributeName, annotationType);
    }

    public AnnotationAttributes[] getAnnotationArray(String attributeName) {
        return (AnnotationAttributes[]) getRequiredAttribute(attributeName, AnnotationAttributes[].class);
    }

    public <A extends Annotation> A[] getAnnotationArray(String str, Class<A> cls) {
        return (A[]) ((Annotation[]) getRequiredAttribute(str, cls.arrayType()));
    }

    private <T> T getRequiredAttribute(String str, Class<T> cls) {
        Assert.hasText(str, "'attributeName' must not be null or empty");
        Object obj = get(str);
        assertAttributePresence(str, obj);
        assertNotException(str, obj);
        if (!cls.isInstance(obj) && cls.isArray() && cls.componentType().isInstance(obj)) {
            Object newInstance = Array.newInstance(cls.componentType(), 1);
            Array.set(newInstance, 0, obj);
            obj = newInstance;
        }
        assertAttributeType(str, obj, cls);
        return (T) obj;
    }

    private void assertAttributePresence(String attributeName, Object attributeValue) {
        Assert.notNull(attributeValue, (Supplier<String>) () -> {
            return String.format("Attribute '%s' not found in attributes for annotation [%s]", attributeName, this.displayName);
        });
    }

    private void assertNotException(String attributeName, Object attributeValue) {
        if (attributeValue instanceof Throwable) {
            Throwable throwable = (Throwable) attributeValue;
            throw new IllegalArgumentException(String.format("Attribute '%s' for annotation [%s] was not resolvable due to exception [%s]", attributeName, this.displayName, attributeValue), throwable);
        }
    }

    private void assertAttributeType(String attributeName, Object attributeValue, Class<?> expectedType) {
        if (!expectedType.isInstance(attributeValue)) {
            throw new IllegalArgumentException(String.format("Attribute '%s' is of type %s, but %s was expected in attributes for annotation [%s]", attributeName, attributeValue.getClass().getSimpleName(), expectedType.getSimpleName(), this.displayName));
        }
    }

    @Override // java.util.AbstractMap
    public String toString() {
        Iterator<Map.Entry<String, Object>> entries = entrySet().iterator();
        StringBuilder sb = new StringBuilder("{");
        while (entries.hasNext()) {
            Map.Entry<String, Object> entry = entries.next();
            sb.append(entry.getKey());
            sb.append('=');
            sb.append(valueToString(entry.getValue()));
            if (entries.hasNext()) {
                sb.append(", ");
            }
        }
        sb.append('}');
        return sb.toString();
    }

    private String valueToString(Object value) {
        if (value == this) {
            return "(this Map)";
        }
        if (value instanceof Object[]) {
            Object[] objects = (Object[]) value;
            return "[" + StringUtils.arrayToDelimitedString(objects, ", ") + "]";
        }
        return String.valueOf(value);
    }

    @Nullable
    public static AnnotationAttributes fromMap(@Nullable Map<String, Object> map) {
        if (map == null) {
            return null;
        }
        if (map instanceof AnnotationAttributes) {
            AnnotationAttributes annotationAttributes = (AnnotationAttributes) map;
            return annotationAttributes;
        }
        return new AnnotationAttributes(map);
    }
}
