package org.springframework.core.io.support;

import java.beans.PropertyEditorSupport;
import java.io.IOException;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Set;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.core.env.PropertyResolver;
import org.springframework.core.env.StandardEnvironment;
import org.springframework.core.io.Resource;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

/* loaded from: agent.jar:BOOT-INF/lib/spring-core-6.1.3.jar:org/springframework/core/io/support/ResourceArrayPropertyEditor.class */
public class ResourceArrayPropertyEditor extends PropertyEditorSupport {
    private static final Log logger = LogFactory.getLog((Class<?>) ResourceArrayPropertyEditor.class);
    private final ResourcePatternResolver resourcePatternResolver;

    @Nullable
    private PropertyResolver propertyResolver;
    private final boolean ignoreUnresolvablePlaceholders;

    public ResourceArrayPropertyEditor() {
        this(new PathMatchingResourcePatternResolver(), null, true);
    }

    public ResourceArrayPropertyEditor(ResourcePatternResolver resourcePatternResolver, @Nullable PropertyResolver propertyResolver) {
        this(resourcePatternResolver, propertyResolver, true);
    }

    public ResourceArrayPropertyEditor(ResourcePatternResolver resourcePatternResolver, @Nullable PropertyResolver propertyResolver, boolean ignoreUnresolvablePlaceholders) {
        Assert.notNull(resourcePatternResolver, "ResourcePatternResolver must not be null");
        this.resourcePatternResolver = resourcePatternResolver;
        this.propertyResolver = propertyResolver;
        this.ignoreUnresolvablePlaceholders = ignoreUnresolvablePlaceholders;
    }

    public void setAsText(String text) {
        String pattern = resolvePath(text).trim();
        String[] locationPatterns = StringUtils.commaDelimitedListToStringArray(pattern);
        if (locationPatterns.length == 1) {
            setValue(getResources(locationPatterns[0]));
        } else {
            Resource[] resources = (Resource[]) Arrays.stream(locationPatterns).map((v0) -> {
                return v0.trim();
            }).map(this::getResources).flatMap((v0) -> {
                return Arrays.stream(v0);
            }).toArray(x$0 -> {
                return new Resource[x$0];
            });
            setValue(resources);
        }
    }

    private Resource[] getResources(String locationPattern) {
        try {
            return this.resourcePatternResolver.getResources(locationPattern);
        } catch (IOException ex) {
            throw new IllegalArgumentException("Could not resolve resource location pattern [" + locationPattern + "]: " + ex.getMessage());
        }
    }

    public void setValue(Object value) throws IllegalArgumentException {
        Collection<?> asList;
        if ((value instanceof Collection) || ((value instanceof Object[]) && !(value instanceof Resource[]))) {
            if (value instanceof Collection) {
                Collection<?> collection = (Collection) value;
                asList = collection;
            } else {
                asList = Arrays.asList((Object[]) value);
            }
            Collection<?> input = asList;
            Set<Resource> merged = new LinkedHashSet<>();
            for (Object element : input) {
                if (element instanceof String) {
                    String path = (String) element;
                    String pattern = resolvePath(path.trim());
                    try {
                        Resource[] resources = this.resourcePatternResolver.getResources(pattern);
                        Collections.addAll(merged, resources);
                    } catch (IOException ex) {
                        if (logger.isDebugEnabled()) {
                            logger.debug("Could not retrieve resources for pattern '" + pattern + "'", ex);
                        }
                    }
                } else if (element instanceof Resource) {
                    Resource resource = (Resource) element;
                    merged.add(resource);
                } else {
                    throw new IllegalArgumentException("Cannot convert element [" + element + "] to [" + Resource.class.getName() + "]: only location String and Resource object supported");
                }
            }
            super.setValue(merged.toArray(new Resource[0]));
            return;
        }
        super.setValue(value);
    }

    protected String resolvePath(String path) {
        if (this.propertyResolver == null) {
            this.propertyResolver = new StandardEnvironment();
        }
        return this.ignoreUnresolvablePlaceholders ? this.propertyResolver.resolvePlaceholders(path) : this.propertyResolver.resolveRequiredPlaceholders(path);
    }
}
