package org.springframework.aot.hint;

import java.util.Comparator;
import java.util.List;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import org.springframework.cglib.core.Constants;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;

/* loaded from: agent.jar:BOOT-INF/lib/spring-core-6.1.3.jar:org/springframework/aot/hint/ExecutableHint.class */
public final class ExecutableHint extends MemberHint implements Comparable<ExecutableHint> {
    private final List<TypeReference> parameterTypes;
    private final ExecutableMode mode;

    private ExecutableHint(Builder builder) {
        super(builder.name);
        this.parameterTypes = List.copyOf(builder.parameterTypes);
        this.mode = builder.mode != null ? builder.mode : ExecutableMode.INVOKE;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static Builder ofConstructor(List<TypeReference> parameterTypes) {
        return new Builder(Constants.CONSTRUCTOR_NAME, parameterTypes);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static Builder ofMethod(String name, List<TypeReference> parameterTypes) {
        return new Builder(name, parameterTypes);
    }

    public List<TypeReference> getParameterTypes() {
        return this.parameterTypes;
    }

    public ExecutableMode getMode() {
        return this.mode;
    }

    public static Consumer<Builder> builtWith(ExecutableMode mode) {
        return builder -> {
            builder.withMode(mode);
        };
    }

    @Override // java.lang.Comparable
    public int compareTo(ExecutableHint other) {
        return Comparator.comparing((v0) -> {
            return v0.getName();
        }, (v0, v1) -> {
            return v0.compareToIgnoreCase(v1);
        }).thenComparing((v0) -> {
            return v0.getParameterTypes();
        }, Comparator.comparingInt((v0) -> {
            return v0.size();
        })).thenComparing((v0) -> {
            return v0.getParameterTypes();
        }, (params1, params2) -> {
            String left = (String) params1.stream().map((v0) -> {
                return v0.getCanonicalName();
            }).collect(Collectors.joining(","));
            String right = (String) params2.stream().map((v0) -> {
                return v0.getCanonicalName();
            }).collect(Collectors.joining(","));
            return left.compareTo(right);
        }).compare(this, other);
    }

    /* loaded from: agent.jar:BOOT-INF/lib/spring-core-6.1.3.jar:org/springframework/aot/hint/ExecutableHint$Builder.class */
    public static class Builder {
        private final String name;
        private final List<TypeReference> parameterTypes;

        @Nullable
        private ExecutableMode mode;

        Builder(String name, List<TypeReference> parameterTypes) {
            this.name = name;
            this.parameterTypes = parameterTypes;
        }

        public Builder withMode(ExecutableMode mode) {
            Assert.notNull(mode, "'mode' must not be null");
            if (this.mode == null || !this.mode.includes(mode)) {
                this.mode = mode;
            }
            return this;
        }

        /* JADX INFO: Access modifiers changed from: package-private */
        public ExecutableHint build() {
            return new ExecutableHint(this);
        }
    }
}
