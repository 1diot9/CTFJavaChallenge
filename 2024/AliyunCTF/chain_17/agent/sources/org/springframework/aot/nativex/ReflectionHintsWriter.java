package org.springframework.aot.nativex;

import ch.qos.logback.core.joran.conditional.IfAction;
import java.util.Collection;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Stream;
import org.springframework.aot.hint.ExecutableHint;
import org.springframework.aot.hint.ExecutableMode;
import org.springframework.aot.hint.FieldHint;
import org.springframework.aot.hint.MemberCategory;
import org.springframework.aot.hint.ReflectionHints;
import org.springframework.aot.hint.TypeHint;
import org.springframework.lang.Nullable;

/* JADX INFO: Access modifiers changed from: package-private */
/* loaded from: agent.jar:BOOT-INF/lib/spring-core-6.1.3.jar:org/springframework/aot/nativex/ReflectionHintsWriter.class */
public class ReflectionHintsWriter {
    public static final ReflectionHintsWriter INSTANCE = new ReflectionHintsWriter();

    ReflectionHintsWriter() {
    }

    public void write(BasicJsonWriter writer, ReflectionHints hints) {
        writer.writeArray(hints.typeHints().sorted(Comparator.comparing((v0) -> {
            return v0.getType();
        })).map(this::toAttributes).toList());
    }

    private Map<String, Object> toAttributes(TypeHint hint) {
        Map<String, Object> attributes = new LinkedHashMap<>();
        attributes.put("name", hint.getType());
        handleCondition(attributes, hint);
        handleCategories(attributes, hint.getMemberCategories());
        handleFields(attributes, hint.fields());
        handleExecutables(attributes, Stream.concat(hint.constructors(), hint.methods()).sorted().toList());
        return attributes;
    }

    private void handleCondition(Map<String, Object> attributes, TypeHint hint) {
        if (hint.getReachableType() != null) {
            Map<String, Object> conditionAttributes = new LinkedHashMap<>();
            conditionAttributes.put("typeReachable", hint.getReachableType());
            attributes.put(IfAction.CONDITION_ATTRIBUTE, conditionAttributes);
        }
    }

    private void handleFields(Map<String, Object> attributes, Stream<FieldHint> fields) {
        addIfNotEmpty(attributes, "fields", fields.sorted(Comparator.comparing((v0) -> {
            return v0.getName();
        }, (v0, v1) -> {
            return v0.compareToIgnoreCase(v1);
        })).map(this::toAttributes).toList());
    }

    private Map<String, Object> toAttributes(FieldHint hint) {
        Map<String, Object> attributes = new LinkedHashMap<>();
        attributes.put("name", hint.getName());
        return attributes;
    }

    private void handleExecutables(Map<String, Object> attributes, List<ExecutableHint> hints) {
        addIfNotEmpty(attributes, "methods", hints.stream().filter(h -> {
            return h.getMode().equals(ExecutableMode.INVOKE);
        }).map(this::toAttributes).toList());
        addIfNotEmpty(attributes, "queriedMethods", hints.stream().filter(h2 -> {
            return h2.getMode().equals(ExecutableMode.INTROSPECT);
        }).map(this::toAttributes).toList());
    }

    private Map<String, Object> toAttributes(ExecutableHint hint) {
        Map<String, Object> attributes = new LinkedHashMap<>();
        attributes.put("name", hint.getName());
        attributes.put("parameterTypes", hint.getParameterTypes());
        return attributes;
    }

    private void handleCategories(Map<String, Object> attributes, Set<MemberCategory> categories) {
        categories.stream().sorted().forEach(category -> {
            switch (category) {
                case PUBLIC_FIELDS:
                    attributes.put("allPublicFields", true);
                    return;
                case DECLARED_FIELDS:
                    attributes.put("allDeclaredFields", true);
                    return;
                case INTROSPECT_PUBLIC_CONSTRUCTORS:
                    attributes.put("queryAllPublicConstructors", true);
                    return;
                case INTROSPECT_DECLARED_CONSTRUCTORS:
                    attributes.put("queryAllDeclaredConstructors", true);
                    return;
                case INVOKE_PUBLIC_CONSTRUCTORS:
                    attributes.put("allPublicConstructors", true);
                    return;
                case INVOKE_DECLARED_CONSTRUCTORS:
                    attributes.put("allDeclaredConstructors", true);
                    return;
                case INTROSPECT_PUBLIC_METHODS:
                    attributes.put("queryAllPublicMethods", true);
                    return;
                case INTROSPECT_DECLARED_METHODS:
                    attributes.put("queryAllDeclaredMethods", true);
                    return;
                case INVOKE_PUBLIC_METHODS:
                    attributes.put("allPublicMethods", true);
                    return;
                case INVOKE_DECLARED_METHODS:
                    attributes.put("allDeclaredMethods", true);
                    return;
                case PUBLIC_CLASSES:
                    attributes.put("allPublicClasses", true);
                    return;
                case DECLARED_CLASSES:
                    attributes.put("allDeclaredClasses", true);
                    return;
                default:
                    return;
            }
        });
    }

    private void addIfNotEmpty(Map<String, Object> attributes, String name, @Nullable Object value) {
        if (value == null || !(value instanceof Collection)) {
            return;
        }
        Collection<?> collection = (Collection) value;
        if (!collection.isEmpty()) {
            attributes.put(name, value);
        }
    }
}
