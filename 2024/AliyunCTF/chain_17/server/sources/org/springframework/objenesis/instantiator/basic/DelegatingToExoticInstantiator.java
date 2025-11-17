package org.springframework.objenesis.instantiator.basic;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import org.springframework.objenesis.ObjenesisException;
import org.springframework.objenesis.instantiator.ObjectInstantiator;

/* loaded from: server.jar:BOOT-INF/lib/spring-core-6.1.3.jar:org/springframework/objenesis/instantiator/basic/DelegatingToExoticInstantiator.class */
public abstract class DelegatingToExoticInstantiator<T> implements ObjectInstantiator<T> {
    private final ObjectInstantiator<T> wrapped;

    /* JADX INFO: Access modifiers changed from: protected */
    public DelegatingToExoticInstantiator(String className, Class<T> type) {
        Class<ObjectInstantiator<T>> clazz = instantiatorClass(className);
        Constructor<ObjectInstantiator<T>> constructor = instantiatorConstructor(className, clazz);
        this.wrapped = instantiator(className, type, constructor);
    }

    private ObjectInstantiator<T> instantiator(String className, Class<T> type, Constructor<ObjectInstantiator<T>> constructor) {
        try {
            return constructor.newInstance(type);
        } catch (IllegalAccessException | InstantiationException | InvocationTargetException e) {
            throw new RuntimeException("Failed to call constructor of " + className, e);
        }
    }

    private Class<ObjectInstantiator<T>> instantiatorClass(String str) {
        try {
            return (Class<ObjectInstantiator<T>>) Class.forName(str);
        } catch (ClassNotFoundException e) {
            throw new ObjenesisException(getClass().getSimpleName() + " now requires objenesis-exotic to be in the classpath", e);
        }
    }

    private Constructor<ObjectInstantiator<T>> instantiatorConstructor(String className, Class<ObjectInstantiator<T>> clazz) {
        try {
            return clazz.getConstructor(Class.class);
        } catch (NoSuchMethodException e) {
            throw new ObjenesisException("Try to find constructor taking a Class<T> in parameter on " + className + " but can't find it", e);
        }
    }

    @Override // org.springframework.objenesis.instantiator.ObjectInstantiator
    public T newInstance() {
        return this.wrapped.newInstance();
    }
}
