package org.springframework.beans.factory.support;

import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Stream;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanCreationException;
import org.springframework.beans.factory.BeanFactoryUtils;
import org.springframework.beans.factory.BeanIsNotAFactoryException;
import org.springframework.beans.factory.BeanNotOfRequiredTypeException;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.ListableBeanFactory;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.beans.factory.NoUniqueBeanDefinitionException;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.core.OrderComparator;
import org.springframework.core.ResolvableType;
import org.springframework.core.annotation.AnnotatedElementUtils;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;

/* loaded from: server.jar:BOOT-INF/lib/spring-beans-6.1.3.jar:org/springframework/beans/factory/support/StaticListableBeanFactory.class */
public class StaticListableBeanFactory implements ListableBeanFactory {
    private final Map<String, Object> beans;

    public StaticListableBeanFactory() {
        this.beans = new LinkedHashMap();
    }

    public StaticListableBeanFactory(Map<String, Object> beans) {
        Assert.notNull(beans, "Beans Map must not be null");
        this.beans = beans;
    }

    public void addBean(String name, Object bean) {
        this.beans.put(name, bean);
    }

    @Override // org.springframework.beans.factory.BeanFactory
    public Object getBean(String name) throws BeansException {
        String beanName = BeanFactoryUtils.transformedBeanName(name);
        Object bean = this.beans.get(beanName);
        if (bean == null) {
            throw new NoSuchBeanDefinitionException(beanName, "Defined beans are [" + StringUtils.collectionToCommaDelimitedString(this.beans.keySet()) + "]");
        }
        if (BeanFactoryUtils.isFactoryDereference(name) && !(bean instanceof FactoryBean)) {
            throw new BeanIsNotAFactoryException(beanName, bean.getClass());
        }
        if (bean instanceof FactoryBean) {
            FactoryBean<?> factoryBean = (FactoryBean) bean;
            if (!BeanFactoryUtils.isFactoryDereference(name)) {
                try {
                    Object exposedObject = factoryBean.getObject();
                    if (exposedObject == null) {
                        throw new BeanCreationException(beanName, "FactoryBean exposed null object");
                    }
                    return exposedObject;
                } catch (Exception ex) {
                    throw new BeanCreationException(beanName, "FactoryBean threw exception on object creation", ex);
                }
            }
        }
        return bean;
    }

    @Override // org.springframework.beans.factory.BeanFactory
    public <T> T getBean(String str, @Nullable Class<T> cls) throws BeansException {
        T t = (T) getBean(str);
        if (cls != null && !cls.isInstance(t)) {
            throw new BeanNotOfRequiredTypeException(str, cls, t.getClass());
        }
        return t;
    }

    @Override // org.springframework.beans.factory.BeanFactory
    public Object getBean(String name, Object... args) throws BeansException {
        if (!ObjectUtils.isEmpty(args)) {
            throw new UnsupportedOperationException("StaticListableBeanFactory does not support explicit bean creation arguments");
        }
        return getBean(name);
    }

    @Override // org.springframework.beans.factory.BeanFactory
    public <T> T getBean(Class<T> cls) throws BeansException {
        String[] beanNamesForType = getBeanNamesForType((Class<?>) cls);
        if (beanNamesForType.length == 1) {
            return (T) getBean(beanNamesForType[0], cls);
        }
        if (beanNamesForType.length > 1) {
            throw new NoUniqueBeanDefinitionException((Class<?>) cls, beanNamesForType);
        }
        throw new NoSuchBeanDefinitionException((Class<?>) cls);
    }

    @Override // org.springframework.beans.factory.BeanFactory
    public <T> T getBean(Class<T> cls, Object... objArr) throws BeansException {
        if (!ObjectUtils.isEmpty(objArr)) {
            throw new UnsupportedOperationException("StaticListableBeanFactory does not support explicit bean creation arguments");
        }
        return (T) getBean(cls);
    }

    @Override // org.springframework.beans.factory.BeanFactory
    public <T> ObjectProvider<T> getBeanProvider(Class<T> requiredType) throws BeansException {
        return getBeanProvider(ResolvableType.forRawClass(requiredType), true);
    }

    @Override // org.springframework.beans.factory.BeanFactory
    public <T> ObjectProvider<T> getBeanProvider(ResolvableType requiredType) {
        return getBeanProvider(requiredType, true);
    }

