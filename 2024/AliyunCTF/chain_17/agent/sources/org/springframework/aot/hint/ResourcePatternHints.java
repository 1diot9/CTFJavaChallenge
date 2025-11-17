package org.springframework.aot.hint;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Stream;
import org.springframework.lang.Nullable;

/* loaded from: agent.jar:BOOT-INF/lib/spring-core-6.1.3.jar:org/springframework/aot/hint/ResourcePatternHints.class */
public final class ResourcePatternHints {
    private final List<ResourcePatternHint> includes;
    private final List<ResourcePatternHint> excludes;

    private ResourcePatternHints(Builder builder) {
        this.includes = new ArrayList(builder.includes);
        this.excludes = new ArrayList(builder.excludes);
    }

    public List<ResourcePatternHint> getIncludes() {
        return this.includes;
    }

    public List<ResourcePatternHint> getExcludes() {
        return this.excludes;
    }

    /* loaded from: agent.jar:BOOT-INF/lib/spring-core-6.1.3.jar:org/springframework/aot/hint/ResourcePatternHints$Builder.class */
    public static class Builder {
        private final Set<ResourcePatternHint> includes = new LinkedHashSet();
        private final Set<ResourcePatternHint> excludes = new LinkedHashSet();

        public Builder includes(@Nullable TypeReference reachableType, String... includes) {
            Stream map = Arrays.stream(includes).map(this::expandToIncludeDirectories).flatMap((v0) -> {
                return v0.stream();
            }).map(include -> {
                return new ResourcePatternHint(include, reachableType);
            });
            Set<ResourcePatternHint> set = this.includes;
            Objects.requireNonNull(set);
            map.forEach((v1) -> {
                r1.add(v1);
            });
            return this;
        }

        private List<String> expandToIncludeDirectories(String includePattern) {
            if (!includePattern.contains("/")) {
                return List.of("/", includePattern);
            }
            List<String> includePatterns = new ArrayList<>();
            includePatterns.add("/");
            includePatterns.add(includePattern);
            StringBuilder path = new StringBuilder();
            for (String pathElement : includePattern.split("/")) {
                if (!pathElement.isEmpty()) {
                    if (pathElement.contains("*")) {
                        break;
                    }
                    if (!path.isEmpty()) {
                        path.append("/");
                    }
                    path.append(pathElement);
                    includePatterns.add(path.toString());
                }
            }
            return includePatterns;
        }

        public Builder includes(String... includes) {
            return includes(null, includes);
        }

        public Builder excludes(@Nullable TypeReference reachableType, String... excludes) {
            List<ResourcePatternHint> newExcludes = Arrays.stream(excludes).map(include -> {
                return new ResourcePatternHint(include, reachableType);
            }).toList();
            this.excludes.addAll(newExcludes);
            return this;
        }

        public Builder excludes(String... excludes) {
            return excludes(null, excludes);
        }

        /* JADX INFO: Access modifiers changed from: package-private */
        public ResourcePatternHints build() {
            return new ResourcePatternHints(this);
        }
    }
}
