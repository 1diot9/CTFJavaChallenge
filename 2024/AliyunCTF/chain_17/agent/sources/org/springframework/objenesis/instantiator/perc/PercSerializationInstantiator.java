package org.springframework.objenesis.instantiator.perc;

import java.io.ObjectInputStream;
import java.io.Serializable;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import org.springframework.objenesis.ObjenesisException;
import org.springframework.objenesis.instantiator.ObjectInstantiator;
import org.springframework.objenesis.instantiator.annotations.Instantiator;
import org.springframework.objenesis.instantiator.annotations.Typology;

@Instantiator(Typology.SERIALIZATION)
/* loaded from: agent.jar:BOOT-INF/lib/spring-core-6.1.3.jar:org/springframework/objenesis/instantiator/perc/PercSerializationInstantiator.class */
public class PercSerializationInstantiator<T> implements ObjectInstantiator<T> {
    private final Object[] typeArgs;
    private final Method newInstanceMethod;

    public PercSerializationInstantiator(Class<T> type) {
        Class<T> cls = type;
        while (true) {
            Class<T> cls2 = cls;
            if (Serializable.class.isAssignableFrom(cls2)) {
                cls = cls2.getSuperclass();
            } else {
                try {
                    Class<?> percMethodClass = Class.forName("COM.newmonics.PercClassLoader.Method");
                    this.newInstanceMethod = ObjectInputStream.class.getDeclaredMethod("noArgConstruct", Class.class, Object.class, percMethodClass);
                    this.newInstanceMethod.setAccessible(true);
                    Class<?> percClassClass = Class.forName("COM.newmonics.PercClassLoader.PercClass");
                    Method getPercClassMethod = percClassClass.getDeclaredMethod("getPercClass", Class.class);
                    Object someObject = getPercClassMethod.invoke(null, cls2);
                    Method findMethodMethod = someObject.getClass().getDeclaredMethod("findMethod", String.class);
                    Object percMethod = findMethodMethod.invoke(someObject, "<init>()V");
                    this.typeArgs = new Object[]{cls2, type, percMethod};
                    return;
                } catch (ClassNotFoundException | IllegalAccessException | NoSuchMethodException | InvocationTargetException e) {
                    throw new ObjenesisException(e);
                }
            }
        }
    }

    @Override // org.springframework.objenesis.instantiator.ObjectInstantiator
    public T newInstance() {
        try {
            return (T) this.newInstanceMethod.invoke(null, this.typeArgs);
        } catch (IllegalAccessException | InvocationTargetException e) {
            throw new ObjenesisException(e);
        }
    }
}
