package org.springframework.objenesis;

import org.springframework.objenesis.instantiator.ObjectInstantiator;

/* loaded from: agent.jar:BOOT-INF/lib/spring-core-6.1.3.jar:org/springframework/objenesis/Objenesis.class */
public interface Objenesis {
    <T> T newInstance(Class<T> cls);

    <T> ObjectInstantiator<T> getInstantiatorOf(Class<T> cls);
}
