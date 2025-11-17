package org.springframework.aot.hint;

import java.util.Objects;
import org.springframework.lang.Nullable;

/* loaded from: server.jar:BOOT-INF/lib/spring-core-6.1.3.jar:org/springframework/aot/hint/ResourceBundleHint.class */
public final class ResourceBundleHint implements ConditionalHint {
    private final String baseName;

    @Nullable
    private final TypeReference reachableType;

    ResourceBundleHint(Builder builder) {
        this.baseName = builder.baseName;
        this.reachableType = builder.reachableType;
    }

    public String getBaseName() {
        return this.baseName;
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
        ResourceBundleHint that = (ResourceBundleHint) o;
        return this.baseName.equals(that.baseName) && Objects.equals(this.reachableType, that.reachableType);
    }

    public int hashCode() {
        return Objects.hash(this.baseName, this.reachableType);
    }

    /* loaded from: server.jar:BOOT-INF/lib/spring-core-6.1.3.jar:org/springframework/aot/hint/ResourceBundleHint$Builder.class */
    public static class Builder {
        private String baseName;

        @Nullable
        private TypeReference reachableType;

        /* JADX INFO: Access modifiers changed from: package-private */
        public Builder(String baseName) {
            this.baseName = baseName;
        }

        public Builder onReachableType(TypeReference reachableType) {
            this.reachableType = reachableType;
            return this;
        }

        public Builder baseName(String baseName) {
            this.baseName = baseName;
            return this;
        }

        /* JADX INFO: Access modifiers changed from: package-private */
        public ResourceBundleHint build() {
            return new ResourceBundleHint(this);
        }
    }
}
