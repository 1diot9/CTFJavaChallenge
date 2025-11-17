package org.springframework.boot.web.reactive.context;

import org.springframework.core.env.MutablePropertySources;
import org.springframework.core.env.StandardEnvironment;

/* loaded from: server.jar:BOOT-INF/lib/spring-boot-3.2.2.jar:org/springframework/boot/web/reactive/context/StandardReactiveWebEnvironment.class */
public class StandardReactiveWebEnvironment extends StandardEnvironment implements ConfigurableReactiveWebEnvironment {
    public StandardReactiveWebEnvironment() {
    }

    protected StandardReactiveWebEnvironment(MutablePropertySources propertySources) {
        super(propertySources);
    }
}
