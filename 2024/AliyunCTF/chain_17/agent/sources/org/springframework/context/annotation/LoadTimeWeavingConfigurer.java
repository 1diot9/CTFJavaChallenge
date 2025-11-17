package org.springframework.context.annotation;

import org.springframework.instrument.classloading.LoadTimeWeaver;

/* loaded from: agent.jar:BOOT-INF/lib/spring-context-6.1.3.jar:org/springframework/context/annotation/LoadTimeWeavingConfigurer.class */
public interface LoadTimeWeavingConfigurer {
    LoadTimeWeaver getLoadTimeWeaver();
}
