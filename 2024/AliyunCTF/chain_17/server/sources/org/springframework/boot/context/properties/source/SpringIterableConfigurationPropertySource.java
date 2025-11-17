package org.springframework.boot.context.properties.source;

import java.util.Arrays;
import java.util.Collections;
import java.util.ConcurrentModificationException;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.Set;
import java.util.function.BiPredicate;
import java.util.function.Supplier;
import java.util.stream.Stream;
import org.springframework.boot.origin.Origin;
import org.springframework.boot.origin.OriginLookup;
import org.springframework.boot.origin.PropertySourceOrigin;
import org.springframework.core.env.EnumerablePropertySource;
import org.springframework.core.env.MapPropertySource;

/* JADX INFO: Access modifiers changed from: package-private */
/* loaded from: server.jar:BOOT-INF/lib/spring-boot-3.2.2.jar:org/springframework/boot/context/properties/source/SpringIterableConfigurationPropertySource.class */
public class SpringIterableConfigurationPropertySource extends SpringConfigurationPropertySource implements IterableConfigurationPropertySource, CachingConfigurationPropertySource {
    private final BiPredicate<ConfigurationPropertyName, ConfigurationPropertyName> ancestorOfCheck;
    private final SoftReferenceConfigurationPropertyCache<Mappings> cache;
    private volatile ConfigurationPropertyName[] configurationPropertyNames;

    /* JADX INFO: Access modifiers changed from: package-private */
    public SpringIterableConfigurationPropertySource(EnumerablePropertySource<?> propertySource, PropertyMapper... mappers) {
        super(propertySource, mappers);
        assertEnumerablePropertySource();
        this.ancestorOfCheck = getAncestorOfCheck(mappers);
        this.cache = new SoftReferenceConfigurationPropertyCache<>(isImmutablePropertySource());
    }

    private BiPredicate<ConfigurationPropertyName, ConfigurationPropertyName> getAncestorOfCheck(PropertyMapper[] mappers) {
        BiPredicate<ConfigurationPropertyName, ConfigurationPropertyName> ancestorOfCheck = mappers[0].getAncestorOfCheck();
        for (int i = 1; i < mappers.length; i++) {
            ancestorOfCheck = ancestorOfCheck.or(mappers[i].getAncestorOfCheck());
        }
        return ancestorOfCheck;
    }

    private void assertEnumerablePropertySource() {
        EnumerablePropertySource<?> propertySource = getPropertySource();
        if (propertySource instanceof MapPropertySource) {
            MapPropertySource mapSource = (MapPropertySource) propertySource;
            try {
                mapSource.getSource().size();
            } catch (UnsupportedOperationException e) {
                throw new IllegalArgumentException("PropertySource must be fully enumerable");
            }
        }
    }

    @Override // org.springframework.boot.context.properties.source.CachingConfigurationPropertySource
    public ConfigurationPropertyCaching getCaching() {
        return this.cache;
    }

    @Override // org.springframework.boot.context.properties.source.SpringConfigurationPropertySource, org.springframework.boot.context.properties.source.ConfigurationPropertySource
    public ConfigurationProperty getConfigurationProperty(ConfigurationPropertyName name) {
        if (name == null) {
            return null;
        }
        ConfigurationProperty configurationProperty = super.getConfigurationProperty(name);
        if (configurationProperty != null) {
            return configurationProperty;
        }
        for (String candidate : getMappings().getMapped(name)) {
            Object value = getPropertySource().getProperty(candidate);
            if (value != null) {
                Origin origin = PropertySourceOrigin.get(getPropertySource(), candidate);
                return ConfigurationProperty.of(this, name, value, origin);
            }
        }
        return null;
    }

    @Override // org.springframework.boot.context.properties.source.IterableConfigurationPropertySource
    public Stream<ConfigurationPropertyName> stream() {
        ConfigurationPropertyName[] names = getConfigurationPropertyNames();
        return Arrays.stream(names).filter((v0) -> {
            return Objects.nonNull(v0);
        });
    }

    @Override // org.springframework.boot.context.properties.source.IterableConfigurationPropertySource, java.lang.Iterable
    public Iterator<ConfigurationPropertyName> iterator() {
        return new ConfigurationPropertyNamesIterator(getConfigurationPropertyNames());
    }

