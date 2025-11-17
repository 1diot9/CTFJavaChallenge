package org.springframework.aot.hint;

import java.util.Objects;
import org.springframework.lang.Nullable;

/* loaded from: server.jar:BOOT-INF/lib/spring-core-6.1.3.jar:org/springframework/aot/hint/JavaSerializationHint.class */
public class JavaSerializationHint implements ConditionalHint {
    private final TypeReference type;

    @Nullable
    private final TypeReference reachableType;

    JavaSerializationHint(Builder builder) {
        this.type = builder.type;
        this.reachableType = builder.reachableType;
    }

    public TypeReference getType() {
        return this.type;
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
        JavaSerializationHint that = (JavaSerializationHint) o;
        return this.type.equals(that.type) && Objects.equals(this.reachableType, that.reachableType);
    }

    public int hashCode() {
        return Objects.hash(this.type, this.reachableType);
    }

    /* loaded from: server.jar:BOOT-INF/lib/spring-core-6.1.3.jar:org/springframework/aot/hint/JavaSerializationHint$Builder.class */
    public static class Builder {
        private final TypeReference type;

        @Nullable
        private TypeReference reachableType;

        /* JADX INFO: Access modifiers changed from: package-private */
        public Builder(TypeReference type) {
            this.type = type;
        }

        public Builder onReachableType(TypeReference reachableType) {
            this.reachableType = reachableType;
            return this;
        }

        /* JADX INFO: Access modifiers changed from: package-private */
        public JavaSerializationHint build() {
            return new JavaSerializationHint(this);
        }
    }
}
