package org.springframework.core.env;

import java.util.Iterator;
import java.util.List;
import java.util.Spliterator;
import java.util.Spliterators;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.stream.Stream;
import org.springframework.lang.Nullable;

/* loaded from: agent.jar:BOOT-INF/lib/spring-core-6.1.3.jar:org/springframework/core/env/MutablePropertySources.class */
public class MutablePropertySources implements PropertySources {
    private final List<PropertySource<?>> propertySourceList;

    public MutablePropertySources() {
        this.propertySourceList = new CopyOnWriteArrayList();
    }

    public MutablePropertySources(PropertySources propertySources) {
        this();
        for (PropertySource<?> propertySource : propertySources) {
            addLast(propertySource);
        }
    }

    @Override // java.lang.Iterable
    public Iterator<PropertySource<?>> iterator() {
        return this.propertySourceList.iterator();
    }

    @Override // java.lang.Iterable
    public Spliterator<PropertySource<?>> spliterator() {
        return Spliterators.spliterator(this.propertySourceList, 0);
    }

    @Override // org.springframework.core.env.PropertySources
    public Stream<PropertySource<?>> stream() {
        return this.propertySourceList.stream();
    }

    @Override // org.springframework.core.env.PropertySources
    public boolean contains(String name) {
        for (PropertySource<?> propertySource : this.propertySourceList) {
            if (propertySource.getName().equals(name)) {
                return true;
            }
        }
        return false;
    }

    @Override // org.springframework.core.env.PropertySources
    @Nullable
    public PropertySource<?> get(String name) {
        for (PropertySource<?> propertySource : this.propertySourceList) {
            if (propertySource.getName().equals(name)) {
                return propertySource;
            }
        }
        return null;
    }

    public void addFirst(PropertySource<?> propertySource) {
        synchronized (this.propertySourceList) {
            removeIfPresent(propertySource);
            this.propertySourceList.add(0, propertySource);
        }
    }

    public void addLast(PropertySource<?> propertySource) {
        synchronized (this.propertySourceList) {
            removeIfPresent(propertySource);
            this.propertySourceList.add(propertySource);
        }
    }

    public void addBefore(String relativePropertySourceName, PropertySource<?> propertySource) {
        assertLegalRelativeAddition(relativePropertySourceName, propertySource);
        synchronized (this.propertySourceList) {
            removeIfPresent(propertySource);
            int index = assertPresentAndGetIndex(relativePropertySourceName);
            addAtIndex(index, propertySource);
        }
    }

    public void addAfter(String relativePropertySourceName, PropertySource<?> propertySource) {
        assertLegalRelativeAddition(relativePropertySourceName, propertySource);
        synchronized (this.propertySourceList) {
            removeIfPresent(propertySource);
            int index = assertPresentAndGetIndex(relativePropertySourceName);
            addAtIndex(index + 1, propertySource);
        }
    }

    public int precedenceOf(PropertySource<?> propertySource) {
        return this.propertySourceList.indexOf(propertySource);
    }

    @Nullable
    public PropertySource<?> remove(String name) {
        PropertySource<?> remove;
        synchronized (this.propertySourceList) {
            int index = this.propertySourceList.indexOf(PropertySource.named(name));
            remove = index != -1 ? this.propertySourceList.remove(index) : null;
        }
        return remove;
    }

    public void replace(String name, PropertySource<?> propertySource) {
        synchronized (this.propertySourceList) {
            int index = assertPresentAndGetIndex(name);
            this.propertySourceList.set(index, propertySource);
        }
    }

    public int size() {
        return this.propertySourceList.size();
    }

    public String toString() {
        return this.propertySourceList.toString();
    }

    protected void assertLegalRelativeAddition(String relativePropertySourceName, PropertySource<?> propertySource) {
        String newPropertySourceName = propertySource.getName();
        if (relativePropertySourceName.equals(newPropertySourceName)) {
            throw new IllegalArgumentException("PropertySource named '" + newPropertySourceName + "' cannot be added relative to itself");
        }
    }

    protected void removeIfPresent(PropertySource<?> propertySource) {
        this.propertySourceList.remove(propertySource);
    }

    private void addAtIndex(int index, PropertySource<?> propertySource) {
        removeIfPresent(propertySource);
        this.propertySourceList.add(index, propertySource);
    }

    private int assertPresentAndGetIndex(String name) {
        int index = this.propertySourceList.indexOf(PropertySource.named(name));
        if (index == -1) {
            throw new IllegalArgumentException("PropertySource named '" + name + "' does not exist");
        }
        return index;
    }
}
