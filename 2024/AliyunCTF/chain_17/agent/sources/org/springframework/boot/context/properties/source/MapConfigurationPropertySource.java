package org.springframework.boot.context.properties.source;

import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.stream.Stream;
import org.springframework.core.env.MapPropertySource;
import org.springframework.util.Assert;

/* loaded from: agent.jar:BOOT-INF/lib/spring-boot-3.2.2.jar:org/springframework/boot/context/properties/source/MapConfigurationPropertySource.class */
public class MapConfigurationPropertySource implements IterableConfigurationPropertySource {
    private static final PropertyMapper[] DEFAULT_MAPPERS = {DefaultPropertyMapper.INSTANCE};
    private final Map<String, Object> source;
    private final IterableConfigurationPropertySource delegate;

    public MapConfigurationPropertySource() {
        this(Collections.emptyMap());
    }

    public MapConfigurationPropertySource(Map<?, ?> map) {
        this.source = new LinkedHashMap();
        MapPropertySource mapPropertySource = new MapPropertySource("source", this.source);
        this.delegate = new SpringIterableConfigurationPropertySource(mapPropertySource, DEFAULT_MAPPERS);
        putAll(map);
    }

    public void putAll(Map<?, ?> map) {
        Assert.notNull(map, "Map must not be null");
        assertNotReadOnlySystemAttributesMap(map);
        map.forEach(this::put);
    }

    public void put(Object name, Object value) {
        this.source.put(name != null ? name.toString() : null, value);
    }

    @Override // org.springframework.boot.context.properties.source.ConfigurationPropertySource
    public Object getUnderlyingSource() {
        return this.source;
    }

    @Override // org.springframework.boot.context.properties.source.ConfigurationPropertySource
    public ConfigurationProperty getConfigurationProperty(ConfigurationPropertyName name) {
        return this.delegate.getConfigurationProperty(name);
    }

    @Override // org.springframework.boot.context.properties.source.IterableConfigurationPropertySource, java.lang.Iterable
    public Iterator<ConfigurationPropertyName> iterator() {
        return this.delegate.iterator();
    }

    @Override // org.springframework.boot.context.properties.source.IterableConfigurationPropertySource
    public Stream<ConfigurationPropertyName> stream() {
        return this.delegate.stream();
    }

    private void assertNotReadOnlySystemAttributesMap(Map<?, ?> map) {
        try {
            map.size();
        } catch (UnsupportedOperationException ex) {
            throw new IllegalArgumentException("Security restricted maps are not supported", ex);
        }
    }
}
