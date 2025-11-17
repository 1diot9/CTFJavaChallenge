package org.springframework.beans.factory.config;

import java.util.LinkedHashSet;
import java.util.Set;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.TypeConverter;
import org.springframework.core.ResolvableType;
import org.springframework.lang.Nullable;

/* loaded from: server.jar:BOOT-INF/lib/spring-beans-6.1.3.jar:org/springframework/beans/factory/config/SetFactoryBean.class */
public class SetFactoryBean extends AbstractFactoryBean<Set<Object>> {

    @Nullable
    private Set<?> sourceSet;

    @Nullable
    private Class<? extends Set> targetSetClass;

    public void setSourceSet(Set<?> sourceSet) {
        this.sourceSet = sourceSet;
    }

    public void setTargetSetClass(@Nullable Class<? extends Set> targetSetClass) {
        if (targetSetClass == null) {
            throw new IllegalArgumentException("'targetSetClass' must not be null");
        }
        if (!Set.class.isAssignableFrom(targetSetClass)) {
            throw new IllegalArgumentException("'targetSetClass' must implement [java.util.Set]");
        }
        this.targetSetClass = targetSetClass;
    }

    @Override // org.springframework.beans.factory.config.AbstractFactoryBean, org.springframework.beans.factory.FactoryBean
    public Class<Set> getObjectType() {
        return Set.class;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    /* JADX WARN: Can't rename method to resolve collision */
    @Override // org.springframework.beans.factory.config.AbstractFactoryBean
    public Set<Object> createInstance() {
        Set<Object> result;
        if (this.sourceSet == null) {
            throw new IllegalArgumentException("'sourceSet' is required");
        }
        if (this.targetSetClass != null) {
            result = (Set) BeanUtils.instantiateClass(this.targetSetClass);
        } else {
            result = new LinkedHashSet<>(this.sourceSet.size());
        }
        Class<?> valueType = null;
        if (this.targetSetClass != null) {
            valueType = ResolvableType.forClass(this.targetSetClass).asCollection().resolveGeneric(new int[0]);
        }
        if (valueType != null) {
            TypeConverter converter = getBeanTypeConverter();
            for (Object elem : this.sourceSet) {
                result.add(converter.convertIfNecessary(elem, valueType));
            }
        } else {
            result.addAll(this.sourceSet);
        }
        return result;
    }
}
