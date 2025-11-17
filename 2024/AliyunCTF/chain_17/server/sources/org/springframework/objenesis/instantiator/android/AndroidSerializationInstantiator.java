package org.springframework.objenesis.instantiator.android;

import java.io.ObjectStreamClass;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import org.springframework.objenesis.ObjenesisException;
import org.springframework.objenesis.instantiator.ObjectInstantiator;
import org.springframework.objenesis.instantiator.annotations.Instantiator;
import org.springframework.objenesis.instantiator.annotations.Typology;

@Instantiator(Typology.SERIALIZATION)
/* loaded from: server.jar:BOOT-INF/lib/spring-core-6.1.3.jar:org/springframework/objenesis/instantiator/android/AndroidSerializationInstantiator.class */
public class AndroidSerializationInstantiator<T> implements ObjectInstantiator<T> {
    private final Class<T> type;
    private final ObjectStreamClass objectStreamClass;
    private final Method newInstanceMethod = getNewInstanceMethod();

    public AndroidSerializationInstantiator(Class<T> type) {
        this.type = type;
        try {
            Method m = ObjectStreamClass.class.getMethod("lookupAny", Class.class);
            try {
                this.objectStreamClass = (ObjectStreamClass) m.invoke(null, type);
            } catch (IllegalAccessException | InvocationTargetException e) {
                throw new ObjenesisException(e);
            }
        } catch (NoSuchMethodException e2) {
            throw new ObjenesisException(e2);
        }
    }

    @Override // org.springframework.objenesis.instantiator.ObjectInstantiator
    public T newInstance() {
        try {
            return this.type.cast(this.newInstanceMethod.invoke(this.objectStreamClass, this.type));
        } catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
            throw new ObjenesisException(e);
        }
    }

    private static Method getNewInstanceMethod() {
        try {
            Method newInstanceMethod = ObjectStreamClass.class.getDeclaredMethod("newInstance", Class.class);
            newInstanceMethod.setAccessible(true);
            return newInstanceMethod;
        } catch (NoSuchMethodException | RuntimeException e) {
            throw new ObjenesisException(e);
        }
    }
}
