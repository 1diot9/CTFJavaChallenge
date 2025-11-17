package org.springframework.boot.context.properties.source;

import java.util.Collections;
import java.util.stream.Stream;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.ConfigurablePropertyResolver;
import org.springframework.core.env.Environment;
import org.springframework.core.env.MutablePropertySources;
import org.springframework.core.env.PropertySource;
import org.springframework.core.env.PropertySources;
import org.springframework.util.Assert;

/* loaded from: agent.jar:BOOT-INF/lib/spring-boot-3.2.2.jar:org/springframework/boot/context/properties/source/ConfigurationPropertySources.class */
public final class ConfigurationPropertySources {
    private static final String ATTACHED_PROPERTY_SOURCE_NAME = "configurationProperties";

    private ConfigurationPropertySources() {
    }

    public static ConfigurablePropertyResolver createPropertyResolver(MutablePropertySources propertySources) {
        return new ConfigurationPropertySourcesPropertyResolver(propertySources);
    }

    public static boolean isAttachedConfigurationPropertySource(PropertySource<?> propertySource) {
        return ATTACHED_PROPERTY_SOURCE_NAME.equals(propertySource.getName());
    }

    public static void attach(Environment environment) {
        Assert.isInstanceOf(ConfigurableEnvironment.class, environment);
        MutablePropertySources sources = ((ConfigurableEnvironment) environment).getPropertySources();
        PropertySource<?> attached = getAttached(sources);
        if (attached == null || !isUsingSources(attached, sources)) {
            attached = new ConfigurationPropertySourcesPropertySource(ATTACHED_PROPERTY_SOURCE_NAME, new SpringConfigurationPropertySources(sources));
        }
        sources.remove(ATTACHED_PROPERTY_SOURCE_NAME);
        sources.addFirst(attached);
    }

    private static boolean isUsingSources(PropertySource<?> attached, MutablePropertySources sources) {
        return (attached instanceof ConfigurationPropertySourcesPropertySource) && ((SpringConfigurationPropertySources) attached.getSource()).isUsingSources(sources);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static PropertySource<?> getAttached(MutablePropertySources sources) {
        if (sources != null) {
            return sources.get(ATTACHED_PROPERTY_SOURCE_NAME);
        }
        return null;
    }

    public static Iterable<ConfigurationPropertySource> get(Environment environment) {
        Assert.isInstanceOf(ConfigurableEnvironment.class, environment);
        MutablePropertySources sources = ((ConfigurableEnvironment) environment).getPropertySources();
        ConfigurationPropertySourcesPropertySource attached = (ConfigurationPropertySourcesPropertySource) sources.get(ATTACHED_PROPERTY_SOURCE_NAME);
        if (attached == null) {
            return from(sources);
        }
        return attached.getSource();
    }

    public static Iterable<ConfigurationPropertySource> from(PropertySource<?> source) {
        return Collections.singleton(ConfigurationPropertySource.from(source));
    }

    public static Iterable<ConfigurationPropertySource> from(Iterable<PropertySource<?>> sources) {
        return new SpringConfigurationPropertySources(sources);
    }

    private static Stream<PropertySource<?>> streamPropertySources(PropertySources sources) {
        return sources.stream().flatMap(ConfigurationPropertySources::flatten).filter(ConfigurationPropertySources::isIncluded);
    }

    private static Stream<PropertySource<?>> flatten(PropertySource<?> source) {
        Object source2 = source.getSource();
        if (source2 instanceof ConfigurableEnvironment) {
            ConfigurableEnvironment configurableEnvironment = (ConfigurableEnvironment) source2;
            return streamPropertySources(configurableEnvironment.getPropertySources());
        }
        return Stream.of(source);
    }

    private static boolean isIncluded(PropertySource<?> source) {
        return ((source instanceof PropertySource.StubPropertySource) || (source instanceof ConfigurationPropertySourcesPropertySource)) ? false : true;
    }
}
