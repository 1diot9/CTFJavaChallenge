package org.springframework.core.io.support;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.core.env.CompositePropertySource;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.MutablePropertySources;
import org.springframework.core.env.PropertySource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;
import org.springframework.util.ReflectionUtils;

/* loaded from: server.jar:BOOT-INF/lib/spring-core-6.1.3.jar:org/springframework/core/io/support/PropertySourceProcessor.class */
public class PropertySourceProcessor {
    private static final PropertySourceFactory defaultPropertySourceFactory = new DefaultPropertySourceFactory();
    private static final Log logger = LogFactory.getLog((Class<?>) PropertySourceProcessor.class);
    private final ConfigurableEnvironment environment;
    private final ResourcePatternResolver resourcePatternResolver;
    private final List<String> propertySourceNames = new ArrayList();

    public PropertySourceProcessor(ConfigurableEnvironment environment, ResourceLoader resourceLoader) {
        this.environment = environment;
        this.resourcePatternResolver = ResourcePatternUtils.getResourcePatternResolver(resourceLoader);
    }

    public void processPropertySource(PropertySourceDescriptor descriptor) throws IOException {
        String name = descriptor.name();
        String encoding = descriptor.encoding();
        List<String> locations = descriptor.locations();
        Assert.isTrue(locations.size() > 0, "At least one @PropertySource(value) location is required");
        boolean ignoreResourceNotFound = descriptor.ignoreResourceNotFound();
        PropertySourceFactory factory = descriptor.propertySourceFactory() != null ? instantiateClass(descriptor.propertySourceFactory()) : defaultPropertySourceFactory;
        for (String location : locations) {
            try {
                String resolvedLocation = this.environment.resolveRequiredPlaceholders(location);
                for (Resource resource : this.resourcePatternResolver.getResources(resolvedLocation)) {
                    addPropertySource(factory.createPropertySource(name, new EncodedResource(resource, encoding)));
                }
            } catch (IOException | RuntimeException ex) {
                if (ignoreResourceNotFound && ((ex instanceof IllegalArgumentException) || isIgnorableException(ex) || isIgnorableException(ex.getCause()))) {
                    if (logger.isInfoEnabled()) {
                        logger.info("Properties location [" + location + "] not resolvable: " + ex.getMessage());
                    }
                } else {
                    throw ex;
                }
            }
        }
    }

    private void addPropertySource(PropertySource<?> propertySource) {
        PropertySource<?> propertySource2;
        String name = propertySource.getName();
        MutablePropertySources propertySources = this.environment.getPropertySources();
        if (this.propertySourceNames.contains(name)) {
            PropertySource<?> existing = propertySources.get(name);
            if (existing != null) {
                if (propertySource instanceof ResourcePropertySource) {
                    ResourcePropertySource rps = (ResourcePropertySource) propertySource;
                    propertySource2 = rps.withResourceName();
                } else {
                    propertySource2 = propertySource;
                }
                PropertySource<?> newSource = propertySource2;
                if (existing instanceof CompositePropertySource) {
                    CompositePropertySource cps = (CompositePropertySource) existing;
                    cps.addFirstPropertySource(newSource);
                    return;
                }
                if (existing instanceof ResourcePropertySource) {
                    ResourcePropertySource rps2 = (ResourcePropertySource) existing;
                    existing = rps2.withResourceName();
                }
                CompositePropertySource composite = new CompositePropertySource(name);
                composite.addPropertySource(newSource);
                composite.addPropertySource(existing);
                propertySources.replace(name, composite);
                return;
            }
        }
        if (this.propertySourceNames.isEmpty()) {
            propertySources.addLast(propertySource);
        } else {
            String lastAdded = this.propertySourceNames.get(this.propertySourceNames.size() - 1);
            propertySources.addBefore(lastAdded, propertySource);
        }
        this.propertySourceNames.add(name);
    }

    private static PropertySourceFactory instantiateClass(Class<? extends PropertySourceFactory> type) {
        try {
            Constructor<? extends PropertySourceFactory> constructor = type.getDeclaredConstructor(new Class[0]);
            ReflectionUtils.makeAccessible(constructor);
            return constructor.newInstance(new Object[0]);
        } catch (Exception ex) {
            throw new IllegalStateException("Failed to instantiate " + type, ex);
        }
    }

    private static boolean isIgnorableException(@Nullable Throwable ex) {
        return (ex instanceof FileNotFoundException) || (ex instanceof UnknownHostException) || (ex instanceof SocketException);
    }
}
