package org.springframework.core.annotation;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Map;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;
import org.springframework.util.ConcurrentReferenceHashMap;
import org.springframework.util.ReflectionUtils;

/* JADX INFO: Access modifiers changed from: package-private */
/* loaded from: agent.jar:BOOT-INF/lib/spring-core-6.1.3.jar:org/springframework/core/annotation/AttributeMethods.class */
public final class AttributeMethods {
    static final AttributeMethods NONE = new AttributeMethods(null, new Method[0]);
    static final Map<Class<? extends Annotation>, AttributeMethods> cache = new ConcurrentReferenceHashMap();
    private static final Comparator<Method> methodComparator = (m1, m2) -> {
        if (m1 == null || m2 == null) {
            return m1 != null ? -1 : 1;
        }
        return m1.getName().compareTo(m2.getName());
    };

    @Nullable
    private final Class<? extends Annotation> annotationType;
    private final Method[] attributeMethods;
    private final boolean[] canThrowTypeNotPresentException;
    private final boolean hasDefaultValueMethod;
    private final boolean hasNestedAnnotation;

    private AttributeMethods(@Nullable Class<? extends Annotation> annotationType, Method[] attributeMethods) {
        this.annotationType = annotationType;
        this.attributeMethods = attributeMethods;
        this.canThrowTypeNotPresentException = new boolean[attributeMethods.length];
        boolean foundDefaultValueMethod = false;
        boolean foundNestedAnnotation = false;
        for (int i = 0; i < attributeMethods.length; i++) {
            Method method = this.attributeMethods[i];
            Class<?> type = method.getReturnType();
            if (!foundDefaultValueMethod && method.getDefaultValue() != null) {
                foundDefaultValueMethod = true;
            }
            if (!foundNestedAnnotation && (type.isAnnotation() || (type.isArray() && type.componentType().isAnnotation()))) {
                foundNestedAnnotation = true;
            }
            ReflectionUtils.makeAccessible(method);
            this.canThrowTypeNotPresentException[i] = type == Class.class || type == Class[].class || type.isEnum();
        }
        this.hasDefaultValueMethod = foundDefaultValueMethod;
        this.hasNestedAnnotation = foundNestedAnnotation;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public boolean canLoad(Annotation annotation) {
        assertAnnotation(annotation);
        for (int i = 0; i < size(); i++) {
            if (canThrowTypeNotPresentException(i)) {
                try {
                    AnnotationUtils.invokeAnnotationMethod(get(i), annotation);
                } catch (IllegalStateException e) {
                } catch (Throwable th) {
                    return false;
                }
            }
        }
        return true;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void validate(Annotation annotation) {
        assertAnnotation(annotation);
        for (int i = 0; i < size(); i++) {
            if (canThrowTypeNotPresentException(i)) {
                try {
                    AnnotationUtils.invokeAnnotationMethod(get(i), annotation);
                } catch (IllegalStateException ex) {
                    throw ex;
                } catch (Throwable ex2) {
                    throw new IllegalStateException("Could not obtain annotation attribute value for " + get(i).getName() + " declared on " + annotation.annotationType(), ex2);
                }
            }
        }
    }

    private void assertAnnotation(Annotation annotation) {
        Assert.notNull(annotation, "Annotation must not be null");
        if (this.annotationType != null) {
            Assert.isInstanceOf(this.annotationType, annotation);
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    @Nullable
    public Method get(String name) {
        int index = indexOf(name);
        if (index != -1) {
            return this.attributeMethods[index];
        }
        return null;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public Method get(int index) {
        return this.attributeMethods[index];
    }

    boolean canThrowTypeNotPresentException(int index) {
        return this.canThrowTypeNotPresentException[index];
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public int indexOf(String name) {
        for (int i = 0; i < this.attributeMethods.length; i++) {
            if (this.attributeMethods[i].getName().equals(name)) {
                return i;
            }
        }
        return -1;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public int indexOf(Method attribute) {
        for (int i = 0; i < this.attributeMethods.length; i++) {
            if (this.attributeMethods[i].equals(attribute)) {
                return i;
            }
        }
        return -1;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public int size() {
        return this.attributeMethods.length;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public boolean hasDefaultValueMethod() {
        return this.hasDefaultValueMethod;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public boolean hasNestedAnnotation() {
        return this.hasNestedAnnotation;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static AttributeMethods forAnnotationType(@Nullable Class<? extends Annotation> annotationType) {
        if (annotationType == null) {
            return NONE;
        }
        return cache.computeIfAbsent(annotationType, AttributeMethods::compute);
    }

    private static AttributeMethods compute(Class<? extends Annotation> annotationType) {
        Method[] methods = annotationType.getDeclaredMethods();
        int size = methods.length;
        for (int i = 0; i < methods.length; i++) {
            if (!isAttributeMethod(methods[i])) {
                methods[i] = null;
                size--;
            }
        }
        if (size == 0) {
            return NONE;
        }
        Arrays.sort(methods, methodComparator);
        Method[] attributeMethods = (Method[]) Arrays.copyOf(methods, size);
        return new AttributeMethods(annotationType, attributeMethods);
    }

    private static boolean isAttributeMethod(Method method) {
        return method.getParameterCount() == 0 && method.getReturnType() != Void.TYPE;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static String describe(@Nullable Method attribute) {
        if (attribute == null) {
            return "(none)";
        }
        return describe(attribute.getDeclaringClass(), attribute.getName());
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static String describe(@Nullable Class<?> annotationType, @Nullable String attributeName) {
        if (attributeName == null) {
            return "(none)";
        }
        String in = annotationType != null ? " in annotation [" + annotationType.getName() + "]" : "";
        return "attribute '" + attributeName + "'" + in;
    }
}
