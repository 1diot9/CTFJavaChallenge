package org.springframework.objenesis.strategy;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import org.springframework.objenesis.ObjenesisException;
import org.springframework.objenesis.instantiator.ObjectInstantiator;

/* loaded from: server.jar:BOOT-INF/lib/spring-core-6.1.3.jar:org/springframework/objenesis/strategy/SingleInstantiatorStrategy.class */
public class SingleInstantiatorStrategy implements InstantiatorStrategy {
    private final Constructor<?> constructor;

    public <T extends ObjectInstantiator<?>> SingleInstantiatorStrategy(Class<T> cls) {
        try {
            this.constructor = cls.getConstructor(Class.class);
        } catch (NoSuchMethodException e) {
            throw new ObjenesisException(e);
        }
    }

    @Override // org.springframework.objenesis.strategy.InstantiatorStrategy
    public <T> ObjectInstantiator<T> newInstantiatorOf(Class<T> type) {
        try {
            return (ObjectInstantiator) this.constructor.newInstance(type);
        } catch (IllegalAccessException | InstantiationException | InvocationTargetException e) {
            throw new ObjenesisException(e);
        }
    }
}
