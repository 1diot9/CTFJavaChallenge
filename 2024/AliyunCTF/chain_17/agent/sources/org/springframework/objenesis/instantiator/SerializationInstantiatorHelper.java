package org.springframework.objenesis.instantiator;

import java.io.Serializable;

/* loaded from: agent.jar:BOOT-INF/lib/spring-core-6.1.3.jar:org/springframework/objenesis/instantiator/SerializationInstantiatorHelper.class */
public class SerializationInstantiatorHelper {
    public static <T> Class<? super T> getNonSerializableSuperClass(Class<T> cls) {
        Class<T> cls2 = cls;
        while (Serializable.class.isAssignableFrom(cls2)) {
            cls2 = cls2.getSuperclass();
            if (cls2 == null) {
                throw new Error("Bad class hierarchy: No non-serializable parents");
            }
        }
        return (Class<? super T>) cls2;
    }
}
