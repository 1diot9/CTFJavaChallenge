package org.springframework.beans.factory.config;

import java.util.ArrayList;
import java.util.List;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.TypeConverter;
import org.springframework.core.ResolvableType;
import org.springframework.lang.Nullable;

/* loaded from: agent.jar:BOOT-INF/lib/spring-beans-6.1.3.jar:org/springframework/beans/factory/config/ListFactoryBean.class */
public class ListFactoryBean extends AbstractFactoryBean<List<Object>> {

    @Nullable
    private List<?> sourceList;

    @Nullable
    private Class<? extends List> targetListClass;

    public void setSourceList(List<?> sourceList) {
        this.sourceList = sourceList;
    }

    public void setTargetListClass(@Nullable Class<? extends List> targetListClass) {
        if (targetListClass == null) {
            throw new IllegalArgumentException("'targetListClass' must not be null");
        }
        if (!List.class.isAssignableFrom(targetListClass)) {
            throw new IllegalArgumentException("'targetListClass' must implement [java.util.List]");
        }
        this.targetListClass = targetListClass;
    }

    @Override // org.springframework.beans.factory.config.AbstractFactoryBean, org.springframework.beans.factory.FactoryBean
    public Class<List> getObjectType() {
        return List.class;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    /* JADX WARN: Can't rename method to resolve collision */
    @Override // org.springframework.beans.factory.config.AbstractFactoryBean
    public List<Object> createInstance() {
        List<Object> result;
        if (this.sourceList == null) {
            throw new IllegalArgumentException("'sourceList' is required");
        }
        if (this.targetListClass != null) {
            result = (List) BeanUtils.instantiateClass(this.targetListClass);
        } else {
            result = new ArrayList<>(this.sourceList.size());
        }
        Class<?> valueType = null;
        if (this.targetListClass != null) {
            valueType = ResolvableType.forClass(this.targetListClass).asCollection().resolveGeneric(new int[0]);
        }
        if (valueType != null) {
            TypeConverter converter = getBeanTypeConverter();
            for (Object elem : this.sourceList) {
                result.add(converter.convertIfNecessary(elem, valueType));
            }
        } else {
            result.addAll(this.sourceList);
        }
        return result;
    }
}
