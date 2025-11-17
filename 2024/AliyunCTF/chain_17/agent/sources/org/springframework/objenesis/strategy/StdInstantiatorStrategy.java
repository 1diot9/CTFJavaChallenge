package org.springframework.objenesis.strategy;

import java.io.Serializable;
import org.springframework.objenesis.instantiator.ObjectInstantiator;
import org.springframework.objenesis.instantiator.android.Android10Instantiator;
import org.springframework.objenesis.instantiator.android.Android17Instantiator;
import org.springframework.objenesis.instantiator.android.Android18Instantiator;
import org.springframework.objenesis.instantiator.basic.AccessibleInstantiator;
import org.springframework.objenesis.instantiator.basic.ObjectInputStreamInstantiator;
import org.springframework.objenesis.instantiator.gcj.GCJInstantiator;
import org.springframework.objenesis.instantiator.perc.PercInstantiator;
import org.springframework.objenesis.instantiator.sun.SunReflectionFactoryInstantiator;
import org.springframework.objenesis.instantiator.sun.UnsafeFactoryInstantiator;

/* loaded from: agent.jar:BOOT-INF/lib/spring-core-6.1.3.jar:org/springframework/objenesis/strategy/StdInstantiatorStrategy.class */
public class StdInstantiatorStrategy extends BaseInstantiatorStrategy {
    @Override // org.springframework.objenesis.strategy.InstantiatorStrategy
    public <T> ObjectInstantiator<T> newInstantiatorOf(Class<T> type) {
        if (PlatformDescription.isThisJVM("Java HotSpot") || PlatformDescription.isThisJVM(PlatformDescription.OPENJDK)) {
            if (PlatformDescription.isGoogleAppEngine() && PlatformDescription.SPECIFICATION_VERSION.equals("1.7")) {
                if (Serializable.class.isAssignableFrom(type)) {
                    return new ObjectInputStreamInstantiator(type);
                }
                return new AccessibleInstantiator(type);
            }
            return new SunReflectionFactoryInstantiator(type);
        }
        if (PlatformDescription.isThisJVM(PlatformDescription.DALVIK)) {
            if (PlatformDescription.isAndroidOpenJDK()) {
                return new UnsafeFactoryInstantiator(type);
            }
            if (PlatformDescription.ANDROID_VERSION <= 10) {
                return new Android10Instantiator(type);
            }
            if (PlatformDescription.ANDROID_VERSION <= 17) {
                return new Android17Instantiator(type);
            }
            return new Android18Instantiator(type);
        }
        if (PlatformDescription.isThisJVM(PlatformDescription.GNU)) {
            return new GCJInstantiator(type);
        }
        if (PlatformDescription.isThisJVM(PlatformDescription.PERC)) {
            return new PercInstantiator(type);
        }
        return new UnsafeFactoryInstantiator(type);
    }
}
