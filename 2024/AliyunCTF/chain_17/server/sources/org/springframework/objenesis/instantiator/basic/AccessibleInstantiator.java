package org.springframework.objenesis.instantiator.basic;

import org.springframework.objenesis.instantiator.annotations.Instantiator;
import org.springframework.objenesis.instantiator.annotations.Typology;

@Instantiator(Typology.NOT_COMPLIANT)
/* loaded from: server.jar:BOOT-INF/lib/spring-core-6.1.3.jar:org/springframework/objenesis/instantiator/basic/AccessibleInstantiator.class */
public class AccessibleInstantiator<T> extends ConstructorInstantiator<T> {
    public AccessibleInstantiator(Class<T> type) {
        super(type);
        if (this.constructor != null) {
            this.constructor.setAccessible(true);
        }
    }
}
