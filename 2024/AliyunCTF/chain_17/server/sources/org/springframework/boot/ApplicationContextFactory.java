package org.springframework.boot;

import java.util.function.Supplier;
import org.springframework.beans.BeanUtils;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.core.env.ConfigurableEnvironment;

@FunctionalInterface
/* loaded from: server.jar:BOOT-INF/lib/spring-boot-3.2.2.jar:org/springframework/boot/ApplicationContextFactory.class */
public interface ApplicationContextFactory {
    public static final ApplicationContextFactory DEFAULT = new DefaultApplicationContextFactory();

    ConfigurableApplicationContext create(WebApplicationType webApplicationType);

    default Class<? extends ConfigurableEnvironment> getEnvironmentType(WebApplicationType webApplicationType) {
        return null;
    }

    default ConfigurableEnvironment createEnvironment(WebApplicationType webApplicationType) {
        return null;
    }

    static ApplicationContextFactory ofContextClass(Class<? extends ConfigurableApplicationContext> contextClass) {
        return of(() -> {
            return (ConfigurableApplicationContext) BeanUtils.instantiateClass(contextClass);
        });
    }

    static ApplicationContextFactory of(Supplier<ConfigurableApplicationContext> supplier) {
        return webApplicationType -> {
            return (ConfigurableApplicationContext) supplier.get();
        };
    }
}
