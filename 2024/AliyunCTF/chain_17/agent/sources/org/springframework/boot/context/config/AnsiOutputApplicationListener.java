package org.springframework.boot.context.config;

import org.springframework.boot.ansi.AnsiOutput;
import org.springframework.boot.context.event.ApplicationEnvironmentPreparedEvent;
import org.springframework.boot.context.properties.bind.Binder;
import org.springframework.context.ApplicationListener;
import org.springframework.core.Ordered;
import org.springframework.core.env.ConfigurableEnvironment;

/* loaded from: agent.jar:BOOT-INF/lib/spring-boot-3.2.2.jar:org/springframework/boot/context/config/AnsiOutputApplicationListener.class */
public class AnsiOutputApplicationListener implements ApplicationListener<ApplicationEnvironmentPreparedEvent>, Ordered {
    @Override // org.springframework.context.ApplicationListener
    public void onApplicationEvent(ApplicationEnvironmentPreparedEvent event) {
        ConfigurableEnvironment environment = event.getEnvironment();
        Binder.get(environment).bind("spring.output.ansi.enabled", AnsiOutput.Enabled.class).ifBound(AnsiOutput::setEnabled);
        AnsiOutput.setConsoleAvailable((Boolean) environment.getProperty("spring.output.ansi.console-available", Boolean.class));
    }

    @Override // org.springframework.core.Ordered
    public int getOrder() {
        return -2147483637;
    }
}
