package org.springframework.aot.hint;

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.StringJoiner;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.springframework.aot.hint.ExecutableHint;
import org.springframework.cglib.core.Constants;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;

/* loaded from: agent.jar:BOOT-INF/lib/spring-core-6.1.3.jar:org/springframework/aot/hint/TypeHint.class */
public final class TypeHint implements ConditionalHint {
    private final TypeReference type;

    @Nullable
    private final TypeReference reachableType;
    private final Set<FieldHint> fields;
    private final Set<ExecutableHint> constructors;
    private final Set<ExecutableHint> methods;
    private final Set<MemberCategory> memberCategories;

    private TypeHint(Builder builder) {
        this.type = builder.type;
        this.reachableType = builder.reachableType;
        this.memberCategories = Set.copyOf(builder.memberCategories);
        this.fields = (Set) builder.fields.stream().map(FieldHint::new).collect(Collectors.toSet());
        this.constructors = (Set) builder.constructors.values().stream().map((v0) -> {
            return v0.build();
        }).collect(Collectors.toSet());
        this.methods = (Set) builder.methods.values().stream().map((v0) -> {
            return v0.build();
        }).collect(Collectors.toSet());
    }

    static Builder of(TypeReference type) {
        Assert.notNull(type, "'type' must not be null");
        return new Builder(type);
    }

    public TypeReference getType() {
        return this.type;
    }

    @Override // org.springframework.aot.hint.ConditionalHint
    @Nullable
    public TypeReference getReachableType() {
        return this.reachableType;
    }

    public Stream<FieldHint> fields() {
        return this.fields.stream();
    }

    public Stream<ExecutableHint> constructors() {
        return this.constructors.stream();
    }

    public Stream<ExecutableHint> methods() {
        return this.methods.stream();
    }

    public Set<MemberCategory> getMemberCategories() {
        return this.memberCategories;
    }

    public String toString() {
        return new StringJoiner(", ", TypeHint.class.getSimpleName() + "[", "]").add("type=" + this.type).toString();
    }

    public static Consumer<Builder> builtWith(MemberCategory... memberCategories) {
        return builder -> {
            builder.withMembers(memberCategories);
        };
    }

    /* loaded from: agent.jar:BOOT-INF/lib/spring-core-6.1.3.jar:org/springframework/aot/hint/TypeHint$Builder.class */
    public static class Builder {
        private final TypeReference type;

        @Nullable
        private TypeReference reachableType;
        private final Set<String> fields = new HashSet();
        private final Map<ExecutableKey, ExecutableHint.Builder> constructors = new HashMap();
        private final Map<ExecutableKey, ExecutableHint.Builder> methods = new HashMap();
        private final Set<MemberCategory> memberCategories = new HashSet();

        /* JADX INFO: Access modifiers changed from: package-private */
        public Builder(TypeReference type) {
            this.type = type;
        }

        public Builder onReachableType(TypeReference reachableType) {
            this.reachableType = reachableType;
            return this;
        }

        public Builder onReachableType(Class<?> reachableType) {
            this.reachableType = TypeReference.of(reachableType);
            return this;
        }

        public Builder withField(String name) {
            this.fields.add(name);
            return this;
        }

        public Builder withConstructor(List<TypeReference> parameterTypes, ExecutableMode mode) {
            return withConstructor(parameterTypes, ExecutableHint.builtWith(mode));
        }

        private Builder withConstructor(List<TypeReference> parameterTypes, Consumer<ExecutableHint.Builder> constructorHint) {
            ExecutableKey key = new ExecutableKey(Constants.CONSTRUCTOR_NAME, parameterTypes);
            ExecutableHint.Builder builder = this.constructors.computeIfAbsent(key, k -> {
                return ExecutableHint.ofConstructor(parameterTypes);
            });
            constructorHint.accept(builder);
            return this;
        }

        public Builder withMethod(String name, List<TypeReference> parameterTypes, ExecutableMode mode) {
            return withMethod(name, parameterTypes, ExecutableHint.builtWith(mode));
        }

        private Builder withMethod(String name, List<TypeReference> parameterTypes, Consumer<ExecutableHint.Builder> methodHint) {
            ExecutableKey key = new ExecutableKey(name, parameterTypes);
            ExecutableHint.Builder builder = this.methods.computeIfAbsent(key, k -> {
                return ExecutableHint.ofMethod(name, parameterTypes);
            });
            methodHint.accept(builder);
            return this;
        }

        public Builder withMembers(MemberCategory... memberCategories) {
            this.memberCategories.addAll(Arrays.asList(memberCategories));
            return this;
        }

        /* JADX INFO: Access modifiers changed from: package-private */
        public TypeHint build() {
            return new TypeHint(this);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: agent.jar:BOOT-INF/lib/spring-core-6.1.3.jar:org/springframework/aot/hint/TypeHint$ExecutableKey.class */
    public static final class ExecutableKey {
        private final String name;
        private final List<String> parameterTypes;

        private ExecutableKey(String name, List<TypeReference> parameterTypes) {
            this.name = name;
            this.parameterTypes = parameterTypes.stream().map((v0) -> {
                return v0.getCanonicalName();
            }).toList();
        }

        public boolean equals(@Nullable Object o) {
            if (this == o) {
                return true;
            }
            if (o == null || getClass() != o.getClass()) {
                return false;
            }
            ExecutableKey that = (ExecutableKey) o;
            return this.name.equals(that.name) && this.parameterTypes.equals(that.parameterTypes);
        }

        public int hashCode() {
            return Objects.hash(this.name, this.parameterTypes);
        }
    }
}
