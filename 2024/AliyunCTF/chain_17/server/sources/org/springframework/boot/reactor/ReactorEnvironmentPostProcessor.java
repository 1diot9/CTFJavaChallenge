package org.springframework.boot.reactor;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.env.EnvironmentPostProcessor;
import org.springframework.boot.system.JavaVersion;
import org.springframework.core.Ordered;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.util.ClassUtils;

/* loaded from: server.jar:BOOT-INF/lib/spring-boot-3.2.2.jar:org/springframework/boot/reactor/ReactorEnvironmentPostProcessor.class */
public class ReactorEnvironmentPostProcessor implements EnvironmentPostProcessor, Ordered {
    private static final String REACTOR_DEBUGAGENT_CLASS = "reactor.tools.agent.ReactorDebugAgent";

    @Override // org.springframework.boot.env.EnvironmentPostProcessor
    public void postProcessEnvironment(ConfigurableEnvironment environment, SpringApplication application) {
        if (ClassUtils.isPresent(REACTOR_DEBUGAGENT_CLASS, null)) {
            Boolean agentEnabled = (Boolean) environment.getProperty("spring.reactor.debug-agent.enabled", Boolean.class);
            if (agentEnabled != Boolean.FALSE) {
                try {
                    Class<?> debugAgent = Class.forName(REACTOR_DEBUGAGENT_CLASS);
                    debugAgent.getMethod("init", new Class[0]).invoke(null, new Object[0]);
                } catch (Exception ex) {
                    throw new RuntimeException("Failed to init Reactor's debug agent", ex);
                }
            }
        }
        if (((Boolean) environment.getProperty("spring.threads.virtual.enabled", Boolean.TYPE, false)).booleanValue() && JavaVersion.getJavaVersion().isEqualOrNewerThan(JavaVersion.TWENTY_ONE)) {
            System.setProperty("reactor.schedulers.defaultBoundedElasticOnVirtualThreads", "true");
        }
    }

    @Override // org.springframework.core.Ordered
    public int getOrder() {
        return Integer.MAX_VALUE;
    }
}