    @Override // org.springframework.beans.factory.BeanFactory
    public boolean containsBean(String name) {
        return this.beans.containsKey(name);
    }

    @Override // org.springframework.beans.factory.BeanFactory
    public boolean isSingleton(String name) throws NoSuchBeanDefinitionException {
        Object bean = getBean(name);
        if (bean instanceof FactoryBean) {
            FactoryBean<?> factoryBean = (FactoryBean) bean;
            return factoryBean.isSingleton();
        }
        return true;
    }

    /* JADX WARN: Code restructure failed: missing block: B:4:0x001a, code lost:            if (r0.isPrototype() == false) goto L6;     */
    @Override // org.springframework.beans.factory.BeanFactory
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct code enable 'Show inconsistent code' option in preferences
    */
    public boolean isPrototype(java.lang.String r4) throws org.springframework.beans.factory.NoSuchBeanDefinitionException {
        /*
            r3 = this;
            r0 = r3
            r1 = r4
            java.lang.Object r0 = r0.getBean(r1)
            r5 = r0
            r0 = r5
            boolean r0 = r0 instanceof org.springframework.beans.factory.SmartFactoryBean
            if (r0 == 0) goto L1d
            r0 = r5
            org.springframework.beans.factory.SmartFactoryBean r0 = (org.springframework.beans.factory.SmartFactoryBean) r0
            r7 = r0
            r0 = r7
            boolean r0 = r0.isPrototype()
            if (r0 != 0) goto L32
        L1d:
            r0 = r5
            boolean r0 = r0 instanceof org.springframework.beans.factory.FactoryBean
            if (r0 == 0) goto L36
            r0 = r5
            org.springframework.beans.factory.FactoryBean r0 = (org.springframework.beans.factory.FactoryBean) r0
            r6 = r0
            r0 = r6
            boolean r0 = r0.isSingleton()
            if (r0 != 0) goto L36
        L32:
            r0 = 1
            goto L37
        L36:
            r0 = 0
        L37:
            return r0
        */
        throw new UnsupportedOperationException("Method not decompiled: org.springframework.beans.factory.support.StaticListableBeanFactory.isPrototype(java.lang.String):boolean");
    }

    @Override // org.springframework.beans.factory.BeanFactory
    public boolean isTypeMatch(String name, ResolvableType typeToMatch) throws NoSuchBeanDefinitionException {
        Class<?> type = getType(name);
        return type != null && typeToMatch.isAssignableFrom(type);
    }

    @Override // org.springframework.beans.factory.BeanFactory
    public boolean isTypeMatch(String name, @Nullable Class<?> typeToMatch) throws NoSuchBeanDefinitionException {
        Class<?> type = getType(name);
        return typeToMatch == null || (type != null && typeToMatch.isAssignableFrom(type));
    }

    @Override // org.springframework.beans.factory.BeanFactory
    public Class<?> getType(String name) throws NoSuchBeanDefinitionException {
        return getType(name, true);
    }

    @Override // org.springframework.beans.factory.BeanFactory
    public Class<?> getType(String name, boolean allowFactoryBeanInit) throws NoSuchBeanDefinitionException {
        String beanName = BeanFactoryUtils.transformedBeanName(name);
        Object bean = this.beans.get(beanName);
        if (bean == null) {
            throw new NoSuchBeanDefinitionException(beanName, "Defined beans are [" + StringUtils.collectionToCommaDelimitedString(this.beans.keySet()) + "]");
        }
        if (bean instanceof FactoryBean) {
            FactoryBean<?> factoryBean = (FactoryBean) bean;
            if (!BeanFactoryUtils.isFactoryDereference(name)) {
                return factoryBean.getObjectType();
            }
        }
        return bean.getClass();
    }

    @Override // org.springframework.beans.factory.BeanFactory
    public String[] getAliases(String name) {
        return new String[0];
    }

    @Override // org.springframework.beans.factory.ListableBeanFactory, org.springframework.beans.factory.support.BeanDefinitionRegistry
    public boolean containsBeanDefinition(String name) {
        return this.beans.containsKey(name);
    }

    @Override // org.springframework.beans.factory.ListableBeanFactory, org.springframework.beans.factory.support.BeanDefinitionRegistry
    public int getBeanDefinitionCount() {
        return this.beans.size();
    }

