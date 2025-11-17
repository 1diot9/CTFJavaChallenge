package org.springframework.boot.context.properties.source;

import java.util.Iterator;
import java.util.Objects;
import java.util.function.Predicate;
import java.util.stream.Stream;
import org.springframework.util.StringUtils;

/* loaded from: server.jar:BOOT-INF/lib/spring-boot-3.2.2.jar:org/springframework/boot/context/properties/source/IterableConfigurationPropertySource.class */
public interface IterableConfigurationPropertySource extends ConfigurationPropertySource, Iterable<ConfigurationPropertyName> {
    Stream<ConfigurationPropertyName> stream();

    @Override // org.springframework.boot.context.properties.source.ConfigurationPropertySource
    /* bridge */ /* synthetic */ default ConfigurationPropertySource filter(Predicate filter) {
        return filter((Predicate<ConfigurationPropertyName>) filter);
    }

    @Override // java.lang.Iterable
    default Iterator<ConfigurationPropertyName> iterator() {
        return stream().iterator();
    }

    @Override // org.springframework.boot.context.properties.source.ConfigurationPropertySource
    default ConfigurationPropertyState containsDescendantOf(ConfigurationPropertyName name) {
        Objects.requireNonNull(name);
        return ConfigurationPropertyState.search(this, name::isAncestorOf);
    }

    @Override // org.springframework.boot.context.properties.source.ConfigurationPropertySource
    default IterableConfigurationPropertySource filter(Predicate<ConfigurationPropertyName> filter) {
        return new FilteredIterableConfigurationPropertiesSource(this, filter);
    }

    @Override // org.springframework.boot.context.properties.source.ConfigurationPropertySource
    default IterableConfigurationPropertySource withAliases(ConfigurationPropertyNameAliases aliases) {
        return new AliasedIterableConfigurationPropertySource(this, aliases);
    }

    @Override // org.springframework.boot.context.properties.source.ConfigurationPropertySource
    default IterableConfigurationPropertySource withPrefix(String prefix) {
        return StringUtils.hasText(prefix) ? new PrefixedIterableConfigurationPropertySource(this, prefix) : this;
    }
}
