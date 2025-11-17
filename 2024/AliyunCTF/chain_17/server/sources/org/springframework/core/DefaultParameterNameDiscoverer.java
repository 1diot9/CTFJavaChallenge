package org.springframework.core;

/* loaded from: server.jar:BOOT-INF/lib/spring-core-6.1.3.jar:org/springframework/core/DefaultParameterNameDiscoverer.class */
public class DefaultParameterNameDiscoverer extends PrioritizedParameterNameDiscoverer {
    public DefaultParameterNameDiscoverer() {
        if (KotlinDetector.isKotlinReflectPresent()) {
            addDiscoverer(new KotlinReflectionParameterNameDiscoverer());
        }
        addDiscoverer(new StandardReflectionParameterNameDiscoverer());
    }
}
