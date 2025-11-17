package org.springframework.beans;

import java.beans.BeanInfo;
import java.beans.IntrospectionException;
import java.beans.PropertyDescriptor;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.net.URL;
import java.security.ProtectionDomain;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.core.io.support.SpringFactoriesLoader;
import org.springframework.lang.Nullable;
import org.springframework.util.ClassUtils;
import org.springframework.util.ConcurrentReferenceHashMap;
import org.springframework.util.StringUtils;

/* loaded from: agent.jar:BOOT-INF/lib/spring-beans-6.1.3.jar:org/springframework/beans/CachedIntrospectionResults.class */
public final class CachedIntrospectionResults {
    private static final List<BeanInfoFactory> beanInfoFactories = SpringFactoriesLoader.loadFactories(BeanInfoFactory.class, CachedIntrospectionResults.class.getClassLoader());
    private static final SimpleBeanInfoFactory simpleBeanInfoFactory = new SimpleBeanInfoFactory();
    private static final Log logger = LogFactory.getLog((Class<?>) CachedIntrospectionResults.class);
    static final Set<ClassLoader> acceptedClassLoaders = Collections.newSetFromMap(new ConcurrentHashMap(16));
    static final ConcurrentMap<Class<?>, CachedIntrospectionResults> strongClassCache = new ConcurrentHashMap(64);
    static final ConcurrentMap<Class<?>, CachedIntrospectionResults> softClassCache = new ConcurrentReferenceHashMap(64);
    private final BeanInfo beanInfo;
    private final Map<String, PropertyDescriptor> propertyDescriptors;

    public static void acceptClassLoader(@Nullable ClassLoader classLoader) {
        if (classLoader != null) {
            acceptedClassLoaders.add(classLoader);
        }
    }

