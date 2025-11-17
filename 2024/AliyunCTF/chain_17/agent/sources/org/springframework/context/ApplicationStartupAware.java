package org.springframework.context;

import org.springframework.beans.factory.Aware;
import org.springframework.core.metrics.ApplicationStartup;

/* loaded from: agent.jar:BOOT-INF/lib/spring-context-6.1.3.jar:org/springframework/context/ApplicationStartupAware.class */
public interface ApplicationStartupAware extends Aware {
    void setApplicationStartup(ApplicationStartup applicationStartup);
}
