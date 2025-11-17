package org.springframework.beans.factory.support;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.function.BiFunction;
import org.springframework.beans.BeanWrapper;
import org.springframework.beans.BeanWrapperImpl;
import org.springframework.beans.BeansException;
import org.springframework.beans.TypeConverter;
import org.springframework.beans.factory.BeanCreationException;
import org.springframework.beans.factory.BeanDefinitionStoreException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.BeanDefinitionHolder;
import org.springframework.beans.factory.config.DependencyDescriptor;
import org.springframework.beans.factory.config.NamedBeanHolder;
import org.springframework.beans.factory.config.RuntimeBeanNameReference;
import org.springframework.beans.factory.config.RuntimeBeanReference;
import org.springframework.beans.factory.config.TypedStringValue;
import org.springframework.lang.Nullable;
import org.springframework.util.ClassUtils;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;

/* loaded from: agent.jar:BOOT-INF/lib/spring-beans-6.1.3.jar:org/springframework/beans/factory/support/BeanDefinitionValueResolver.class */
public class BeanDefinitionValueResolver {
    private final AbstractAutowireCapableBeanFactory beanFactory;
    private final String beanName;
    private final BeanDefinition beanDefinition;
    private final TypeConverter typeConverter;

    public BeanDefinitionValueResolver(AbstractAutowireCapableBeanFactory beanFactory, String beanName, BeanDefinition beanDefinition, TypeConverter typeConverter) {
        this.beanFactory = beanFactory;
        this.beanName = beanName;
        this.beanDefinition = beanDefinition;
        this.typeConverter = typeConverter;
    }

    public BeanDefinitionValueResolver(AbstractAutowireCapableBeanFactory beanFactory, String beanName, BeanDefinition beanDefinition) {
        this.beanFactory = beanFactory;
        this.beanName = beanName;
        this.beanDefinition = beanDefinition;
        BeanWrapper beanWrapper = new BeanWrapperImpl();
        beanFactory.initBeanWrapper(beanWrapper);
        this.typeConverter = beanWrapper;
    }

    @Nullable
    public Object resolveValueIfNecessary(Object argName, @Nullable Object value) {
        if (value instanceof RuntimeBeanReference) {
            RuntimeBeanReference ref = (RuntimeBeanReference) value;
            return resolveReference(argName, ref);
        }
        if (value instanceof RuntimeBeanNameReference) {
            RuntimeBeanNameReference ref2 = (RuntimeBeanNameReference) value;
            String refName = String.valueOf(doEvaluate(ref2.getBeanName()));
            if (!this.beanFactory.containsBean(refName)) {
                throw new BeanDefinitionStoreException("Invalid bean name '" + refName + "' in bean reference for " + argName);
            }
            return refName;
        }
        if (value instanceof BeanDefinitionHolder) {
            BeanDefinitionHolder bdHolder = (BeanDefinitionHolder) value;
            return resolveInnerBean(bdHolder.getBeanName(), bdHolder.getBeanDefinition(), (name, mbd) -> {
                return resolveInnerBeanValue(argName, name, mbd);
            });
        }
        if (value instanceof BeanDefinition) {
            BeanDefinition bd = (BeanDefinition) value;
            return resolveInnerBean(null, bd, (name2, mbd2) -> {
                return resolveInnerBeanValue(argName, name2, mbd2);
            });
        }
        if (value instanceof DependencyDescriptor) {
            DependencyDescriptor dependencyDescriptor = (DependencyDescriptor) value;
            Set<String> autowiredBeanNames = new LinkedHashSet<>(2);
            Object result = this.beanFactory.resolveDependency(dependencyDescriptor, this.beanName, autowiredBeanNames, this.typeConverter);
            for (String autowiredBeanName : autowiredBeanNames) {
                if (this.beanFactory.containsBean(autowiredBeanName)) {
                    this.beanFactory.registerDependentBean(autowiredBeanName, this.beanName);
                }
            }
            return result;
        }
        if (value instanceof ManagedArray) {
            ManagedArray managedArray = (ManagedArray) value;
            Class<?> elementType = managedArray.resolvedElementType;
            if (elementType == null) {
                String elementTypeName = managedArray.getElementTypeName();
                if (StringUtils.hasText(elementTypeName)) {
                    try {
                        elementType = ClassUtils.forName(elementTypeName, this.beanFactory.getBeanClassLoader());
                        managedArray.resolvedElementType = elementType;
                    } catch (Throwable ex) {
                        throw new BeanCreationException(this.beanDefinition.getResourceDescription(), this.beanName, "Error resolving array type for " + argName, ex);
                    }
                } else {
                    elementType = Object.class;
                }
            }
            return resolveManagedArray(argName, (List) value, elementType);
        }
        if (value instanceof ManagedList) {
            ManagedList<?> managedList = (ManagedList) value;
            return resolveManagedList(argName, managedList);
        }
        if (value instanceof ManagedSet) {
            ManagedSet<?> managedSet = (ManagedSet) value;
            return resolveManagedSet(argName, managedSet);
        }
        if (value instanceof ManagedMap) {
            ManagedMap<?, ?> managedMap = (ManagedMap) value;
            return resolveManagedMap(argName, managedMap);
        }
        if (value instanceof ManagedProperties) {
            ManagedProperties original = (ManagedProperties) value;
            Properties copy = new Properties();
            original.forEach((propKey, propValue) -> {
                if (propKey instanceof TypedStringValue) {
                    TypedStringValue typedStringValue = (TypedStringValue) propKey;
                    propKey = evaluate(typedStringValue);
                }
                if (propValue instanceof TypedStringValue) {
                    TypedStringValue typedStringValue2 = (TypedStringValue) propValue;
                    propValue = evaluate(typedStringValue2);
                }
                if (propKey == null || propValue == null) {
                    throw new BeanCreationException(this.beanDefinition.getResourceDescription(), this.beanName, "Error converting Properties key/value pair for " + argName + ": resolved to null");
                }
                copy.put(propKey, propValue);
            });
            return copy;
        }
        if (value instanceof TypedStringValue) {
            TypedStringValue typedStringValue = (TypedStringValue) value;
            Object valueObject = evaluate(typedStringValue);
            try {
                Class<?> resolvedTargetType = resolveTargetType(typedStringValue);
                if (resolvedTargetType != null) {
                    return this.typeConverter.convertIfNecessary(valueObject, resolvedTargetType);
                }
                return valueObject;
            } catch (Throwable ex2) {
                throw new BeanCreationException(this.beanDefinition.getResourceDescription(), this.beanName, "Error converting typed String value for " + argName, ex2);
            }
        }
        if (value instanceof NullBean) {
            return null;
        }
        return evaluate(value);
    }

