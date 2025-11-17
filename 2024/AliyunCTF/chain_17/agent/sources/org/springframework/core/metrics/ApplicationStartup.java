package org.springframework.core.metrics;

/* loaded from: agent.jar:BOOT-INF/lib/spring-core-6.1.3.jar:org/springframework/core/metrics/ApplicationStartup.class */
public interface ApplicationStartup {
    public static final ApplicationStartup DEFAULT = new DefaultApplicationStartup();

    StartupStep start(String name);
}
