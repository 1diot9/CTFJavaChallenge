package org.springframework.boot;

import org.springframework.boot.context.properties.source.ConfigurationPropertySources;
import org.springframework.core.env.ConfigurablePropertyResolver;
import org.springframework.core.env.MutablePropertySources;
import org.springframework.core.env.StandardEnvironment;

/* JADX INFO: Access modifiers changed from: package-private */
/* loaded from: server.jar:BOOT-INF/lib/spring-boot-3.2.2.jar:org/springframework/boot/ApplicationEnvironment.class */
public class ApplicationEnvironment extends StandardEnvironment {
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
