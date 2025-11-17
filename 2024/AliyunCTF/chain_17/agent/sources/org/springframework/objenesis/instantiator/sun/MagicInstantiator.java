package org.springframework.objenesis.instantiator.sun;

import org.springframework.objenesis.instantiator.annotations.Instantiator;
import org.springframework.objenesis.instantiator.annotations.Typology;
import org.springframework.objenesis.instantiator.basic.DelegatingToExoticInstantiator;

@Instantiator(Typology.STANDARD)
/* loaded from: agent.jar:BOOT-INF/lib/spring-core-6.1.3.jar:org/springframework/objenesis/instantiator/sun/MagicInstantiator.class */
public class MagicInstantiator<T> extends DelegatingToExoticInstantiator<T> {
    public MagicInstantiator(Class<T> type) {
        super("org.springframework.objenesis.instantiator.exotic.MagicInstantiator", type);
    }
}
