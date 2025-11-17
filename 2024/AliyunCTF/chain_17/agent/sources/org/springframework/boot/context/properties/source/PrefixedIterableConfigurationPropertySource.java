package org.springframework.boot.context.properties.source;

import java.util.stream.Stream;

/* loaded from: agent.jar:BOOT-INF/lib/spring-boot-3.2.2.jar:org/springframework/boot/context/properties/source/PrefixedIterableConfigurationPropertySource.class */
class PrefixedIterableConfigurationPropertySource extends PrefixedConfigurationPropertySource implements IterableConfigurationPropertySource {
    /* JADX INFO: Access modifiers changed from: package-private */
    public PrefixedIterableConfigurationPropertySource(IterableConfigurationPropertySource source, String prefix) {
        super(source, prefix);
    }

    @Override // org.springframework.boot.context.properties.source.IterableConfigurationPropertySource
    public Stream<ConfigurationPropertyName> stream() {
        return getSource().stream().map(this::stripPrefix);
    }

    private ConfigurationPropertyName stripPrefix(ConfigurationPropertyName name) {
        return getPrefix().isAncestorOf(name) ? name.subName(getPrefix().getNumberOfElements()) : name;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // org.springframework.boot.context.properties.source.PrefixedConfigurationPropertySource
    public IterableConfigurationPropertySource getSource() {
        return (IterableConfigurationPropertySource) super.getSource();
    }
}
