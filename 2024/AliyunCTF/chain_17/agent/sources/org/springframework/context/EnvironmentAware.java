package org.springframework.context;

import org.springframework.beans.factory.Aware;
import org.springframework.core.env.Environment;

/* loaded from: agent.jar:BOOT-INF/lib/spring-context-6.1.3.jar:org/springframework/context/EnvironmentAware.class */
public interface EnvironmentAware extends Aware {
    void setEnvironment(Environment environment);
}
