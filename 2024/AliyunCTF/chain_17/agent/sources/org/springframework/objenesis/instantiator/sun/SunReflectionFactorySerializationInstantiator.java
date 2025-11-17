package org.springframework.objenesis.instantiator.sun;

import java.io.NotSerializableException;
import java.lang.reflect.Constructor;
import org.springframework.objenesis.ObjenesisException;
import org.springframework.objenesis.instantiator.ObjectInstantiator;
import org.springframework.objenesis.instantiator.SerializationInstantiatorHelper;
import org.springframework.objenesis.instantiator.annotations.Instantiator;
import org.springframework.objenesis.instantiator.annotations.Typology;

@Instantiator(Typology.SERIALIZATION)
/* loaded from: agent.jar:BOOT-INF/lib/spring-core-6.1.3.jar:org/springframework/objenesis/instantiator/sun/SunReflectionFactorySerializationInstantiator.class */
public class SunReflectionFactorySerializationInstantiator<T> implements ObjectInstantiator<T> {
    private final Constructor<T> mungedConstructor;

    public SunReflectionFactorySerializationInstantiator(Class<T> type) {
        Class<? super T> nonSerializableAncestor = SerializationInstantiatorHelper.getNonSerializableSuperClass(type);
        try {
            this.mungedConstructor = SunReflectionFactoryHelper.newConstructorForSerialization(type, nonSerializableAncestor.getDeclaredConstructor((Class[]) null));
            this.mungedConstructor.setAccessible(true);
        } catch (NoSuchMethodException e) {
            throw new ObjenesisException(new NotSerializableException(type + " has no suitable superclass constructor"));
        }
    }

    @Override // org.springframework.objenesis.instantiator.ObjectInstantiator
    public T newInstance() {
        try {
            return this.mungedConstructor.newInstance((Object[]) null);
        } catch (Exception e) {
            throw new ObjenesisException(e);
        }
    }
}
