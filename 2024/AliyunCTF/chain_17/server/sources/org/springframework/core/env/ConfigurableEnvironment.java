package org.springframework.core.env;

import java.util.Map;

/* loaded from: server.jar:BOOT-INF/lib/spring-core-6.1.3.jar:org/springframework/core/env/ConfigurableEnvironment.class */
public interface ConfigurableEnvironment extends Environment, ConfigurablePropertyResolver {
    void setActiveProfiles(String... profiles);

    void addActiveProfile(String profile);

    void setDefaultProfiles(String... profiles);

    MutablePropertySources getPropertySources();

    Map<String, Object> getSystemProperties();

    Map<String, Object> getSystemEnvironment();

    void merge(ConfigurableEnvironment parent);
}
