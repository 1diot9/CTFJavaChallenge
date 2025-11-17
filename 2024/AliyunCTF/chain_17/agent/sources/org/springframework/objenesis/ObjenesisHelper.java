package org.springframework.objenesis;

import java.io.Serializable;
import org.springframework.objenesis.instantiator.ObjectInstantiator;

/* loaded from: agent.jar:BOOT-INF/lib/spring-core-6.1.3.jar:org/springframework/objenesis/ObjenesisHelper.class */
public final class ObjenesisHelper {
    private static final Objenesis OBJENESIS_STD = new ObjenesisStd();
    private static final Objenesis OBJENESIS_SERIALIZER = new ObjenesisSerializer();

    private ObjenesisHelper() {
    }

    public static <T> T newInstance(Class<T> cls) {
        return (T) OBJENESIS_STD.newInstance(cls);
    }

    public static <T extends Serializable> T newSerializableInstance(Class<T> clazz) {
        return (T) OBJENESIS_SERIALIZER.newInstance(clazz);
    }

    public static <T> ObjectInstantiator<T> getInstantiatorOf(Class<T> clazz) {
        return OBJENESIS_STD.getInstantiatorOf(clazz);
    }

    public static <T extends Serializable> ObjectInstantiator<T> getSerializableObjectInstantiatorOf(Class<T> clazz) {
        return OBJENESIS_SERIALIZER.getInstantiatorOf(clazz);
    }
}
