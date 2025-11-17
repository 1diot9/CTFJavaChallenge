package org.springframework.core.env;

/* loaded from: server.jar:BOOT-INF/lib/spring-core-6.1.3.jar:org/springframework/core/env/StandardEnvironment.class */
public class StandardEnvironment extends AbstractEnvironment {
    public static final String SYSTEM_ENVIRONMENT_PROPERTY_SOURCE_NAME = "systemEnvironment";
    public static final String SYSTEM_PROPERTIES_PROPERTY_SOURCE_NAME = "systemProperties";

    public StandardEnvironment() {
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public StandardEnvironment(MutablePropertySources propertySources) {
        super(propertySources);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // org.springframework.core.env.AbstractEnvironment
    public void customizePropertySources(MutablePropertySources propertySources) {
        propertySources.addLast(new PropertiesPropertySource("systemProperties", getSystemProperties()));
        propertySources.addLast(new SystemEnvironmentPropertySource("systemEnvironment", getSystemEnvironment()));
    }
}
