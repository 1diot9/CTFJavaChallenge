package org.springframework.boot;

import java.lang.reflect.Constructor;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.MutablePropertySources;
import org.springframework.core.env.PropertySource;
import org.springframework.util.ClassUtils;
import org.springframework.util.ReflectionUtils;
import org.springframework.web.context.support.StandardServletEnvironment;

/* JADX INFO: Access modifiers changed from: package-private */
/* loaded from: server.jar:BOOT-INF/lib/spring-boot-3.2.2.jar:org/springframework/boot/EnvironmentConverter.class */
public final class EnvironmentConverter {
    private static final String CONFIGURABLE_WEB_ENVIRONMENT_CLASS = "org.springframework.web.context.ConfigurableWebEnvironment";
    private static final Set<String> SERVLET_ENVIRONMENT_SOURCE_NAMES;
    private final ClassLoader classLoader;

    static {
        Set<String> names = new HashSet<>();
        names.add(StandardServletEnvironment.SERVLET_CONTEXT_PROPERTY_SOURCE_NAME);
        names.add(StandardServletEnvironment.SERVLET_CONFIG_PROPERTY_SOURCE_NAME);
        names.add(StandardServletEnvironment.JNDI_PROPERTY_SOURCE_NAME);
        SERVLET_ENVIRONMENT_SOURCE_NAMES = Collections.unmodifiableSet(names);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public EnvironmentConverter(ClassLoader classLoader) {
        this.classLoader = classLoader;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public ConfigurableEnvironment convertEnvironmentIfNecessary(ConfigurableEnvironment environment, Class<? extends ConfigurableEnvironment> type) {
        if (type.equals(environment.getClass())) {
            return environment;
        }
        return convertEnvironment(environment, type);
    }

    private ConfigurableEnvironment convertEnvironment(ConfigurableEnvironment environment, Class<? extends ConfigurableEnvironment> type) {
        ConfigurableEnvironment result = createEnvironment(type);
        result.setActiveProfiles(environment.getActiveProfiles());
        result.setConversionService(environment.getConversionService());
        copyPropertySources(environment, result);
        return result;
    }

    private ConfigurableEnvironment createEnvironment(Class<? extends ConfigurableEnvironment> type) {
        try {
            Constructor<? extends ConfigurableEnvironment> constructor = type.getDeclaredConstructor(new Class[0]);
            ReflectionUtils.makeAccessible(constructor);
            return constructor.newInstance(new Object[0]);
        } catch (Exception e) {
            return new ApplicationEnvironment();
        }
    }

    private void copyPropertySources(ConfigurableEnvironment source, ConfigurableEnvironment target) {
        removePropertySources(target.getPropertySources(), isServletEnvironment(target.getClass(), this.classLoader));
        Iterator<PropertySource<?>> it = source.getPropertySources().iterator();
        while (it.hasNext()) {
            PropertySource<?> propertySource = it.next();
            if (!SERVLET_ENVIRONMENT_SOURCE_NAMES.contains(propertySource.getName())) {
                target.getPropertySources().addLast(propertySource);
            }
        }
    }

    private boolean isServletEnvironment(Class<?> conversionType, ClassLoader classLoader) {
        try {
            Class<?> webEnvironmentClass = ClassUtils.forName(CONFIGURABLE_WEB_ENVIRONMENT_CLASS, classLoader);
            return webEnvironmentClass.isAssignableFrom(conversionType);
        } catch (Throwable th) {
            return false;
        }
    }

    private void removePropertySources(MutablePropertySources propertySources, boolean isServletEnvironment) {
        Set<String> names = new HashSet<>();
        Iterator<PropertySource<?>> it = propertySources.iterator();
        while (it.hasNext()) {
            PropertySource<?> propertySource = it.next();
            names.add(propertySource.getName());
        }
        for (String name : names) {
            if (!isServletEnvironment || !SERVLET_ENVIRONMENT_SOURCE_NAMES.contains(name)) {
                propertySources.remove(name);
            }
        }
    }
}
