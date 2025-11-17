package org.springframework.boot.diagnostics.analyzer;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.TreeSet;
import java.util.function.Function;
import java.util.stream.Stream;
import org.springframework.boot.context.properties.source.ConfigurationPropertySources;
import org.springframework.boot.context.properties.source.MutuallyExclusiveConfigurationPropertiesException;
import org.springframework.boot.diagnostics.AbstractFailureAnalyzer;
import org.springframework.boot.diagnostics.FailureAnalysis;
import org.springframework.boot.origin.Origin;
import org.springframework.boot.origin.OriginLookup;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.Environment;
import org.springframework.core.env.PropertySource;

/* loaded from: server.jar:BOOT-INF/lib/spring-boot-3.2.2.jar:org/springframework/boot/diagnostics/analyzer/MutuallyExclusiveConfigurationPropertiesFailureAnalyzer.class */
class MutuallyExclusiveConfigurationPropertiesFailureAnalyzer extends AbstractFailureAnalyzer<MutuallyExclusiveConfigurationPropertiesException> {
    private final ConfigurableEnvironment environment;

    MutuallyExclusiveConfigurationPropertiesFailureAnalyzer(Environment environment) {
        this.environment = (ConfigurableEnvironment) environment;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // org.springframework.boot.diagnostics.AbstractFailureAnalyzer
    public FailureAnalysis analyze(Throwable rootFailure, MutuallyExclusiveConfigurationPropertiesException cause) {
        List<Descriptor> descriptors = new ArrayList<>();
        for (String name : cause.getConfiguredNames()) {
            List<Descriptor> descriptorsForName = getDescriptors(name);
            if (descriptorsForName.isEmpty()) {
                return null;
            }
            descriptors.addAll(descriptorsForName);
        }
        StringBuilder description = new StringBuilder();
        appendDetails(description, cause, descriptors);
        return new FailureAnalysis(description.toString(), "Update your configuration so that only one of the mutually exclusive properties is configured.", cause);
    }

    private List<Descriptor> getDescriptors(String propertyName) {
        return getPropertySources().filter(source -> {
            return source.containsProperty(propertyName);
        }).map(source2 -> {
            return Descriptor.get(source2, propertyName);
        }).toList();
    }

    private Stream<PropertySource<?>> getPropertySources() {
        if (this.environment == null) {
            return Stream.empty();
        }
        return this.environment.getPropertySources().stream().filter(source -> {
            return !ConfigurationPropertySources.isAttachedConfigurationPropertySource(source);
        });
    }

    private void appendDetails(StringBuilder message, MutuallyExclusiveConfigurationPropertiesException cause, List<Descriptor> descriptors) {
        descriptors.sort(Comparator.comparing(descriptor -> {
            return descriptor.propertyName;
        }));
        message.append(String.format("The following configuration properties are mutually exclusive:%n%n", new Object[0]));
        sortedStrings(cause.getMutuallyExclusiveNames()).forEach(name -> {
            message.append(String.format("\t%s%n", name));
        });
        message.append(String.format("%n", new Object[0]));
        message.append(String.format("However, more than one of those properties has been configured at the same time:%n%n", new Object[0]));
        Set<String> configuredDescriptions = sortedStrings(descriptors, descriptor2 -> {
            Object[] objArr = new Object[2];
            objArr[0] = descriptor2.propertyName;
            objArr[1] = descriptor2.origin != null ? " (originating from '" + descriptor2.origin + "')" : "";
            return String.format("\t%s%s%n", objArr);
        });
        Objects.requireNonNull(message);
        configuredDescriptions.forEach(message::append);
    }

    private Set<String> sortedStrings(Collection<String> input) {
        return sortedStrings(input, Function.identity());
    }

    private <S> Set<String> sortedStrings(Collection<S> input, Function<S, String> converter) {
        TreeSet<String> results = new TreeSet<>();
        for (S item : input) {
            results.add(converter.apply(item));
        }
        return results;
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: server.jar:BOOT-INF/lib/spring-boot-3.2.2.jar:org/springframework/boot/diagnostics/analyzer/MutuallyExclusiveConfigurationPropertiesFailureAnalyzer$Descriptor.class */
    public static final class Descriptor {
        private final String propertyName;
        private final Origin origin;

        private Descriptor(String propertyName, Origin origin) {
            this.propertyName = propertyName;
            this.origin = origin;
        }

        /* JADX INFO: Access modifiers changed from: package-private */
        public static Descriptor get(PropertySource<?> source, String propertyName) {
            Origin origin = OriginLookup.getOrigin(source, propertyName);
            return new Descriptor(propertyName, origin);
        }
    }
}
