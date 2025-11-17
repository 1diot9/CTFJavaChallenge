package org.springframework.objenesis;

import org.springframework.objenesis.strategy.StdInstantiatorStrategy;

/* loaded from: agent.jar:BOOT-INF/lib/spring-core-6.1.3.jar:org/springframework/objenesis/ObjenesisStd.class */
public class ObjenesisStd extends ObjenesisBase {
    public ObjenesisStd() {
        super(new StdInstantiatorStrategy());
    }

    public ObjenesisStd(boolean useCache) {
        super(new StdInstantiatorStrategy(), useCache);
    }
}
