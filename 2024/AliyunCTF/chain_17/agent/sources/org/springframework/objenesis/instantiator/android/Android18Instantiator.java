package org.springframework.objenesis.instantiator.android;

import java.io.ObjectStreamClass;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import org.springframework.objenesis.ObjenesisException;
import org.springframework.objenesis.instantiator.ObjectInstantiator;
import org.springframework.objenesis.instantiator.annotations.Instantiator;
import org.springframework.objenesis.instantiator.annotations.Typology;

@Instantiator(Typology.STANDARD)
/* loaded from: agent.jar:BOOT-INF/lib/spring-core-6.1.3.jar:org/springframework/objenesis/instantiator/android/Android18Instantiator.class */
public class Android18Instantiator<T> implements ObjectInstantiator<T> {
    private final Class<T> type;
    private final Method newInstanceMethod = getNewInstanceMethod();
    private final Long objectConstructorId = findConstructorIdForJavaLangObjectConstructor();

    public Android18Instantiator(Class<T> type) {
        this.type = type;
    }

    @Override // org.springframework.objenesis.instantiator.ObjectInstantiator
    public T newInstance() {
        try {
            return this.type.cast(this.newInstanceMethod.invoke(null, this.type, this.objectConstructorId));
        } catch (Exception e) {
            throw new ObjenesisException(e);
        }
    }

    private static Method getNewInstanceMethod() {
        try {
            Method newInstanceMethod = ObjectStreamClass.class.getDeclaredMethod("newInstance", Class.class, Long.TYPE);
            newInstanceMethod.setAccessible(true);
            return newInstanceMethod;
        } catch (NoSuchMethodException | RuntimeException e) {
            throw new ObjenesisException(e);
        }
    }

    private static Long findConstructorIdForJavaLangObjectConstructor() {
        try {
            Method newInstanceMethod = ObjectStreamClass.class.getDeclaredMethod("getConstructorId", Class.class);
            newInstanceMethod.setAccessible(true);
            return (Long) newInstanceMethod.invoke(null, Object.class);
        } catch (IllegalAccessException | NoSuchMethodException | RuntimeException | InvocationTargetException e) {
            throw new ObjenesisException(e);
        }
    }
}
