package org.springframework.boot.context.properties.source;

import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import org.springframework.util.Assert;

/* loaded from: server.jar:BOOT-INF/lib/spring-boot-3.2.2.jar:org/springframework/boot/context/properties/source/MutuallyExclusiveConfigurationPropertiesException.class */
public class MutuallyExclusiveConfigurationPropertiesException extends RuntimeException {
    private final Set<String> configuredNames;
    private final Set<String> mutuallyExclusiveNames;

    public MutuallyExclusiveConfigurationPropertiesException(Collection<String> configuredNames, Collection<String> mutuallyExclusiveNames) {
        this(asSet(configuredNames), asSet(mutuallyExclusiveNames));
    }

    private MutuallyExclusiveConfigurationPropertiesException(Set<String> configuredNames, Set<String> mutuallyExclusiveNames) {
        super(buildMessage(mutuallyExclusiveNames, configuredNames));
        this.configuredNames = configuredNames;
        this.mutuallyExclusiveNames = mutuallyExclusiveNames;
    }

    public Set<String> getConfiguredNames() {
        return this.configuredNames;
    }

    public Set<String> getMutuallyExclusiveNames() {
        return this.mutuallyExclusiveNames;
    }

    private static Set<String> asSet(Collection<String> collection) {
        if (collection != null) {
            return new LinkedHashSet(collection);
        }
        return null;
    }

    private static String buildMessage(Set<String> mutuallyExclusiveNames, Set<String> configuredNames) {
        Assert.isTrue(configuredNames != null && configuredNames.size() > 1, "ConfiguredNames must contain 2 or more names");
        Assert.isTrue(mutuallyExclusiveNames != null && mutuallyExclusiveNames.size() > 1, "MutuallyExclusiveNames must contain 2 or more names");
        return "The configuration properties '" + String.join(", ", mutuallyExclusiveNames) + "' are mutually exclusive and '" + String.join(", ", configuredNames) + "' have been configured together";
    }

    public static void throwIfMultipleNonNullValuesIn(Consumer<Map<String, Object>> entries) {
        Map<String, Object> map = new LinkedHashMap<>();
        entries.accept(map);
        Set<String> configuredNames = (Set) map.entrySet().stream().filter(entry -> {
            return entry.getValue() != null;
        }).map((v0) -> {
            return v0.getKey();
        }).collect(Collectors.toCollection(LinkedHashSet::new));
        if (configuredNames.size() > 1) {
            throw new MutuallyExclusiveConfigurationPropertiesException(configuredNames, map.keySet());
        }
    }
}
