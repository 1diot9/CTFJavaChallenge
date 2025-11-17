package org.springframework.objenesis.instantiator.util;

import org.springframework.objenesis.ObjenesisException;

/* loaded from: agent.jar:BOOT-INF/lib/spring-core-6.1.3.jar:org/springframework/objenesis/instantiator/util/ClassUtils.class */
public final class ClassUtils {
    private ClassUtils() {
    }

    public static String classNameToInternalClassName(String className) {
        return className.replace('.', '/');
    }

    public static String classNameToResource(String className) {
        return classNameToInternalClassName(className) + ".class";
    }

    public static <T> Class<T> getExistingClass(ClassLoader classLoader, String str) {
        try {
            return (Class<T>) Class.forName(str, true, classLoader);
        } catch (ClassNotFoundException e) {
            return null;
        }
    }

    public static <T> T newInstance(Class<T> clazz) {
        try {
            return clazz.newInstance();
        } catch (IllegalAccessException | InstantiationException e) {
            throw new ObjenesisException(e);
        }
    }
}
