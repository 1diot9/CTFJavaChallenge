package org.springframework.core.type;

import java.lang.reflect.Modifier;
import java.util.LinkedHashSet;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

/* loaded from: server.jar:BOOT-INF/lib/spring-core-6.1.3.jar:org/springframework/core/type/StandardClassMetadata.class */
public class StandardClassMetadata implements ClassMetadata {
    private final Class<?> introspectedClass;

    @Deprecated
    public StandardClassMetadata(Class<?> introspectedClass) {
        Assert.notNull(introspectedClass, "Class must not be null");
        this.introspectedClass = introspectedClass;
    }

    public final Class<?> getIntrospectedClass() {
        return this.introspectedClass;
    }

    @Override // org.springframework.core.type.ClassMetadata
    public String getClassName() {
        return this.introspectedClass.getName();
    }

    @Override // org.springframework.core.type.ClassMetadata
    public boolean isInterface() {
        return this.introspectedClass.isInterface();
    }

    @Override // org.springframework.core.type.ClassMetadata
    public boolean isAnnotation() {
        return this.introspectedClass.isAnnotation();
    }

    @Override // org.springframework.core.type.ClassMetadata
    public boolean isAbstract() {
        return Modifier.isAbstract(this.introspectedClass.getModifiers());
    }

    @Override // org.springframework.core.type.ClassMetadata
    public boolean isFinal() {
        return Modifier.isFinal(this.introspectedClass.getModifiers());
    }

    @Override // org.springframework.core.type.ClassMetadata
    public boolean isIndependent() {
        return !hasEnclosingClass() || (this.introspectedClass.getDeclaringClass() != null && Modifier.isStatic(this.introspectedClass.getModifiers()));
    }

    @Override // org.springframework.core.type.ClassMetadata
    @Nullable
    public String getEnclosingClassName() {
        Class<?> enclosingClass = this.introspectedClass.getEnclosingClass();
        if (enclosingClass != null) {
            return enclosingClass.getName();
        }
        return null;
    }

    @Override // org.springframework.core.type.ClassMetadata
    @Nullable
    public String getSuperClassName() {
        Class<?> superClass = this.introspectedClass.getSuperclass();
        if (superClass != null) {
            return superClass.getName();
        }
        return null;
    }

    @Override // org.springframework.core.type.ClassMetadata
    public String[] getInterfaceNames() {
        Class<?>[] ifcs = this.introspectedClass.getInterfaces();
        String[] ifcNames = new String[ifcs.length];
        for (int i = 0; i < ifcs.length; i++) {
            ifcNames[i] = ifcs[i].getName();
        }
        return ifcNames;
    }

    @Override // org.springframework.core.type.ClassMetadata
    public String[] getMemberClassNames() {
        LinkedHashSet<String> memberClassNames = new LinkedHashSet<>(4);
        for (Class<?> nestedClass : this.introspectedClass.getDeclaredClasses()) {
            memberClassNames.add(nestedClass.getName());
        }
        return StringUtils.toStringArray(memberClassNames);
    }

    public boolean equals(@Nullable Object other) {
        if (this != other) {
            if (other instanceof StandardClassMetadata) {
                StandardClassMetadata that = (StandardClassMetadata) other;
                if (getIntrospectedClass().equals(that.getIntrospectedClass())) {
                }
            }
            return false;
        }
        return true;
    }

    public int hashCode() {
        return getIntrospectedClass().hashCode();
    }

    public String toString() {
        return getClassName();
    }
}
