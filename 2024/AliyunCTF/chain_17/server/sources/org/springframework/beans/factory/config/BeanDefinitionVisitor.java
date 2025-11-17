package org.springframework.beans.factory.config;

import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.springframework.beans.MutablePropertyValues;
import org.springframework.beans.PropertyValue;
import org.springframework.beans.factory.config.ConstructorArgumentValues;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringValueResolver;

/* loaded from: server.jar:BOOT-INF/lib/spring-beans-6.1.3.jar:org/springframework/beans/factory/config/BeanDefinitionVisitor.class */
public class BeanDefinitionVisitor {

    @Nullable
    private StringValueResolver valueResolver;

    public BeanDefinitionVisitor(StringValueResolver valueResolver) {
        Assert.notNull(valueResolver, "StringValueResolver must not be null");
        this.valueResolver = valueResolver;
    }

    protected BeanDefinitionVisitor() {
    }

    public void visitBeanDefinition(BeanDefinition beanDefinition) {
        visitParentName(beanDefinition);
        visitBeanClassName(beanDefinition);
        visitFactoryBeanName(beanDefinition);
        visitFactoryMethodName(beanDefinition);
        visitScope(beanDefinition);
        if (beanDefinition.hasPropertyValues()) {
            visitPropertyValues(beanDefinition.getPropertyValues());
        }
        if (beanDefinition.hasConstructorArgumentValues()) {
            ConstructorArgumentValues cas = beanDefinition.getConstructorArgumentValues();
            visitIndexedArgumentValues(cas.getIndexedArgumentValues());
            visitGenericArgumentValues(cas.getGenericArgumentValues());
        }
    }

    protected void visitParentName(BeanDefinition beanDefinition) {
        String parentName = beanDefinition.getParentName();
        if (parentName != null) {
            String resolvedName = resolveStringValue(parentName);
            if (!parentName.equals(resolvedName)) {
                beanDefinition.setParentName(resolvedName);
            }
        }
    }

    protected void visitBeanClassName(BeanDefinition beanDefinition) {
        String beanClassName = beanDefinition.getBeanClassName();
        if (beanClassName != null) {
            String resolvedName = resolveStringValue(beanClassName);
            if (!beanClassName.equals(resolvedName)) {
                beanDefinition.setBeanClassName(resolvedName);
            }
        }
    }

    protected void visitFactoryBeanName(BeanDefinition beanDefinition) {
        String factoryBeanName = beanDefinition.getFactoryBeanName();
        if (factoryBeanName != null) {
            String resolvedName = resolveStringValue(factoryBeanName);
            if (!factoryBeanName.equals(resolvedName)) {
                beanDefinition.setFactoryBeanName(resolvedName);
            }
        }
    }

    protected void visitFactoryMethodName(BeanDefinition beanDefinition) {
        String factoryMethodName = beanDefinition.getFactoryMethodName();
        if (factoryMethodName != null) {
            String resolvedName = resolveStringValue(factoryMethodName);
            if (!factoryMethodName.equals(resolvedName)) {
                beanDefinition.setFactoryMethodName(resolvedName);
            }
        }
    }

    protected void visitScope(BeanDefinition beanDefinition) {
        String scope = beanDefinition.getScope();
        if (scope != null) {
            String resolvedScope = resolveStringValue(scope);
            if (!scope.equals(resolvedScope)) {
                beanDefinition.setScope(resolvedScope);
            }
        }
    }

    protected void visitPropertyValues(MutablePropertyValues pvs) {
        PropertyValue[] pvArray = pvs.getPropertyValues();
        for (PropertyValue pv : pvArray) {
            Object newVal = resolveValue(pv.getValue());
            if (!ObjectUtils.nullSafeEquals(newVal, pv.getValue())) {
                pvs.add(pv.getName(), newVal);
            }
        }
    }

    protected void visitIndexedArgumentValues(Map<Integer, ConstructorArgumentValues.ValueHolder> ias) {
        for (ConstructorArgumentValues.ValueHolder valueHolder : ias.values()) {
            Object newVal = resolveValue(valueHolder.getValue());
            if (!ObjectUtils.nullSafeEquals(newVal, valueHolder.getValue())) {
                valueHolder.setValue(newVal);
            }
        }
    }

    protected void visitGenericArgumentValues(List<ConstructorArgumentValues.ValueHolder> gas) {
        for (ConstructorArgumentValues.ValueHolder valueHolder : gas) {
            Object newVal = resolveValue(valueHolder.getValue());
            if (!ObjectUtils.nullSafeEquals(newVal, valueHolder.getValue())) {
                valueHolder.setValue(newVal);
            }
        }
    }

