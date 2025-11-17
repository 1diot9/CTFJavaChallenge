package org.springframework.aot.generate;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import org.springframework.beans.factory.support.PropertiesBeanDefinitionReader;
import org.springframework.javapoet.ClassName;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;
import org.springframework.util.ClassUtils;
import org.springframework.util.StringUtils;

/* loaded from: agent.jar:BOOT-INF/lib/spring-core-6.1.3.jar:org/springframework/aot/generate/ClassNameGenerator.class */
public final class ClassNameGenerator {
    private static final String SEPARATOR = "__";
    private static final String AOT_FEATURE = "Aot";
    private final ClassName defaultTarget;
    private final String featureNamePrefix;
    private final Map<String, AtomicInteger> sequenceGenerator;

    public ClassNameGenerator(ClassName defaultTarget) {
        this(defaultTarget, "");
    }

    public ClassNameGenerator(ClassName defaultTarget, String featureNamePrefix) {
        this(defaultTarget, featureNamePrefix, new ConcurrentHashMap());
    }

    private ClassNameGenerator(ClassName defaultTarget, String featureNamePrefix, Map<String, AtomicInteger> sequenceGenerator) {
        Assert.notNull(defaultTarget, "'defaultTarget' must not be null");
        this.defaultTarget = defaultTarget;
        this.featureNamePrefix = !StringUtils.hasText(featureNamePrefix) ? "" : featureNamePrefix;
        this.sequenceGenerator = sequenceGenerator;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public String getFeatureNamePrefix() {
        return this.featureNamePrefix;
    }

    public ClassName generateClassName(String featureName, @Nullable ClassName target) {
        return generateSequencedClassName(getRootName(featureName, target));
    }

    private String getRootName(String featureName, @Nullable ClassName target) {
        Assert.hasLength(featureName, "'featureName' must not be empty");
        String featureName2 = clean(featureName);
        ClassName targetToUse = target != null ? target : this.defaultTarget;
        String featureNameToUse = this.featureNamePrefix + featureName2;
        return toName(targetToUse).replace(PropertiesBeanDefinitionReader.CONSTRUCTOR_ARG_PREFIX, "_") + "__" + StringUtils.capitalize(featureNameToUse);
    }

    private String clean(String name) {
        boolean z;
        StringBuilder clean = new StringBuilder();
        boolean lastNotLetter = true;
        for (char ch2 : name.toCharArray()) {
            if (!Character.isLetter(ch2)) {
                z = true;
            } else {
                clean.append(lastNotLetter ? Character.toUpperCase(ch2) : ch2);
                z = false;
            }
            lastNotLetter = z;
        }
        return !clean.isEmpty() ? clean.toString() : AOT_FEATURE;
    }

    private ClassName generateSequencedClassName(String name) {
        int sequence = this.sequenceGenerator.computeIfAbsent(name, key -> {
            return new AtomicInteger();
        }).getAndIncrement();
        if (sequence > 0) {
            name = name + sequence;
        }
        return ClassName.get(ClassUtils.getPackageName(name), ClassUtils.getShortName(name), new String[0]);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public ClassNameGenerator withFeatureNamePrefix(String featureNamePrefix) {
        return new ClassNameGenerator(this.defaultTarget, featureNamePrefix, this.sequenceGenerator);
    }

    private static String toName(ClassName className) {
        return GeneratedTypeReference.of(className).getName();
    }
}
