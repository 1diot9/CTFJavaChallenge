package org.springframework.core.type.filter;

import org.springframework.lang.Nullable;
import org.springframework.util.ClassUtils;

/* loaded from: agent.jar:BOOT-INF/lib/spring-core-6.1.3.jar:org/springframework/core/type/filter/AssignableTypeFilter.class */
public class AssignableTypeFilter extends AbstractTypeHierarchyTraversingFilter {
    private final Class<?> targetType;

    public AssignableTypeFilter(Class<?> targetType) {
        super(true, true);
        this.targetType = targetType;
    }

    public final Class<?> getTargetType() {
        return this.targetType;
    }

    @Override // org.springframework.core.type.filter.AbstractTypeHierarchyTraversingFilter
    protected boolean matchClassName(String className) {
        return this.targetType.getName().equals(className);
    }

    @Override // org.springframework.core.type.filter.AbstractTypeHierarchyTraversingFilter
    @Nullable
    protected Boolean matchSuperClass(String superClassName) {
        return matchTargetType(superClassName);
    }

    @Override // org.springframework.core.type.filter.AbstractTypeHierarchyTraversingFilter
    @Nullable
    protected Boolean matchInterface(String interfaceName) {
        return matchTargetType(interfaceName);
    }

    @Nullable
    protected Boolean matchTargetType(String typeName) {
        if (this.targetType.getName().equals(typeName)) {
            return true;
        }
        if (Object.class.getName().equals(typeName)) {
            return false;
        }
        if (typeName.startsWith("java")) {
            try {
                Class<?> clazz = ClassUtils.forName(typeName, getClass().getClassLoader());
                return Boolean.valueOf(this.targetType.isAssignableFrom(clazz));
            } catch (Throwable th) {
                return null;
            }
        }
        return null;
    }
}
