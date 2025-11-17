package org.springframework.aot.hint.predicate;

import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;
import java.lang.runtime.ObjectMethods;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;
import java.util.regex.Pattern;
import org.springframework.aot.hint.ResourceHints;
import org.springframework.aot.hint.ResourcePatternHint;
import org.springframework.aot.hint.RuntimeHints;
import org.springframework.aot.hint.TypeReference;
import org.springframework.util.Assert;
import org.springframework.util.ConcurrentLruCache;

/* loaded from: agent.jar:BOOT-INF/lib/spring-core-6.1.3.jar:org/springframework/aot/hint/predicate/ResourceHintsPredicates.class */
public class ResourceHintsPredicates {
    private static final ConcurrentLruCache<ResourcePatternHint, Pattern> CACHED_RESOURCE_PATTERNS = new ConcurrentLruCache<>(32, (v0) -> {
        return v0.toRegex();
    });

    public Predicate<RuntimeHints> forBundle(String bundleName) {
        Assert.hasText(bundleName, "resource bundle name should not be empty");
        return runtimeHints -> {
            return runtimeHints.resources().resourceBundleHints().anyMatch(bundleHint -> {
                return bundleName.equals(bundleHint.getBaseName());
            });
        };
    }

    public Predicate<RuntimeHints> forResource(TypeReference type, String resourceName) {
        String absoluteName = resolveAbsoluteResourceName(type, resourceName);
        return forResource(absoluteName);
    }

    private String resolveAbsoluteResourceName(TypeReference type, String resourceName) {
        if (resourceName.startsWith("/")) {
            return resourceName.substring(1);
        }
        if (type.getPackageName().isEmpty()) {
            return resourceName;
        }
        return type.getPackageName().replace('.', '/') + "/" + resourceName;
    }

    public Predicate<RuntimeHints> forResource(String resourceName) {
        String resourceNameToUse = resourceName.startsWith("/") ? resourceName.substring(1) : resourceName;
        return hints -> {
            AggregatedResourcePatternHints aggregatedResourcePatternHints = AggregatedResourcePatternHints.of(hints.resources());
            boolean isExcluded = aggregatedResourcePatternHints.excludes().stream().anyMatch(excluded -> {
                return CACHED_RESOURCE_PATTERNS.get(excluded).matcher(resourceNameToUse).matches();
            });
            if (isExcluded) {
                return false;
            }
            return aggregatedResourcePatternHints.includes().stream().anyMatch(included -> {
                return CACHED_RESOURCE_PATTERNS.get(included).matcher(resourceNameToUse).matches();
            });
        };
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: agent.jar:BOOT-INF/lib/spring-core-6.1.3.jar:org/springframework/aot/hint/predicate/ResourceHintsPredicates$AggregatedResourcePatternHints.class */
    public static final class AggregatedResourcePatternHints extends Record {
        private final List<ResourcePatternHint> includes;
        private final List<ResourcePatternHint> excludes;

        private AggregatedResourcePatternHints(List<ResourcePatternHint> includes, List<ResourcePatternHint> excludes) {
            this.includes = includes;
            this.excludes = excludes;
        }

        @Override // java.lang.Record
        public final String toString() {
            return (String) ObjectMethods.bootstrap(MethodHandles.lookup(), "toString", MethodType.methodType(String.class, AggregatedResourcePatternHints.class), AggregatedResourcePatternHints.class, "includes;excludes", "FIELD:Lorg/springframework/aot/hint/predicate/ResourceHintsPredicates$AggregatedResourcePatternHints;->includes:Ljava/util/List;", "FIELD:Lorg/springframework/aot/hint/predicate/ResourceHintsPredicates$AggregatedResourcePatternHints;->excludes:Ljava/util/List;").dynamicInvoker().invoke(this) /* invoke-custom */;
        }

        @Override // java.lang.Record
        public final int hashCode() {
            return (int) ObjectMethods.bootstrap(MethodHandles.lookup(), "hashCode", MethodType.methodType(Integer.TYPE, AggregatedResourcePatternHints.class), AggregatedResourcePatternHints.class, "includes;excludes", "FIELD:Lorg/springframework/aot/hint/predicate/ResourceHintsPredicates$AggregatedResourcePatternHints;->includes:Ljava/util/List;", "FIELD:Lorg/springframework/aot/hint/predicate/ResourceHintsPredicates$AggregatedResourcePatternHints;->excludes:Ljava/util/List;").dynamicInvoker().invoke(this) /* invoke-custom */;
        }

        @Override // java.lang.Record
        public final boolean equals(Object o) {
            return (boolean) ObjectMethods.bootstrap(MethodHandles.lookup(), "equals", MethodType.methodType(Boolean.TYPE, AggregatedResourcePatternHints.class, Object.class), AggregatedResourcePatternHints.class, "includes;excludes", "FIELD:Lorg/springframework/aot/hint/predicate/ResourceHintsPredicates$AggregatedResourcePatternHints;->includes:Ljava/util/List;", "FIELD:Lorg/springframework/aot/hint/predicate/ResourceHintsPredicates$AggregatedResourcePatternHints;->excludes:Ljava/util/List;").dynamicInvoker().invoke(this, o) /* invoke-custom */;
        }

        public List<ResourcePatternHint> includes() {
            return this.includes;
        }

        public List<ResourcePatternHint> excludes() {
            return this.excludes;
        }

        static AggregatedResourcePatternHints of(ResourceHints resourceHints) {
            List<ResourcePatternHint> includes = new ArrayList<>();
            List<ResourcePatternHint> excludes = new ArrayList<>();
            resourceHints.resourcePatternHints().forEach(resourcePatternHint -> {
                includes.addAll(resourcePatternHint.getIncludes());
                excludes.addAll(resourcePatternHint.getExcludes());
            });
            return new AggregatedResourcePatternHints(includes, excludes);
        }
    }
}