    @Override // org.springframework.boot.context.properties.source.SpringConfigurationPropertySource, org.springframework.boot.context.properties.source.ConfigurationPropertySource
    public ConfigurationPropertyState containsDescendantOf(ConfigurationPropertyName name) {
        ConfigurationPropertyState result = super.containsDescendantOf(name);
        if (result != ConfigurationPropertyState.UNKNOWN) {
            return result;
        }
        if (this.ancestorOfCheck == PropertyMapper.DEFAULT_ANCESTOR_OF_CHECK) {
            return getMappings().containsDescendantOf(name, this.ancestorOfCheck);
        }
        ConfigurationPropertyName[] candidates = getConfigurationPropertyNames();
        for (ConfigurationPropertyName candidate : candidates) {
            if (candidate != null && this.ancestorOfCheck.test(name, candidate)) {
                return ConfigurationPropertyState.PRESENT;
            }
        }
        return ConfigurationPropertyState.ABSENT;
    }

    private ConfigurationPropertyName[] getConfigurationPropertyNames() {
        if (!isImmutablePropertySource()) {
            return getMappings().getConfigurationPropertyNames(getPropertySource().getPropertyNames());
        }
        ConfigurationPropertyName[] configurationPropertyNames = this.configurationPropertyNames;
        if (configurationPropertyNames == null) {
            configurationPropertyNames = getMappings().getConfigurationPropertyNames(getPropertySource().getPropertyNames());
            this.configurationPropertyNames = configurationPropertyNames;
        }
        return configurationPropertyNames;
    }

    private Mappings getMappings() {
        return this.cache.get(this::createMappings, this::updateMappings);
    }

    private Mappings createMappings() {
        return new Mappings(getMappers(), isImmutablePropertySource(), this.ancestorOfCheck == PropertyMapper.DEFAULT_ANCESTOR_OF_CHECK);
    }

    private Mappings updateMappings(Mappings mappings) {
        EnumerablePropertySource<?> propertySource = getPropertySource();
        Objects.requireNonNull(propertySource);
        mappings.updateMappings(propertySource::getPropertyNames);
        return mappings;
    }

