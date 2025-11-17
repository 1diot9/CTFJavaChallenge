package org.springframework.boot.context.properties.source;

import java.util.Objects;
import java.util.function.Predicate;
import java.util.stream.Stream;

/* loaded from: server.jar:BOOT-INF/lib/spring-boot-3.2.2.jar:org/springframework/boot/context/properties/source/FilteredIterableConfigurationPropertiesSource.class */
class FilteredIterableConfigurationPropertiesSource extends FilteredConfigurationPropertiesSource implements IterableConfigurationPropertySource {
    /* JADX INFO: Access modifiers changed from: package-private */
    public FilteredIterableConfigurationPropertiesSource(IterableConfigurationPropertySource source, Predicate<ConfigurationPropertyName> filter) {
        super(source, filter);
    }

    @Override // org.springframework.boot.context.properties.source.IterableConfigurationPropertySource
    public Stream<ConfigurationPropertyName> stream() {
        return getSource().stream().filter(getFilter());
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // org.springframework.boot.context.properties.source.FilteredConfigurationPropertiesSource
    public IterableConfigurationPropertySource getSource() {
        return (IterableConfigurationPropertySource) super.getSource();
    }

    @Override // org.springframework.boot.context.properties.source.FilteredConfigurationPropertiesSource, org.springframework.boot.context.properties.source.ConfigurationPropertySource
    public ConfigurationPropertyState containsDescendantOf(ConfigurationPropertyName name) {
        Objects.requireNonNull(name);
        return ConfigurationPropertyState.search(this, name::isAncestorOf);
    }
}
