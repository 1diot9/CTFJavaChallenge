package org.springframework.beans.factory.aot;

import org.springframework.lang.Nullable;
import org.springframework.util.Assert;
import org.springframework.util.ClassUtils;

@FunctionalInterface
/* loaded from: agent.jar:BOOT-INF/lib/spring-beans-6.1.3.jar:org/springframework/beans/factory/aot/AutowiredArguments.class */
public interface AutowiredArguments {
    Object[] toArray();

    @Nullable
    default <T> T get(int i, Class<T> cls) {
        T t = (T) getObject(i);
        if (!ClassUtils.isAssignableValue(cls, t)) {
            throw new IllegalArgumentException("Argument type mismatch: expected '" + ClassUtils.getQualifiedName(cls) + "' for value [" + t + "]");
        }
        return t;
    }

    @Nullable
    default <T> T get(int i) {
        return (T) getObject(i);
    }

    @Nullable
    default Object getObject(int index) {
        return toArray()[index];
    }

    static AutowiredArguments of(Object[] arguments) {
        Assert.notNull(arguments, "'arguments' must not be null");
        return () -> {
            return arguments;
        };
    }
}