    /* JADX WARN: Multi-variable type inference failed */
    private boolean isImmutablePropertySource() {
        EnumerablePropertySource<?> propertySource = getPropertySource();
        if (propertySource instanceof OriginLookup) {
            return ((OriginLookup) propertySource).isImmutable();
        }
        return "systemEnvironment".equals(propertySource.getName()) && propertySource.getSource() == System.getenv();
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // org.springframework.boot.context.properties.source.SpringConfigurationPropertySource
    public EnumerablePropertySource<?> getPropertySource() {
        return (EnumerablePropertySource) super.getPropertySource();
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: server.jar:BOOT-INF/lib/spring-boot-3.2.2.jar:org/springframework/boot/context/properties/source/SpringIterableConfigurationPropertySource$Mappings.class */
    public static class Mappings {
        private static final ConfigurationPropertyName[] EMPTY_NAMES_ARRAY = new ConfigurationPropertyName[0];
        private final PropertyMapper[] mappers;
        private final boolean immutable;
        private final boolean trackDescendants;
        private volatile Map<ConfigurationPropertyName, Set<String>> mappings;
        private volatile Map<String, ConfigurationPropertyName> reverseMappings;
        private volatile Map<ConfigurationPropertyName, Set<ConfigurationPropertyName>> descendants;
        private volatile ConfigurationPropertyName[] configurationPropertyNames;
        private volatile String[] lastUpdated;

        Mappings(PropertyMapper[] mappers, boolean immutable, boolean trackDescendants) {
            this.mappers = mappers;
            this.immutable = immutable;
            this.trackDescendants = trackDescendants;
        }

        void updateMappings(Supplier<String[]> propertyNames) {
            int i;
            if (this.mappings == null || !this.immutable) {
                int count = 0;
                do {
                    try {
                        updateMappings(propertyNames.get());
                        return;
                    } catch (ConcurrentModificationException ex) {
                        i = count;
                        count++;
                    }
                } while (i <= 10);
                throw ex;
            }
        }

        private void updateMappings(String[] propertyNames) {
            ConfigurationPropertyName configurationPropertyName;
            String[] lastUpdated = this.lastUpdated;
            if (lastUpdated != null && Arrays.equals(lastUpdated, propertyNames)) {
                return;
            }
            int size = propertyNames.length;
            Map<ConfigurationPropertyName, Set<String>> mappings = cloneOrCreate(this.mappings, size);
            Map<String, ConfigurationPropertyName> reverseMappings = cloneOrCreate(this.reverseMappings, size);
            Map<ConfigurationPropertyName, Set<ConfigurationPropertyName>> descendants = cloneOrCreate(this.descendants, size);
            for (PropertyMapper propertyMapper : this.mappers) {
                for (String propertyName : propertyNames) {
                    if (!reverseMappings.containsKey(propertyName) && (configurationPropertyName = propertyMapper.map(propertyName)) != null && !configurationPropertyName.isEmpty()) {
                        add(mappings, configurationPropertyName, propertyName);
                        reverseMappings.put(propertyName, configurationPropertyName);
                        if (this.trackDescendants) {
                            addParents(descendants, configurationPropertyName);
                        }
                    }
                }
            }
            this.mappings = mappings;
            this.reverseMappings = reverseMappings;
            this.descendants = descendants;
            this.lastUpdated = this.immutable ? null : propertyNames;
            this.configurationPropertyNames = this.immutable ? (ConfigurationPropertyName[]) reverseMappings.values().toArray(new ConfigurationPropertyName[0]) : null;
        }

        private <K, V> Map<K, V> cloneOrCreate(Map<K, V> source, int size) {
            return source != null ? new LinkedHashMap(source) : new LinkedHashMap(size);
        }

        private void addParents(Map<ConfigurationPropertyName, Set<ConfigurationPropertyName>> descendants, ConfigurationPropertyName name) {
            ConfigurationPropertyName configurationPropertyName = name;
            while (true) {
                ConfigurationPropertyName parent = configurationPropertyName;
                if (!parent.isEmpty()) {
                    add(descendants, parent, name);
                    configurationPropertyName = parent.getParent();
                } else {
                    return;
                }
            }
        }

        private <K, T> void add(Map<K, Set<T>> map, K key, T value) {
            map.computeIfAbsent(key, k -> {
                return new HashSet();
            }).add(value);
        }

        Set<String> getMapped(ConfigurationPropertyName configurationPropertyName) {
            return this.mappings.getOrDefault(configurationPropertyName, Collections.emptySet());
        }

        ConfigurationPropertyName[] getConfigurationPropertyNames(String[] propertyNames) {
            ConfigurationPropertyName[] names = this.configurationPropertyNames;
            if (names != null) {
                return names;
            }
            Map<String, ConfigurationPropertyName> reverseMappings = this.reverseMappings;
            if (reverseMappings == null || reverseMappings.isEmpty()) {
                return EMPTY_NAMES_ARRAY;
            }
            ConfigurationPropertyName[] names2 = new ConfigurationPropertyName[propertyNames.length];
            for (int i = 0; i < propertyNames.length; i++) {
                names2[i] = reverseMappings.get(propertyNames[i]);
            }
            return names2;
        }

        ConfigurationPropertyState containsDescendantOf(ConfigurationPropertyName name, BiPredicate<ConfigurationPropertyName, ConfigurationPropertyName> ancestorOfCheck) {
            if (name.isEmpty() && !this.descendants.isEmpty()) {
                return ConfigurationPropertyState.PRESENT;
            }
            Set<ConfigurationPropertyName> candidates = this.descendants.getOrDefault(name, Collections.emptySet());
            for (ConfigurationPropertyName candidate : candidates) {
                if (ancestorOfCheck.test(name, candidate)) {
                    return ConfigurationPropertyState.PRESENT;
                }
            }
            return ConfigurationPropertyState.ABSENT;
        }
    }

    /* loaded from: server.jar:BOOT-INF/lib/spring-boot-3.2.2.jar:org/springframework/boot/context/properties/source/SpringIterableConfigurationPropertySource$ConfigurationPropertyNamesIterator.class */
    private static class ConfigurationPropertyNamesIterator implements Iterator<ConfigurationPropertyName> {
        private final ConfigurationPropertyName[] names;
        private int index = 0;

        ConfigurationPropertyNamesIterator(ConfigurationPropertyName[] names) {
            this.names = names;
        }

        @Override // java.util.Iterator
        public boolean hasNext() {
            skipNulls();
            return this.index < this.names.length;
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // java.util.Iterator
        public ConfigurationPropertyName next() {
            skipNulls();
            if (this.index >= this.names.length) {
                throw new NoSuchElementException();
            }
            ConfigurationPropertyName[] configurationPropertyNameArr = this.names;
            int i = this.index;
            this.index = i + 1;
            return configurationPropertyNameArr[i];
        }

        private void skipNulls() {
            while (this.index < this.names.length && this.names[this.index] == null) {
                this.index++;
            }
        }
    }
}
