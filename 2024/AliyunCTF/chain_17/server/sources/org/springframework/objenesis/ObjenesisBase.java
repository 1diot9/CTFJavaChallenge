package org.springframework.objenesis;

import java.util.concurrent.ConcurrentHashMap;
import org.springframework.objenesis.instantiator.ObjectInstantiator;
import org.springframework.objenesis.strategy.InstantiatorStrategy;

/* loaded from: server.jar:BOOT-INF/lib/spring-core-6.1.3.jar:org/springframework/objenesis/ObjenesisBase.class */
public class ObjenesisBase implements Objenesis {
    protected final InstantiatorStrategy strategy;
    protected ConcurrentHashMap<String, ObjectInstantiator<?>> cache;

    public ObjenesisBase(InstantiatorStrategy strategy) {
        this(strategy, true);
    }

    public ObjenesisBase(InstantiatorStrategy strategy, boolean useCache) {
        if (strategy == null) {
            throw new IllegalArgumentException("A strategy can't be null");
        }
        this.strategy = strategy;
        this.cache = useCache ? new ConcurrentHashMap<>() : null;
    }

    public String toString() {
        return getClass().getName() + " using " + this.strategy.getClass().getName() + (this.cache == null ? " without" : " with") + " caching";
    }

    @Override // org.springframework.objenesis.Objenesis
    public <T> T newInstance(Class<T> clazz) {
        return getInstantiatorOf(clazz).newInstance();
    }

    @Override // org.springframework.objenesis.Objenesis
    public <T> ObjectInstantiator<T> getInstantiatorOf(Class<T> cls) {
        if (cls.isPrimitive()) {
            throw new IllegalArgumentException("Primitive types can't be instantiated in Java");
        }
        if (this.cache == null) {
            return this.strategy.newInstantiatorOf(cls);
        }
        ObjectInstantiator<?> objectInstantiator = this.cache.get(cls.getName());
        if (objectInstantiator == null) {
            ObjectInstantiator<?> newInstantiatorOf = this.strategy.newInstantiatorOf(cls);
            objectInstantiator = this.cache.putIfAbsent(cls.getName(), newInstantiatorOf);
            if (objectInstantiator == null) {
                objectInstantiator = newInstantiatorOf;
            }
        }
        return (ObjectInstantiator<T>) objectInstantiator;
    }
}