    public <T> T resolveInnerBean(@Nullable String innerBeanName, BeanDefinition innerBd, BiFunction<String, RootBeanDefinition, T> resolver) {
        String nameToUse = innerBeanName != null ? innerBeanName : "(inner bean)#" + ObjectUtils.getIdentityHexString(innerBd);
        return resolver.apply(nameToUse, this.beanFactory.getMergedBeanDefinition(nameToUse, innerBd, this.beanDefinition));
    }

    @Nullable
    protected Object evaluate(TypedStringValue value) {
        Object result = doEvaluate(value.getValue());
        if (!ObjectUtils.nullSafeEquals(result, value.getValue())) {
            value.setDynamic();
        }
        return result;
    }

    @Nullable
    protected Object evaluate(@Nullable Object value) {
        if (value instanceof String) {
            String str = (String) value;
            return doEvaluate(str);
        }
        if (value instanceof String[]) {
            String[] values = (String[]) value;
            boolean actuallyResolved = false;
            Object[] resolvedValues = new Object[values.length];
            for (int i = 0; i < values.length; i++) {
                String originalValue = values[i];
                Object resolvedValue = doEvaluate(originalValue);
                if (resolvedValue != originalValue) {
                    actuallyResolved = true;
                }
                resolvedValues[i] = resolvedValue;
            }
            return actuallyResolved ? resolvedValues : values;
        }
        return value;
    }

    @Nullable
    private Object doEvaluate(@Nullable String value) {
        return this.beanFactory.evaluateBeanDefinitionString(value, this.beanDefinition);
    }

    @Nullable
    protected Class<?> resolveTargetType(TypedStringValue value) throws ClassNotFoundException {
        if (value.hasTargetType()) {
            return value.getTargetType();
        }
        return value.resolveTargetType(this.beanFactory.getBeanClassLoader());
    }

    @Nullable
    private Object resolveReference(Object argName, RuntimeBeanReference ref) {
        String resolvedName;
        Object bean;
        try {
            Class<?> beanType = ref.getBeanType();
            if (ref.isToParent()) {
                BeanFactory parent = this.beanFactory.getParentBeanFactory();
                if (parent == null) {
                    throw new BeanCreationException(this.beanDefinition.getResourceDescription(), this.beanName, "Cannot resolve reference to bean " + ref + " in parent factory: no parent factory available");
                }
                if (beanType != null) {
                    bean = parent.getBean(beanType);
                } else {
                    bean = parent.getBean(String.valueOf(doEvaluate(ref.getBeanName())));
                }
            } else {
                if (beanType != null) {
                    NamedBeanHolder<?> namedBean = this.beanFactory.resolveNamedBean(beanType);
                    bean = namedBean.getBeanInstance();
                    resolvedName = namedBean.getBeanName();
                } else {
                    resolvedName = String.valueOf(doEvaluate(ref.getBeanName()));
                    bean = this.beanFactory.getBean(resolvedName);
                }
                this.beanFactory.registerDependentBean(resolvedName, this.beanName);
            }
            if (bean instanceof NullBean) {
                bean = null;
            }
            return bean;
        } catch (BeansException ex) {
            throw new BeanCreationException(this.beanDefinition.getResourceDescription(), this.beanName, "Cannot resolve reference to bean '" + ref.getBeanName() + "' while setting " + argName, ex);
        }
    }

