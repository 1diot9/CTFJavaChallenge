package org.springframework.core.convert;

import ch.qos.logback.core.joran.util.beans.BeanUtil;
import java.lang.annotation.Annotation;
import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;
import org.springframework.core.MethodParameter;
import org.springframework.lang.Nullable;
import org.springframework.util.ConcurrentReferenceHashMap;
import org.springframework.util.ObjectUtils;
import org.springframework.util.ReflectionUtils;
import org.springframework.util.StringUtils;

/* loaded from: server.jar:BOOT-INF/lib/spring-core-6.1.3.jar:org/springframework/core/convert/Property.class */
public final class Property {
    private static final Map<Property, Annotation[]> annotationCache = new ConcurrentReferenceHashMap();
    private final Class<?> objectType;

    @Nullable
    private final Method readMethod;

    @Nullable
    private final Method writeMethod;
    private final String name;
    private final MethodParameter methodParameter;

    @Nullable
    private Annotation[] annotations;

    public Property(Class<?> objectType, @Nullable Method readMethod, @Nullable Method writeMethod) {
        this(objectType, readMethod, writeMethod, null);
    }

    public Property(Class<?> objectType, @Nullable Method readMethod, @Nullable Method writeMethod, @Nullable String name) {
        this.objectType = objectType;
        this.readMethod = readMethod;
        this.writeMethod = writeMethod;
        this.methodParameter = resolveMethodParameter();
        this.name = name != null ? name : resolveName();
    }

    public Class<?> getObjectType() {
        return this.objectType;
    }

    public String getName() {
        return this.name;
    }

    public Class<?> getType() {
        return this.methodParameter.getParameterType();
    }

    @Nullable
    public Method getReadMethod() {
        return this.readMethod;
    }

    @Nullable
    public Method getWriteMethod() {
        return this.writeMethod;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public MethodParameter getMethodParameter() {
        return this.methodParameter;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public Annotation[] getAnnotations() {
        if (this.annotations == null) {
            this.annotations = resolveAnnotations();
        }
        return this.annotations;
    }

    private String resolveName() {
        int index;
        if (this.readMethod != null) {
            int index2 = this.readMethod.getName().indexOf(BeanUtil.PREFIX_GETTER_GET);
            if (index2 != -1) {
                index = index2 + 3;
            } else {
                int index3 = this.readMethod.getName().indexOf(BeanUtil.PREFIX_GETTER_IS);
                if (index3 != -1) {
                    index = index3 + 2;
                } else {
                    index = 0;
                }
            }
            return StringUtils.uncapitalize(this.readMethod.getName().substring(index));
        }
        if (this.writeMethod != null) {
            int index4 = this.writeMethod.getName().indexOf("set");
            if (index4 == -1) {
                throw new IllegalArgumentException("Not a setter method");
            }
            return StringUtils.uncapitalize(this.writeMethod.getName().substring(index4 + 3));
        }
        throw new IllegalStateException("Property is neither readable nor writeable");
    }

    private MethodParameter resolveMethodParameter() {
        MethodParameter read = resolveReadMethodParameter();
        MethodParameter write = resolveWriteMethodParameter();
        if (write == null) {
            if (read == null) {
                throw new IllegalStateException("Property is neither readable nor writeable");
            }
            return read;
        }
        if (read != null) {
            Class<?> readType = read.getParameterType();
            Class<?> writeType = write.getParameterType();
            if (!writeType.equals(readType) && writeType.isAssignableFrom(readType)) {
                return read;
            }
        }
        return write;
    }

    @Nullable
    private MethodParameter resolveReadMethodParameter() {
        if (getReadMethod() == null) {
            return null;
        }
        return new MethodParameter(getReadMethod(), -1).withContainingClass(getObjectType());
    }

    @Nullable
    private MethodParameter resolveWriteMethodParameter() {
        if (getWriteMethod() == null) {
            return null;
        }
        return new MethodParameter(getWriteMethod(), 0).withContainingClass(getObjectType());
    }

    private Annotation[] resolveAnnotations() {
        Annotation[] annotations = annotationCache.get(this);
        if (annotations == null) {
            Map<Class<? extends Annotation>, Annotation> annotationMap = new LinkedHashMap<>();
            addAnnotationsToMap(annotationMap, getReadMethod());
            addAnnotationsToMap(annotationMap, getWriteMethod());
            addAnnotationsToMap(annotationMap, getField());
            annotations = (Annotation[]) annotationMap.values().toArray(new Annotation[0]);
            annotationCache.put(this, annotations);
        }
        return annotations;
    }

    private void addAnnotationsToMap(Map<Class<? extends Annotation>, Annotation> annotationMap, @Nullable AnnotatedElement object) {
        if (object != null) {
            for (Annotation annotation : object.getAnnotations()) {
                annotationMap.put(annotation.annotationType(), annotation);
            }
        }
    }

    @Nullable
    private Field getField() {
        String name = getName();
        if (!StringUtils.hasLength(name)) {
            return null;
        }
        Field field = null;
        Class<?> declaringClass = declaringClass();
        if (declaringClass != null) {
            field = ReflectionUtils.findField(declaringClass, name);
            if (field == null) {
                field = ReflectionUtils.findField(declaringClass, StringUtils.uncapitalize(name));
                if (field == null) {
                    field = ReflectionUtils.findField(declaringClass, StringUtils.capitalize(name));
                }
            }
        }
        return field;
    }

    @Nullable
    private Class<?> declaringClass() {
        if (getReadMethod() != null) {
            return getReadMethod().getDeclaringClass();
        }
        if (getWriteMethod() != null) {
            return getWriteMethod().getDeclaringClass();
        }
        return null;
    }

    public boolean equals(@Nullable Object other) {
        if (this != other) {
            if (other instanceof Property) {
                Property that = (Property) other;
                if (!ObjectUtils.nullSafeEquals(this.objectType, that.objectType) || !ObjectUtils.nullSafeEquals(this.name, that.name) || !ObjectUtils.nullSafeEquals(this.readMethod, that.readMethod) || !ObjectUtils.nullSafeEquals(this.writeMethod, that.writeMethod)) {
                }
            }
            return false;
        }
        return true;
    }

    public int hashCode() {
        return Objects.hash(this.objectType, this.name);
    }
}
