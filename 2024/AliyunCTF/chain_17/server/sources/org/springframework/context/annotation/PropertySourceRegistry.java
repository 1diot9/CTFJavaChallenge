package org.springframework.context.annotation;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import org.apache.naming.factory.Constants;
import org.springframework.core.annotation.AnnotationAttributes;
import org.springframework.core.io.support.PropertySourceDescriptor;
import org.springframework.core.io.support.PropertySourceFactory;
import org.springframework.core.io.support.PropertySourceProcessor;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

/* loaded from: server.jar:BOOT-INF/lib/spring-context-6.1.3.jar:org/springframework/context/annotation/PropertySourceRegistry.class */
class PropertySourceRegistry {
    private final PropertySourceProcessor propertySourceProcessor;
    private final List<PropertySourceDescriptor> descriptors = new ArrayList();

    public PropertySourceRegistry(PropertySourceProcessor propertySourceProcessor) {
        this.propertySourceProcessor = propertySourceProcessor;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void processPropertySource(AnnotationAttributes propertySource) throws IOException {
        String name = propertySource.getString("name");
        if (!StringUtils.hasLength(name)) {
            name = null;
        }
        String encoding = propertySource.getString("encoding");
        if (!StringUtils.hasLength(encoding)) {
            encoding = null;
        }
        String[] locations = propertySource.getStringArray("value");
        Assert.isTrue(locations.length > 0, "At least one @PropertySource(value) location is required");
        boolean ignoreResourceNotFound = propertySource.getBoolean("ignoreResourceNotFound");
        Class<? extends PropertySourceFactory> factoryClass = propertySource.getClass(Constants.FACTORY);
        Class<? extends PropertySourceFactory> factoryClassToUse = factoryClass != PropertySourceFactory.class ? factoryClass : null;
        PropertySourceDescriptor descriptor = new PropertySourceDescriptor(Arrays.asList(locations), ignoreResourceNotFound, name, factoryClassToUse, encoding);
        this.propertySourceProcessor.processPropertySource(descriptor);
        this.descriptors.add(descriptor);
    }

    public List<PropertySourceDescriptor> getDescriptors() {
        return Collections.unmodifiableList(this.descriptors);
    }
}
