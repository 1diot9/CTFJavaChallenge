package org.springframework.boot.context.properties.source;

import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import org.springframework.util.Assert;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

/* loaded from: agent.jar:BOOT-INF/lib/spring-boot-3.2.2.jar:org/springframework/boot/context/properties/source/ConfigurationPropertyNameAliases.class */
public final class ConfigurationPropertyNameAliases implements Iterable<ConfigurationPropertyName> {
    private final MultiValueMap<ConfigurationPropertyName, ConfigurationPropertyName> aliases = new LinkedMultiValueMap();

    public ConfigurationPropertyNameAliases() {
    }

    public ConfigurationPropertyNameAliases(String name, String... aliases) {
        addAliases(name, aliases);
    }

    public ConfigurationPropertyNameAliases(ConfigurationPropertyName name, ConfigurationPropertyName... aliases) {
        addAliases(name, aliases);
    }

    public void addAliases(String name, String... aliases) {
        Assert.notNull(name, "Name must not be null");
        Assert.notNull(aliases, "Aliases must not be null");
        addAliases(ConfigurationPropertyName.of(name), (ConfigurationPropertyName[]) Arrays.stream(aliases).map((v0) -> {
            return ConfigurationPropertyName.of(v0);
        }).toArray(x$0 -> {
            return new ConfigurationPropertyName[x$0];
        }));
    }

    public void addAliases(ConfigurationPropertyName name, ConfigurationPropertyName... aliases) {
        Assert.notNull(name, "Name must not be null");
        Assert.notNull(aliases, "Aliases must not be null");
        this.aliases.addAll(name, Arrays.asList(aliases));
    }

    public List<ConfigurationPropertyName> getAliases(ConfigurationPropertyName name) {
        return (List) this.aliases.getOrDefault(name, Collections.emptyList());
    }

    public ConfigurationPropertyName getNameForAlias(ConfigurationPropertyName alias) {
        return (ConfigurationPropertyName) this.aliases.entrySet().stream().filter(e -> {
            return ((List) e.getValue()).contains(alias);
        }).map((v0) -> {
            return v0.getKey();
        }).findFirst().orElse(null);
    }

    @Override // java.lang.Iterable
    public Iterator<ConfigurationPropertyName> iterator() {
        return this.aliases.keySet().iterator();
    }
}