    @Override // org.springframework.beans.factory.ListableBeanFactory, org.springframework.beans.factory.support.BeanDefinitionRegistry
    public String[] getBeanDefinitionNames() {
        return StringUtils.toStringArray(this.beans.keySet());
    }

    @Override // org.springframework.beans.factory.ListableBeanFactory
    public <T> ObjectProvider<T> getBeanProvider(Class<T> requiredType, boolean allowEagerInit) {
        return getBeanProvider(ResolvableType.forRawClass(requiredType), allowEagerInit);
    }

    @Override // org.springframework.beans.factory.ListableBeanFactory
    public <T> ObjectProvider<T> getBeanProvider(final ResolvableType requiredType, boolean allowEagerInit) {
        return new ObjectProvider<T>() { // from class: org.springframework.beans.factory.support.StaticListableBeanFactory.1
            @Override // org.springframework.beans.factory.ObjectFactory
            public T getObject() throws BeansException {
                String[] beanNamesForType = StaticListableBeanFactory.this.getBeanNamesForType(requiredType);
                if (beanNamesForType.length == 1) {
                    return (T) StaticListableBeanFactory.this.getBean(beanNamesForType[0], requiredType);
                }
                if (beanNamesForType.length > 1) {
                    throw new NoUniqueBeanDefinitionException(requiredType, beanNamesForType);
                }
                throw new NoSuchBeanDefinitionException(requiredType);
            }

            @Override // org.springframework.beans.factory.ObjectProvider
            public T getObject(Object... objArr) throws BeansException {
                String[] beanNamesForType = StaticListableBeanFactory.this.getBeanNamesForType(requiredType);
                if (beanNamesForType.length == 1) {
                    return (T) StaticListableBeanFactory.this.getBean(beanNamesForType[0], objArr);
                }
                if (beanNamesForType.length > 1) {
                    throw new NoUniqueBeanDefinitionException(requiredType, beanNamesForType);
                }
                throw new NoSuchBeanDefinitionException(requiredType);
            }

            @Override // org.springframework.beans.factory.ObjectProvider
            @Nullable
            public T getIfAvailable() throws BeansException {
                String[] beanNamesForType = StaticListableBeanFactory.this.getBeanNamesForType(requiredType);
                if (beanNamesForType.length == 1) {
                    return (T) StaticListableBeanFactory.this.getBean(beanNamesForType[0]);
                }
                if (beanNamesForType.length > 1) {
                    throw new NoUniqueBeanDefinitionException(requiredType, beanNamesForType);
                }
                return null;
            }

            @Override // org.springframework.beans.factory.ObjectProvider
            @Nullable
            public T getIfUnique() throws BeansException {
                String[] beanNamesForType = StaticListableBeanFactory.this.getBeanNamesForType(requiredType);
                if (beanNamesForType.length == 1) {
                    return (T) StaticListableBeanFactory.this.getBean(beanNamesForType[0]);
                }
                return null;
            }

            @Override // org.springframework.beans.factory.ObjectProvider
            public Stream<T> stream() {
                return Arrays.stream(StaticListableBeanFactory.this.getBeanNamesForType(requiredType)).map(name -> {
                    return StaticListableBeanFactory.this.getBean(name);
                });
            }

            @Override // org.springframework.beans.factory.ObjectProvider
            public Stream<T> orderedStream() {
                return stream().sorted(OrderComparator.INSTANCE);
            }
        };
    }

    @Override // org.springframework.beans.factory.ListableBeanFactory
    public String[] getBeanNamesForType(@Nullable ResolvableType type) {
        return getBeanNamesForType(type, true, true);
    }

    @Override // org.springframework.beans.factory.ListableBeanFactory
    public String[] getBeanNamesForType(@Nullable ResolvableType type, boolean includeNonSingletons, boolean allowEagerInit) {
        Class<?> resolved = type != null ? type.resolve() : null;
        boolean isFactoryType = resolved != null && FactoryBean.class.isAssignableFrom(resolved);
        List<String> matches = new ArrayList<>();
        for (Map.Entry<String, Object> entry : this.beans.entrySet()) {
            String beanName = entry.getKey();
            Object beanInstance = entry.getValue();
            if (beanInstance instanceof FactoryBean) {
                FactoryBean<?> factoryBean = (FactoryBean) beanInstance;
                if (!isFactoryType) {
                    Class<?> objectType = factoryBean.getObjectType();
                    if (includeNonSingletons || factoryBean.isSingleton()) {
                        if (objectType != null && (type == null || type.isAssignableFrom(objectType))) {
                            matches.add(beanName);
                        }
                    }
                }
            }
            if (type == null || type.isInstance(beanInstance)) {
                matches.add(beanName);
            }
        }
        return StringUtils.toStringArray(matches);
    }