    @Nullable
    protected Object resolveValue(@Nullable Object value) {
        if (value instanceof BeanDefinition) {
            BeanDefinition beanDef = (BeanDefinition) value;
            visitBeanDefinition(beanDef);
        } else if (value instanceof BeanDefinitionHolder) {
            BeanDefinitionHolder beanDefHolder = (BeanDefinitionHolder) value;
            visitBeanDefinition(beanDefHolder.getBeanDefinition());
        } else if (value instanceof RuntimeBeanReference) {
            RuntimeBeanReference ref = (RuntimeBeanReference) value;
            String newBeanName = resolveStringValue(ref.getBeanName());
            if (newBeanName == null) {
                return null;
            }
            if (!newBeanName.equals(ref.getBeanName())) {
                return new RuntimeBeanReference(newBeanName);
            }
        } else if (value instanceof RuntimeBeanNameReference) {
            RuntimeBeanNameReference ref2 = (RuntimeBeanNameReference) value;
            String newBeanName2 = resolveStringValue(ref2.getBeanName());
            if (newBeanName2 == null) {
                return null;
            }
            if (!newBeanName2.equals(ref2.getBeanName())) {
                return new RuntimeBeanNameReference(newBeanName2);
            }
        } else if (value instanceof Object[]) {
            Object[] array = (Object[]) value;
            visitArray(array);
        } else if (value instanceof List) {
            List list = (List) value;
            visitList(list);
        } else if (value instanceof Set) {
            Set set = (Set) value;
            visitSet(set);
        } else if (value instanceof Map) {
            Map map = (Map) value;
            visitMap(map);
        } else if (value instanceof TypedStringValue) {
            TypedStringValue typedStringValue = (TypedStringValue) value;
            String stringValue = typedStringValue.getValue();
            if (stringValue != null) {
                String visitedString = resolveStringValue(stringValue);
                typedStringValue.setValue(visitedString);
            }
        } else if (value instanceof String) {
            String strValue = (String) value;
            return resolveStringValue(strValue);
        }
        return value;
    }

    protected void visitArray(Object[] arrayVal) {
        for (int i = 0; i < arrayVal.length; i++) {
            Object elem = arrayVal[i];
            Object newVal = resolveValue(elem);
            if (!ObjectUtils.nullSafeEquals(newVal, elem)) {
                arrayVal[i] = newVal;
            }
        }
    }

    protected void visitList(List listVal) {
        for (int i = 0; i < listVal.size(); i++) {
            Object elem = listVal.get(i);
            Object newVal = resolveValue(elem);
            if (!ObjectUtils.nullSafeEquals(newVal, elem)) {
                listVal.set(i, newVal);
            }
        }
    }

    protected void visitSet(Set setVal) {
        LinkedHashSet linkedHashSet = new LinkedHashSet();
        boolean entriesModified = false;
        Iterator it = setVal.iterator();
        while (it.hasNext()) {
            Object elem = it.next();
            int elemHash = elem != null ? elem.hashCode() : 0;
            Object newVal = resolveValue(elem);
            int newValHash = newVal != null ? newVal.hashCode() : 0;
            linkedHashSet.add(newVal);
            entriesModified = (!entriesModified && newVal == elem && newValHash == elemHash) ? false : true;
        }
        if (entriesModified) {
            setVal.clear();
            setVal.addAll(linkedHashSet);
        }
    }

    protected void visitMap(Map<?, ?> mapVal) {
        Map newContent = new LinkedHashMap();
        boolean entriesModified = false;
        for (Map.Entry entry : mapVal.entrySet()) {
            Object key = entry.getKey();
            int keyHash = key != null ? key.hashCode() : 0;
            Object newKey = resolveValue(key);
            int newKeyHash = newKey != null ? newKey.hashCode() : 0;
            Object val = entry.getValue();
            Object newVal = resolveValue(val);
            newContent.put(newKey, newVal);
            entriesModified = (!entriesModified && newVal == val && newKey == key && newKeyHash == keyHash) ? false : true;
        }
        if (entriesModified) {
            mapVal.clear();
            mapVal.putAll(newContent);
        }
    }

    @Nullable
    protected String resolveStringValue(String strVal) {
        if (this.valueResolver == null) {
            throw new IllegalStateException("No StringValueResolver specified - pass a resolver object into the constructor or override the 'resolveStringValue' method");
        }
        String resolvedValue = this.valueResolver.resolveStringValue(strVal);
        return strVal.equals(resolvedValue) ? strVal : resolvedValue;
    }
}
