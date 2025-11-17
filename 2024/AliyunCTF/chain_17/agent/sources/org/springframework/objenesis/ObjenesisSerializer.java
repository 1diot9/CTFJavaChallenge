package org.springframework.objenesis;

import org.springframework.objenesis.strategy.SerializingInstantiatorStrategy;

/* loaded from: agent.jar:BOOT-INF/lib/spring-core-6.1.3.jar:org/springframework/objenesis/ObjenesisSerializer.class */
public class ObjenesisSerializer extends ObjenesisBase {
    public ObjenesisSerializer() {
        super(new SerializingInstantiatorStrategy());
    }

    public ObjenesisSerializer(boolean useCache) {
        super(new SerializingInstantiatorStrategy(), useCache);
    }
}