    @Override // org.springframework.beans.factory.ListableBeanFactory
    public String[] getBeanNamesForType(@Nullable Class<?> type) {
        return getBeanNamesForType(ResolvableType.forClass(type));
    }

    @Override // org.springframework.beans.factory.ListableBeanFactory
    public String[] getBeanNamesForType(@Nullable Class<?> type, boolean includeNonSingletons, boolean allowEagerInit) {
        return getBeanNamesForType(ResolvableType.forClass(type), includeNonSingletons, allowEagerInit);
    }

    @Override // org.springframework.beans.factory.ListableBeanFactory
    public <T> Map<String, T> getBeansOfType(@Nullable Class<T> type) throws BeansException {
        return getBeansOfType(type, true, true);
    }

    @Override // org.springframework.beans.factory.ListableBeanFactory
    public <T> Map<String, T> getBeansOfType(@Nullable Class<T> type, boolean includeNonSingletons, boolean allowEagerInit) throws BeansException {
        boolean isFactoryType = type != null && FactoryBean.class.isAssignableFrom(type);
        LinkedHashMap linkedHashMap = new LinkedHashMap();
        for (Map.Entry<String, Object> entry : this.beans.entrySet()) {
            String beanName = entry.getKey();
            Object beanInstance = entry.getValue();
            if (beanInstance instanceof FactoryBean) {
                FactoryBean<?> factoryBean = (FactoryBean) beanInstance;
                if (!isFactoryType) {
                    Class<?> objectType = factoryBean.getObjectType();
                    if (includeNonSingletons || factoryBean.isSingleton()) {
                        if (objectType != null && (type == null || type.isAssignableFrom(objectType))) {
                            linkedHashMap.put(beanName, getBean(beanName, type));
                        }
                    }
                }
            }
            if (type == null || type.isInstance(beanInstance)) {
                if (isFactoryType) {
                    beanName = "&" + beanName;
                }
                linkedHashMap.put(beanName, beanInstance);
            }
        }
        return linkedHashMap;
    }

    @Override // org.springframework.beans.factory.ListableBeanFactory
    public String[] getBeanNamesForAnnotation(Class<? extends Annotation> annotationType) {
        List<String> results = new ArrayList<>();
        for (String beanName : this.beans.keySet()) {
            if (findAnnotationOnBean(beanName, annotationType) != null) {
                results.add(beanName);
            }
        }
        return StringUtils.toStringArray(results);
    }

    @Override // org.springframework.beans.factory.ListableBeanFactory
    public Map<String, Object> getBeansWithAnnotation(Class<? extends Annotation> annotationType) throws BeansException {
        Map<String, Object> results = new LinkedHashMap<>();
        for (String beanName : this.beans.keySet()) {
            if (findAnnotationOnBean(beanName, annotationType) != null) {
                results.put(beanName, getBean(beanName));
            }
        }
        return results;
    }

    @Override // org.springframework.beans.factory.ListableBeanFactory
    @Nullable
    public <A extends Annotation> A findAnnotationOnBean(String str, Class<A> cls) throws NoSuchBeanDefinitionException {
        return (A) findAnnotationOnBean(str, cls, true);
    }

    @Override // org.springframework.beans.factory.ListableBeanFactory
    @Nullable
    public <A extends Annotation> A findAnnotationOnBean(String str, Class<A> cls, boolean z) throws NoSuchBeanDefinitionException {
        Class<?> type = getType(str, z);
        if (type != null) {
            return (A) AnnotatedElementUtils.findMergedAnnotation(type, cls);
        }
        return null;
    }

    @Override // org.springframework.beans.factory.ListableBeanFactory
    public <A extends Annotation> Set<A> findAllAnnotationsOnBean(String beanName, Class<A> annotationType, boolean allowFactoryBeanInit) throws NoSuchBeanDefinitionException {
        Class<?> beanType = getType(beanName, allowFactoryBeanInit);
        return beanType != null ? AnnotatedElementUtils.findAllMergedAnnotations(beanType, annotationType) : Collections.emptySet();
    }
}
