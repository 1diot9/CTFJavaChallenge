package org.springframework.aot.hint;

import java.util.Objects;
import org.springframework.lang.Nullable;

/* loaded from: agent.jar:BOOT-INF/lib/spring-core-6.1.3.jar:org/springframework/aot/hint/AbstractTypeReference.class */
public abstract class AbstractTypeReference implements TypeReference {
    private final String packageName;
    private final String simpleName;

    @Nullable
    private final TypeReference enclosingType;

    protected abstract boolean isPrimitive();

    /* JADX INFO: Access modifiers changed from: protected */
    public AbstractTypeReference(String packageName, String simpleName, @Nullable TypeReference enclosingType) {
        this.packageName = packageName;
        this.simpleName = simpleName;
        this.enclosingType = enclosingType;
    }

    @Override // org.springframework.aot.hint.TypeReference
    public String getName() {
        TypeReference enclosingType = getEnclosingType();
        String simpleName = getSimpleName();
        return enclosingType != null ? enclosingType.getName() + "$" + simpleName : addPackageIfNecessary(simpleName);
    }

    @Override // org.springframework.aot.hint.TypeReference
    public String getPackageName() {
        return this.packageName;
    }

    @Override // org.springframework.aot.hint.TypeReference
    public String getSimpleName() {
        return this.simpleName;
    }

    @Override // org.springframework.aot.hint.TypeReference
    @Nullable
    public TypeReference getEnclosingType() {
        return this.enclosingType;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public String addPackageIfNecessary(String part) {
        if (this.packageName.isEmpty() || (this.packageName.equals("java.lang") && isPrimitive())) {
            return part;
        }
        return this.packageName + "." + part;
    }

    @Override // java.lang.Comparable
    public int compareTo(TypeReference other) {
        return getCanonicalName().compareToIgnoreCase(other.getCanonicalName());
    }

    public boolean equals(@Nullable Object other) {
        if (this != other) {
            if (other instanceof TypeReference) {
                TypeReference that = (TypeReference) other;
                if (getCanonicalName().equals(that.getCanonicalName())) {
                }
            }
            return false;
        }
        return true;
    }

    public int hashCode() {
        return Objects.hash(getCanonicalName());
    }

    public String toString() {
        return getCanonicalName();
    }
}