    public static void clearClassLoader(@Nullable ClassLoader classLoader) {
        acceptedClassLoaders.removeIf(registeredLoader -> {
            return isUnderneathClassLoader(registeredLoader, classLoader);
        });
        strongClassCache.keySet().removeIf(beanClass -> {
            return isUnderneathClassLoader(beanClass.getClassLoader(), classLoader);
        });
        softClassCache.keySet().removeIf(beanClass2 -> {
            return isUnderneathClassLoader(beanClass2.getClassLoader(), classLoader);
        });
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static CachedIntrospectionResults forClass(Class<?> beanClass) throws BeansException {
        ConcurrentMap<Class<?>, CachedIntrospectionResults> classCacheToUse;
        CachedIntrospectionResults results = strongClassCache.get(beanClass);
        if (results != null) {
            return results;
        }
        CachedIntrospectionResults results2 = softClassCache.get(beanClass);
        if (results2 != null) {
            return results2;
        }
        CachedIntrospectionResults results3 = new CachedIntrospectionResults(beanClass);
        if (ClassUtils.isCacheSafe(beanClass, CachedIntrospectionResults.class.getClassLoader()) || isClassLoaderAccepted(beanClass.getClassLoader())) {
            classCacheToUse = strongClassCache;
        } else {
            if (logger.isDebugEnabled()) {
                logger.debug("Not strongly caching class [" + beanClass.getName() + "] because it is not cache-safe");
            }
            classCacheToUse = softClassCache;
        }
        CachedIntrospectionResults existing = classCacheToUse.putIfAbsent(beanClass, results3);
        return existing != null ? existing : results3;
    }

    private static boolean isClassLoaderAccepted(ClassLoader classLoader) {
        for (ClassLoader acceptedLoader : acceptedClassLoaders) {
            if (isUnderneathClassLoader(classLoader, acceptedLoader)) {
                return true;
            }
        }
        return false;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static boolean isUnderneathClassLoader(@Nullable ClassLoader candidate, @Nullable ClassLoader parent) {
        if (candidate == parent) {
            return true;
        }
        if (candidate == null) {
            return false;
        }
        ClassLoader classLoaderToCheck = candidate;
        while (classLoaderToCheck != null) {
            classLoaderToCheck = classLoaderToCheck.getParent();
            if (classLoaderToCheck == parent) {
                return true;
            }
        }
        return false;
    }

    private static BeanInfo getBeanInfo(Class<?> beanClass) throws IntrospectionException {
        for (BeanInfoFactory beanInfoFactory : beanInfoFactories) {
            BeanInfo beanInfo = beanInfoFactory.getBeanInfo(beanClass);
            if (beanInfo != null) {
                return beanInfo;
            }
        }
        return simpleBeanInfoFactory.getBeanInfo(beanClass);
    }

    private CachedIntrospectionResults(Class<?> beanClass) throws BeansException {
        try {
            if (logger.isTraceEnabled()) {
                logger.trace("Getting BeanInfo for class [" + beanClass.getName() + "]");
            }
            this.beanInfo = getBeanInfo(beanClass);
            if (logger.isTraceEnabled()) {
                logger.trace("Caching PropertyDescriptors for class [" + beanClass.getName() + "]");
            }
            this.propertyDescriptors = new LinkedHashMap();
            Set<String> readMethodNames = new HashSet<>();
            PropertyDescriptor[] pds = this.beanInfo.getPropertyDescriptors();
            for (PropertyDescriptor pd : pds) {
                if ((Class.class != beanClass || "name".equals(pd.getName()) || (pd.getName().endsWith("Name") && String.class == pd.getPropertyType())) && ((URL.class != beanClass || !"content".equals(pd.getName())) && (pd.getWriteMethod() != null || !isInvalidReadOnlyPropertyType(pd.getPropertyType(), beanClass)))) {
                    if (logger.isTraceEnabled()) {
                        logger.trace("Found bean property '" + pd.getName() + "'" + (pd.getPropertyType() != null ? " of type [" + pd.getPropertyType().getName() + "]" : "") + (pd.getPropertyEditorClass() != null ? "; editor [" + pd.getPropertyEditorClass().getName() + "]" : ""));
                    }
                    PropertyDescriptor pd2 = buildGenericTypeAwarePropertyDescriptor(beanClass, pd);
                    this.propertyDescriptors.put(pd2.getName(), pd2);
                    Method readMethod = pd2.getReadMethod();
                    if (readMethod != null) {
                        readMethodNames.add(readMethod.getName());
                    }
                }
            }
            for (Class<?> currClass = beanClass; currClass != null && currClass != Object.class; currClass = currClass.getSuperclass()) {
                introspectInterfaces(beanClass, currClass, readMethodNames);
            }
            introspectPlainAccessors(beanClass, readMethodNames);
        } catch (IntrospectionException ex) {
            throw new FatalBeanException("Failed to obtain BeanInfo for class [" + beanClass.getName() + "]", ex);
        }
    }

    private void introspectInterfaces(Class<?> beanClass, Class<?> currClass, Set<String> readMethodNames) throws IntrospectionException {
        for (Class<?> ifc : currClass.getInterfaces()) {
            if (!ClassUtils.isJavaLanguageInterface(ifc)) {
                for (PropertyDescriptor pd : getBeanInfo(ifc).getPropertyDescriptors()) {
                    PropertyDescriptor existingPd = this.propertyDescriptors.get(pd.getName());
                    if (existingPd == null || (existingPd.getReadMethod() == null && pd.getReadMethod() != null)) {
                        PropertyDescriptor pd2 = buildGenericTypeAwarePropertyDescriptor(beanClass, pd);
                        if (pd2.getWriteMethod() != null || !isInvalidReadOnlyPropertyType(pd2.getPropertyType(), beanClass)) {
                            this.propertyDescriptors.put(pd2.getName(), pd2);
                            Method readMethod = pd2.getReadMethod();
                            if (readMethod != null) {
                                readMethodNames.add(readMethod.getName());
                            }
                        }
                    }
                }
                introspectInterfaces(ifc, ifc, readMethodNames);
            }
        }
    }

    private void introspectPlainAccessors(Class<?> beanClass, Set<String> readMethodNames) throws IntrospectionException {
        for (Method method : beanClass.getMethods()) {
            if (!this.propertyDescriptors.containsKey(method.getName()) && !readMethodNames.contains(method.getName()) && isPlainAccessor(method)) {
                this.propertyDescriptors.put(method.getName(), new GenericTypeAwarePropertyDescriptor(beanClass, method.getName(), method, null, null));
                readMethodNames.add(method.getName());
            }
        }
    }

    private boolean isPlainAccessor(Method method) {
        if (Modifier.isStatic(method.getModifiers()) || method.getDeclaringClass() == Object.class || method.getDeclaringClass() == Class.class || method.getParameterCount() > 0 || method.getReturnType() == Void.TYPE || isInvalidReadOnlyPropertyType(method.getReturnType(), method.getDeclaringClass())) {
            return false;
        }
        try {
            method.getDeclaringClass().getDeclaredField(method.getName());
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    private boolean isInvalidReadOnlyPropertyType(@Nullable Class<?> returnType, Class<?> beanClass) {
        return returnType != null && (ClassLoader.class.isAssignableFrom(returnType) || ProtectionDomain.class.isAssignableFrom(returnType) || (AutoCloseable.class.isAssignableFrom(returnType) && !AutoCloseable.class.isAssignableFrom(beanClass)));
    }

    BeanInfo getBeanInfo() {
        return this.beanInfo;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public Class<?> getBeanClass() {
        return this.beanInfo.getBeanDescriptor().getBeanClass();
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    @Nullable
    public PropertyDescriptor getPropertyDescriptor(String name) {
        PropertyDescriptor pd = this.propertyDescriptors.get(name);
        if (pd == null && StringUtils.hasLength(name)) {
            pd = this.propertyDescriptors.get(StringUtils.uncapitalize(name));
            if (pd == null) {
                pd = this.propertyDescriptors.get(StringUtils.capitalize(name));
            }
        }
        return pd;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public PropertyDescriptor[] getPropertyDescriptors() {
        return (PropertyDescriptor[]) this.propertyDescriptors.values().toArray(PropertyDescriptorUtils.EMPTY_PROPERTY_DESCRIPTOR_ARRAY);
    }

    private PropertyDescriptor buildGenericTypeAwarePropertyDescriptor(Class<?> beanClass, PropertyDescriptor pd) {
        try {
            return new GenericTypeAwarePropertyDescriptor(beanClass, pd.getName(), pd.getReadMethod(), pd.getWriteMethod(), pd.getPropertyEditorClass());
        } catch (IntrospectionException ex) {
            throw new FatalBeanException("Failed to re-introspect class [" + beanClass.getName() + "]", ex);
        }
    }
}
