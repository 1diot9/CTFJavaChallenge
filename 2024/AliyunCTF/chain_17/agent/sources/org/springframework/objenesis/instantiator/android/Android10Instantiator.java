package org.springframework.objenesis.instantiator.android;

import java.io.ObjectInputStream;
import java.lang.reflect.Method;
import org.springframework.objenesis.ObjenesisException;
import org.springframework.objenesis.instantiator.ObjectInstantiator;
import org.springframework.objenesis.instantiator.annotations.Instantiator;
import org.springframework.objenesis.instantiator.annotations.Typology;

@Instantiator(Typology.STANDARD)
/* loaded from: agent.jar:BOOT-INF/lib/spring-core-6.1.3.jar:org/springframework/objenesis/instantiator/android/Android10Instantiator.class */
public class Android10Instantiator<T> implements ObjectInstantiator<T> {
    private final Class<T> type;
    private final Method newStaticMethod = getNewStaticMethod();

    public Android10Instantiator(Class<T> type) {
        this.type = type;
    }

    @Override // org.springframework.objenesis.instantiator.ObjectInstantiator
    public T newInstance() {
        try {
            return this.type.cast(this.newStaticMethod.invoke(null, this.type, Object.class));
        } catch (Exception e) {
            throw new ObjenesisException(e);
        }
    }

    private static Method getNewStaticMethod() {
        try {
            Method newStaticMethod = ObjectInputStream.class.getDeclaredMethod("newInstance", Class.class, Class.class);
            newStaticMethod.setAccessible(true);
            return newStaticMethod;
        } catch (NoSuchMethodException | RuntimeException e) {
            throw new ObjenesisException(e);
        }
    }
}
