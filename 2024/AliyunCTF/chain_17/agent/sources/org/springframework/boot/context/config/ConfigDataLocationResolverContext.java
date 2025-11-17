package org.springframework.boot.context.config;

import org.springframework.boot.ConfigurableBootstrapContext;
import org.springframework.boot.context.properties.bind.Binder;

/* loaded from: agent.jar:BOOT-INF/lib/spring-boot-3.2.2.jar:org/springframework/boot/context/config/ConfigDataLocationResolverContext.class */
public interface ConfigDataLocationResolverContext {
    Binder getBinder();

    ConfigDataResource getParent();

    ConfigurableBootstrapContext getBootstrapContext();
}
