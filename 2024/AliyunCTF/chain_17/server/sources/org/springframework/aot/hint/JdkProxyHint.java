package org.springframework.aot.hint;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import org.springframework.lang.Nullable;

/* loaded from: server.jar:BOOT-INF/lib/spring-core-6.1.3.jar:org/springframework/aot/hint/JdkProxyHint.class */
public final class JdkProxyHint implements ConditionalHint {
    private final List<TypeReference> proxiedInterfaces;

    @Nullable
    private final TypeReference reachableType;

    private JdkProxyHint(Builder builder) {
        this.proxiedInterfaces = List.copyOf(builder.proxiedInterfaces);
        this.reachableType = builder.reachableType;
    }

    public static Builder of(TypeReference... proxiedInterfaces) {
        return new Builder().proxiedInterfaces(proxiedInterfaces);
    }

    public static Builder of(Class<?>... proxiedInterfaces) {
        return new Builder().proxiedInterfaces(proxiedInterfaces);
    }

    public List<TypeReference> getProxiedInterfaces() {
        return this.proxiedInterfaces;
    }

    @Override // org.springframework.aot.hint.ConditionalHint
    @Nullable
    public TypeReference getReachableType() {
        return this.reachableType;
    }

    public boolean equals(@Nullable Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        JdkProxyHint that = (JdkProxyHint) o;
        return this.proxiedInterfaces.equals(that.proxiedInterfaces) && Objects.equals(this.reachableType, that.reachableType);
    }

    public int hashCode() {
        return Objects.hash(this.proxiedInterfaces);
    }

    /* loaded from: server.jar:BOOT-INF/lib/spring-core-6.1.3.jar:org/springframework/aot/hint/JdkProxyHint$Builder.class */
    public static class Builder {
        private final LinkedList<TypeReference> proxiedInterfaces = new LinkedList<>();

        @Nullable
        private TypeReference reachableType;

        public Builder proxiedInterfaces(TypeReference... proxiedInterfaces) {
            this.proxiedInterfaces.addAll(Arrays.asList(proxiedInterfaces));
            return this;
        }

        public Builder proxiedInterfaces(Class<?>... proxiedInterfaces) {
            this.proxiedInterfaces.addAll(toTypeReferences(proxiedInterfaces));
            return this;
        }

        public Builder onReachableType(TypeReference reachableType) {
            this.reachableType = reachableType;
            return this;
        }

        /* JADX INFO: Access modifiers changed from: package-private */
        public JdkProxyHint build() {
            return new JdkProxyHint(this);
        }

        private static List<TypeReference> toTypeReferences(Class<?>... proxiedInterfaces) {
            List<String> invalidTypes = Arrays.stream(proxiedInterfaces).filter(candidate -> {
                return !candidate.isInterface() || candidate.isSealed();
            }).map((v0) -> {
                return v0.getName();
            }).toList();
            if (!invalidTypes.isEmpty()) {
                throw new IllegalArgumentException("The following must be non-sealed interfaces: " + invalidTypes);
            }
            return TypeReference.listOf(proxiedInterfaces);
        }
    }
}
