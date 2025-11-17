package org.springframework.objenesis.strategy;

import java.io.NotSerializableException;
import java.io.Serializable;
import org.springframework.objenesis.ObjenesisException;
import org.springframework.objenesis.instantiator.ObjectInstantiator;
import org.springframework.objenesis.instantiator.android.AndroidSerializationInstantiator;
import org.springframework.objenesis.instantiator.basic.ObjectInputStreamInstantiator;
import org.springframework.objenesis.instantiator.basic.ObjectStreamClassInstantiator;
import org.springframework.objenesis.instantiator.gcj.GCJSerializationInstantiator;
import org.springframework.objenesis.instantiator.perc.PercSerializationInstantiator;
import org.springframework.objenesis.instantiator.sun.SunReflectionFactorySerializationInstantiator;

/* loaded from: server.jar:BOOT-INF/lib/spring-core-6.1.3.jar:org/springframework/objenesis/strategy/SerializingInstantiatorStrategy.class */
public class SerializingInstantiatorStrategy extends BaseInstantiatorStrategy {
    @Override // org.springframework.objenesis.strategy.InstantiatorStrategy
    public <T> ObjectInstantiator<T> newInstantiatorOf(Class<T> type) {
        if (!Serializable.class.isAssignableFrom(type)) {
            throw new ObjenesisException(new NotSerializableException(type + " not serializable"));
        }
        if (PlatformDescription.JVM_NAME.startsWith("Java HotSpot") || PlatformDescription.isThisJVM(PlatformDescription.OPENJDK)) {
            if (PlatformDescription.isGoogleAppEngine() && PlatformDescription.SPECIFICATION_VERSION.equals("1.7")) {
                return new ObjectInputStreamInstantiator(type);
            }
            return new SunReflectionFactorySerializationInstantiator(type);
        }
        if (PlatformDescription.JVM_NAME.startsWith(PlatformDescription.DALVIK)) {
            if (PlatformDescription.isAndroidOpenJDK()) {
                return new ObjectStreamClassInstantiator(type);
            }
            return new AndroidSerializationInstantiator(type);
        }
        if (PlatformDescription.JVM_NAME.startsWith(PlatformDescription.GNU)) {
            return new GCJSerializationInstantiator(type);
        }
        if (PlatformDescription.JVM_NAME.startsWith(PlatformDescription.PERC)) {
            return new PercSerializationInstantiator(type);
        }
        return new SunReflectionFactorySerializationInstantiator(type);
    }
}
