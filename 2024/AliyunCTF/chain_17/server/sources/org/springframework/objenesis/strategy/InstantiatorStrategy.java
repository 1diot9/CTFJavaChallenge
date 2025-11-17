package org.springframework.objenesis.strategy;

import org.springframework.objenesis.instantiator.ObjectInstantiator;

/* loaded from: server.jar:BOOT-INF/lib/spring-core-6.1.3.jar:org/springframework/objenesis/strategy/InstantiatorStrategy.class */
public interface InstantiatorStrategy {
    <T> ObjectInstantiator<T> newInstantiatorOf(Class<T> cls);
}
