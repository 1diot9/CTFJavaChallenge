package org.springframework.objenesis.instantiator.basic;

import org.springframework.objenesis.instantiator.annotations.Instantiator;
import org.springframework.objenesis.instantiator.annotations.Typology;

@Instantiator(Typology.STANDARD)
/* loaded from: agent.jar:BOOT-INF/lib/spring-core-6.1.3.jar:org/springframework/objenesis/instantiator/basic/ProxyingInstantiator.class */
public class ProxyingInstantiator<T> extends DelegatingToExoticInstantiator<T> {
    public ProxyingInstantiator(Class<T> type) {
        super("org.springframework.objenesis.instantiator.exotic.ProxyingInstantiator", type);
    }
}
