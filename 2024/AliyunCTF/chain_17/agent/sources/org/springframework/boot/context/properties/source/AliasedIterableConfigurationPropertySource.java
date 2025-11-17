package org.springframework.boot.context.properties.source;

import java.util.List;
import java.util.stream.Stream;
import org.springframework.util.CollectionUtils;

/* loaded from: agent.jar:BOOT-INF/lib/spring-boot-3.2.2.jar:org/springframework/boot/context/properties/source/AliasedIterableConfigurationPropertySource.class */
class AliasedIterableConfigurationPropertySource extends AliasedConfigurationPropertySource implements IterableConfigurationPropertySource {
    /* JADX INFO: Access modifiers changed from: package-private */
    public AliasedIterableConfigurationPropertySource(IterableConfigurationPropertySource source, ConfigurationPropertyNameAliases aliases) {
        super(source, aliases);
    }

    @Override // org.springframework.boot.context.properties.source.IterableConfigurationPropertySource
    public Stream<ConfigurationPropertyName> stream() {
        return getSource().stream().flatMap(this::addAliases);
    }

    private Stream<ConfigurationPropertyName> addAliases(ConfigurationPropertyName name) {
        Stream<ConfigurationPropertyName> names = Stream.of(name);
        List<ConfigurationPropertyName> aliases = getAliases().getAliases(name);
        if (CollectionUtils.isEmpty(aliases)) {
            return names;
        }
        return Stream.concat(names, aliases.stream());
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // org.springframework.boot.context.properties.source.AliasedConfigurationPropertySource
    public IterableConfigurationPropertySource getSource() {
        return (IterableConfigurationPropertySource) super.getSource();
    }
}
