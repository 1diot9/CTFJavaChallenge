package org.springframework.aot.nativex;

import ch.qos.logback.core.joran.conditional.IfAction;
import java.util.Collection;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.stream.Stream;
import org.springframework.aot.hint.ConditionalHint;
import org.springframework.aot.hint.ResourceBundleHint;
import org.springframework.aot.hint.ResourceHints;
import org.springframework.aot.hint.ResourcePatternHint;
import org.springframework.lang.Nullable;

/* JADX INFO: Access modifiers changed from: package-private */
/* loaded from: server.jar:BOOT-INF/lib/spring-core-6.1.3.jar:org/springframework/aot/nativex/ResourceHintsWriter.class */
public class ResourceHintsWriter {
    public static final ResourceHintsWriter INSTANCE = new ResourceHintsWriter();
    private static final Comparator<ResourcePatternHint> RESOURCE_PATTERN_HINT_COMPARATOR = Comparator.comparing((v0) -> {
        return v0.getPattern();
    });
    private static final Comparator<ResourceBundleHint> RESOURCE_BUNDLE_HINT_COMPARATOR = Comparator.comparing((v0) -> {
        return v0.getBaseName();
    });

    ResourceHintsWriter() {
    }

    public void write(BasicJsonWriter writer, ResourceHints hints) {
        Map<String, Object> attributes = new LinkedHashMap<>();
        addIfNotEmpty(attributes, "resources", toAttributes(hints));
        handleResourceBundles(attributes, hints.resourceBundleHints());
        writer.writeObject(attributes);
    }

    private Map<String, Object> toAttributes(ResourceHints hint) {
        Map<String, Object> attributes = new LinkedHashMap<>();
        addIfNotEmpty(attributes, "includes", hint.resourcePatternHints().map((v0) -> {
            return v0.getIncludes();
        }).flatMap((v0) -> {
            return v0.stream();
        }).distinct().sorted(RESOURCE_PATTERN_HINT_COMPARATOR).map(this::toAttributes).toList());
        addIfNotEmpty(attributes, "excludes", hint.resourcePatternHints().map((v0) -> {
            return v0.getExcludes();
        }).flatMap((v0) -> {
            return v0.stream();
        }).distinct().sorted(RESOURCE_PATTERN_HINT_COMPARATOR).map(this::toAttributes).toList());
        return attributes;
    }

    private void handleResourceBundles(Map<String, Object> attributes, Stream<ResourceBundleHint> resourceBundles) {
        addIfNotEmpty(attributes, "bundles", resourceBundles.sorted(RESOURCE_BUNDLE_HINT_COMPARATOR).map(this::toAttributes).toList());
    }

    private Map<String, Object> toAttributes(ResourceBundleHint hint) {
        Map<String, Object> attributes = new LinkedHashMap<>();
        handleCondition(attributes, hint);
        attributes.put("name", hint.getBaseName());
        return attributes;
    }

    private Map<String, Object> toAttributes(ResourcePatternHint hint) {
        Map<String, Object> attributes = new LinkedHashMap<>();
        handleCondition(attributes, hint);
        attributes.put("pattern", hint.toRegex().toString());
        return attributes;
    }

    private void addIfNotEmpty(Map<String, Object> attributes, String name, @Nullable Object value) {
        if (value instanceof Collection) {
            Collection<?> collection = (Collection) value;
            if (!collection.isEmpty()) {
                attributes.put(name, value);
                return;
            }
            return;
        }
        if (value instanceof Map) {
            Map<?, ?> map = (Map) value;
            if (!map.isEmpty()) {
                attributes.put(name, value);
                return;
            }
            return;
        }
        if (value != null) {
            attributes.put(name, value);
        }
    }

    private void handleCondition(Map<String, Object> attributes, ConditionalHint hint) {
        if (hint.getReachableType() != null) {
            Map<String, Object> conditionAttributes = new LinkedHashMap<>();
            conditionAttributes.put("typeReachable", hint.getReachableType());
            attributes.put(IfAction.CONDITION_ATTRIBUTE, conditionAttributes);
        }
    }
}