    @Nullable
    private Object resolveInnerBeanValue(Object argName, String innerBeanName, RootBeanDefinition mbd) {
        try {
            String actualInnerBeanName = innerBeanName;
            if (mbd.isSingleton()) {
                actualInnerBeanName = adaptInnerBeanName(innerBeanName);
            }
            this.beanFactory.registerContainedBean(actualInnerBeanName, this.beanName);
            String[] dependsOn = mbd.getDependsOn();
            if (dependsOn != null) {
                for (String dependsOnBean : dependsOn) {
                    this.beanFactory.registerDependentBean(dependsOnBean, actualInnerBeanName);
                    this.beanFactory.getBean(dependsOnBean);
                }
            }
            Object innerBean = this.beanFactory.createBean(actualInnerBeanName, mbd, (Object[]) null);
            if (innerBean instanceof FactoryBean) {
                FactoryBean<?> factoryBean = (FactoryBean) innerBean;
                boolean synthetic = mbd.isSynthetic();
                innerBean = this.beanFactory.getObjectFromFactoryBean(factoryBean, actualInnerBeanName, !synthetic);
            }
            if (innerBean instanceof NullBean) {
                innerBean = null;
            }
            return innerBean;
        } catch (BeansException ex) {
            throw new BeanCreationException(this.beanDefinition.getResourceDescription(), this.beanName, "Cannot create inner bean '" + innerBeanName + "' " + (mbd.getBeanClassName() != null ? "of type [" + mbd.getBeanClassName() + "] " : "") + "while setting " + argName, ex);
        }
    }

    private String adaptInnerBeanName(String innerBeanName) {
        String actualInnerBeanName = innerBeanName;
        int counter = 0;
        String prefix = innerBeanName + "#";
        while (this.beanFactory.isBeanNameInUse(actualInnerBeanName)) {
            counter++;
            actualInnerBeanName = prefix + counter;
        }
        return actualInnerBeanName;
    }

    private Object resolveManagedArray(Object argName, List<?> ml, Class<?> elementType) {
        Object resolved = Array.newInstance(elementType, ml.size());
        for (int i = 0; i < ml.size(); i++) {
            Array.set(resolved, i, resolveValueIfNecessary(new KeyedArgName(argName, Integer.valueOf(i)), ml.get(i)));
        }
        return resolved;
    }

    private List<?> resolveManagedList(Object argName, List<?> ml) {
        List<Object> resolved = new ArrayList<>(ml.size());
        for (int i = 0; i < ml.size(); i++) {
            resolved.add(resolveValueIfNecessary(new KeyedArgName(argName, Integer.valueOf(i)), ml.get(i)));
        }
        return resolved;
    }

    private Set<?> resolveManagedSet(Object argName, Set<?> ms) {
        Set<Object> resolved = new LinkedHashSet<>(ms.size());
        int i = 0;
        for (Object m : ms) {
            resolved.add(resolveValueIfNecessary(new KeyedArgName(argName, Integer.valueOf(i)), m));
            i++;
        }
        return resolved;
    }

    private Map<?, ?> resolveManagedMap(Object argName, Map<?, ?> mm) {
        Map<Object, Object> resolved = CollectionUtils.newLinkedHashMap(mm.size());
        mm.forEach((key, value) -> {
            Object resolvedKey = resolveValueIfNecessary(argName, key);
            Object resolvedValue = resolveValueIfNecessary(new KeyedArgName(argName, key), value);
            resolved.put(resolvedKey, resolvedValue);
        });
        return resolved;
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: agent.jar:BOOT-INF/lib/spring-beans-6.1.3.jar:org/springframework/beans/factory/support/BeanDefinitionValueResolver$KeyedArgName.class */
    public static class KeyedArgName {
        private final Object argName;
        private final Object key;

        public KeyedArgName(Object argName, Object key) {
            this.argName = argName;
            this.key = key;
        }

        public String toString() {
            return this.argName + " with key [" + this.key + "]";
        }
    }
}
