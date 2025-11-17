package org.springframework.boot.web.reactive.context;

import org.springframework.boot.context.properties.source.ConfigurationPropertySources;
import org.springframework.core.env.ConfigurablePropertyResolver;
import org.springframework.core.env.MutablePropertySources;

/* loaded from: server.jar:BOOT-INF/lib/spring-boot-3.2.2.jar:org/springframework/boot/web/reactive/context/ApplicationReactiveWebEnvironment.class */
class ApplicationReactiveWebEnvironment extends StandardReactiveWebEnvironment {
    @Override // org.springframework.core.env.AbstractEnvironment
    protected String doGetActiveProfilesProperty() {
        return null;
    }

    @Override // org.springframework.core.env.AbstractEnvironment
    protected String doGetDefaultProfilesProperty() {
        return null;
    }

    @Override // org.springframework.core.env.AbstractEnvironment
    protected ConfigurablePropertyResolver createPropertyResolver(MutablePropertySources propertySources) {
        return ConfigurationPropertySources.createPropertyResolver(propertySources);
    }
}
