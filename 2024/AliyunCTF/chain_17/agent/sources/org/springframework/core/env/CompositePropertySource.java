package org.springframework.core.env;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import org.springframework.lang.Nullable;
import org.springframework.util.StringUtils;

/* loaded from: agent.jar:BOOT-INF/lib/spring-core-6.1.3.jar:org/springframework/core/env/CompositePropertySource.class */
public class CompositePropertySource extends EnumerablePropertySource<Object> {
    private final Set<PropertySource<?>> propertySources;

    public CompositePropertySource(String name) {
        super(name);
        this.propertySources = new LinkedHashSet();
    }

    @Override // org.springframework.core.env.PropertySource
    @Nullable
    public Object getProperty(String name) {
        for (PropertySource<?> propertySource : this.propertySources) {
            Object candidate = propertySource.getProperty(name);
            if (candidate != null) {
                return candidate;
            }
        }
        return null;
    }

    @Override // org.springframework.core.env.EnumerablePropertySource, org.springframework.core.env.PropertySource
    public boolean containsProperty(String name) {
        for (PropertySource<?> propertySource : this.propertySources) {
            if (propertySource.containsProperty(name)) {
                return true;
            }
        }
        return false;
    }

    @Override // org.springframework.core.env.EnumerablePropertySource
    public String[] getPropertyNames() {
        List<String[]> namesList = new ArrayList<>(this.propertySources.size());
        int total = 0;
        for (PropertySource<?> propertySource : this.propertySources) {
            if (!(propertySource instanceof EnumerablePropertySource)) {
                throw new IllegalStateException("Failed to enumerate property names due to non-enumerable property source: " + propertySource);
            }
            EnumerablePropertySource<?> enumerablePropertySource = (EnumerablePropertySource) propertySource;
            String[] names = enumerablePropertySource.getPropertyNames();
            namesList.add(names);
            total += names.length;
        }
        Set<String> allNames = new LinkedHashSet<>(total);
        namesList.forEach(names2 -> {
            Collections.addAll(allNames, names2);
        });
        return StringUtils.toStringArray(allNames);
    }

    public void addPropertySource(PropertySource<?> propertySource) {
        this.propertySources.add(propertySource);
    }

    public void addFirstPropertySource(PropertySource<?> propertySource) {
        List<PropertySource<?>> existing = new ArrayList<>(this.propertySources);
        this.propertySources.clear();
        this.propertySources.add(propertySource);
        this.propertySources.addAll(existing);
    }

    public Collection<PropertySource<?>> getPropertySources() {
        return this.propertySources;
    }

    @Override // org.springframework.core.env.PropertySource
    public String toString() {
        return getClass().getSimpleName() + " {name='" + this.name + "', propertySources=" + this.propertySources + "}";
    }
}
