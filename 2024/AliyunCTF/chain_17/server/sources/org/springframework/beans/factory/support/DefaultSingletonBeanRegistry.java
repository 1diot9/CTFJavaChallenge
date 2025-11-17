package org.springframework.beans.factory.support;

import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import org.springframework.beans.factory.BeanCreationException;
import org.springframework.beans.factory.BeanCreationNotAllowedException;
import org.springframework.beans.factory.BeanCurrentlyInCreationException;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.ObjectFactory;
import org.springframework.beans.factory.config.SingletonBeanRegistry;
import org.springframework.core.SimpleAliasRegistry;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

/* loaded from: server.jar:BOOT-INF/lib/spring-beans-6.1.3.jar:org/springframework/beans/factory/support/DefaultSingletonBeanRegistry.class */
public class DefaultSingletonBeanRegistry extends SimpleAliasRegistry implements SingletonBeanRegistry {
    private static final int SUPPRESSED_EXCEPTIONS_LIMIT = 100;

    @Nullable
    private Set<Exception> suppressedExceptions;
    private final Map<String, Object> singletonObjects = new ConcurrentHashMap(256);
    private final Map<String, ObjectFactory<?>> singletonFactories = new HashMap(16);
    private final Map<String, Object> earlySingletonObjects = new ConcurrentHashMap(16);
    private final Set<String> registeredSingletons = new LinkedHashSet(256);
    private final Set<String> singletonsCurrentlyInCreation = Collections.newSetFromMap(new ConcurrentHashMap(16));
    private final Set<String> inCreationCheckExclusions = Collections.newSetFromMap(new ConcurrentHashMap(16));
    private boolean singletonsCurrentlyInDestruction = false;
    private final Map<String, DisposableBean> disposableBeans = new LinkedHashMap();
    private final Map<String, Set<String>> containedBeanMap = new ConcurrentHashMap(16);
    private final Map<String, Set<String>> dependentBeanMap = new ConcurrentHashMap(64);
    private final Map<String, Set<String>> dependenciesForBeanMap = new ConcurrentHashMap(64);

    public void registerSingleton(String beanName, Object singletonObject) throws IllegalStateException {
        Assert.notNull(beanName, "Bean name must not be null");
        Assert.notNull(singletonObject, "Singleton object must not be null");
        synchronized (this.singletonObjects) {
            Object oldObject = this.singletonObjects.get(beanName);
            if (oldObject != null) {
                throw new IllegalStateException("Could not register object [" + singletonObject + "] under bean name '" + beanName + "': there is already object [" + oldObject + "] bound");
            }
            addSingleton(beanName, singletonObject);
        }
    }

    protected void addSingleton(String beanName, Object singletonObject) {
        synchronized (this.singletonObjects) {
            this.singletonObjects.put(beanName, singletonObject);
            this.singletonFactories.remove(beanName);
            this.earlySingletonObjects.remove(beanName);
            this.registeredSingletons.add(beanName);
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public void addSingletonFactory(String beanName, ObjectFactory<?> singletonFactory) {
        Assert.notNull(singletonFactory, "Singleton factory must not be null");
        synchronized (this.singletonObjects) {
            if (!this.singletonObjects.containsKey(beanName)) {
                this.singletonFactories.put(beanName, singletonFactory);
                this.earlySingletonObjects.remove(beanName);
                this.registeredSingletons.add(beanName);
            }
        }
    }

    @Override // org.springframework.beans.factory.config.SingletonBeanRegistry
    @Nullable
    public Object getSingleton(String beanName) {
        return getSingleton(beanName, true);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Nullable
    public Object getSingleton(String beanName, boolean allowEarlyReference) {
        ObjectFactory<?> singletonFactory;
        Object singletonObject = this.singletonObjects.get(beanName);
        if (singletonObject == null && isSingletonCurrentlyInCreation(beanName)) {
            singletonObject = this.earlySingletonObjects.get(beanName);
            if (singletonObject == null && allowEarlyReference) {
                synchronized (this.singletonObjects) {
                    singletonObject = this.singletonObjects.get(beanName);
                    if (singletonObject == null) {
                        singletonObject = this.earlySingletonObjects.get(beanName);
                        if (singletonObject == null && (singletonFactory = this.singletonFactories.get(beanName)) != null) {
                            singletonObject = singletonFactory.getObject();
                            this.earlySingletonObjects.put(beanName, singletonObject);
                            this.singletonFactories.remove(beanName);
                        }
                    }
                }
            }
        }
        return singletonObject;
    }

    /* JADX WARN: Finally extract failed */
    public Object getSingleton(String beanName, ObjectFactory<?> singletonFactory) {
        Object obj;
        Assert.notNull(beanName, "Bean name must not be null");
        synchronized (this.singletonObjects) {
            Object singletonObject = this.singletonObjects.get(beanName);
            if (singletonObject == null) {
                if (this.singletonsCurrentlyInDestruction) {
                    throw new BeanCreationNotAllowedException(beanName, "Singleton bean creation not allowed while singletons of this factory are in destruction (Do not request a bean from a BeanFactory in a destroy method implementation!)");
                }
                if (this.logger.isDebugEnabled()) {
                    this.logger.debug("Creating shared instance of singleton bean '" + beanName + "'");
                }
                beforeSingletonCreation(beanName);
                boolean newSingleton = false;
                boolean recordSuppressedExceptions = this.suppressedExceptions == null;
                if (recordSuppressedExceptions) {
                    this.suppressedExceptions = new LinkedHashSet();
                }
                try {
                    try {
                        singletonObject = singletonFactory.getObject();
                        newSingleton = true;
                        if (recordSuppressedExceptions) {
                            this.suppressedExceptions = null;
                        }
                        afterSingletonCreation(beanName);
                    } catch (IllegalStateException ex) {
                        singletonObject = this.singletonObjects.get(beanName);
                        if (singletonObject == null) {
                            throw ex;
                        }
                        if (recordSuppressedExceptions) {
                            this.suppressedExceptions = null;
                        }
                        afterSingletonCreation(beanName);
                    } catch (BeanCreationException ex2) {
                        if (recordSuppressedExceptions) {
                            for (Exception suppressedException : this.suppressedExceptions) {
                                ex2.addRelatedCause(suppressedException);
                            }
                        }
                        throw ex2;
                    }
                    if (newSingleton) {
                        addSingleton(beanName, singletonObject);
                    }
                } catch (Throwable th) {
                    if (recordSuppressedExceptions) {
                        this.suppressedExceptions = null;
                    }
                    afterSingletonCreation(beanName);
                    throw th;
                }
            }
            obj = singletonObject;
        }
        return obj;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public void onSuppressedException(Exception ex) {
        synchronized (this.singletonObjects) {
            if (this.suppressedExceptions != null && this.suppressedExceptions.size() < 100) {
                this.suppressedExceptions.add(ex);
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public void removeSingleton(String beanName) {
        synchronized (this.singletonObjects) {
            this.singletonObjects.remove(beanName);
            this.singletonFactories.remove(beanName);
            this.earlySingletonObjects.remove(beanName);
            this.registeredSingletons.remove(beanName);
        }
    }

    @Override // org.springframework.beans.factory.config.SingletonBeanRegistry
    public boolean containsSingleton(String beanName) {
        return this.singletonObjects.containsKey(beanName);
    }

    @Override // org.springframework.beans.factory.config.SingletonBeanRegistry
    public String[] getSingletonNames() {
        String[] stringArray;
        synchronized (this.singletonObjects) {
            stringArray = StringUtils.toStringArray(this.registeredSingletons);
        }
        return stringArray;
    }

    @Override // org.springframework.beans.factory.config.SingletonBeanRegistry
    public int getSingletonCount() {
        int size;
        synchronized (this.singletonObjects) {
            size = this.registeredSingletons.size();
        }
        return size;
    }

    public void setCurrentlyInCreation(String beanName, boolean inCreation) {
        Assert.notNull(beanName, "Bean name must not be null");
        if (!inCreation) {
            this.inCreationCheckExclusions.add(beanName);
        } else {
            this.inCreationCheckExclusions.remove(beanName);
        }
    }

    public boolean isCurrentlyInCreation(String beanName) {
        Assert.notNull(beanName, "Bean name must not be null");
        return !this.inCreationCheckExclusions.contains(beanName) && isActuallyInCreation(beanName);
    }

    protected boolean isActuallyInCreation(String beanName) {
        return isSingletonCurrentlyInCreation(beanName);
    }

    public boolean isSingletonCurrentlyInCreation(@Nullable String beanName) {
        return this.singletonsCurrentlyInCreation.contains(beanName);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public void beforeSingletonCreation(String beanName) {
        if (!this.inCreationCheckExclusions.contains(beanName) && !this.singletonsCurrentlyInCreation.add(beanName)) {
            throw new BeanCurrentlyInCreationException(beanName);
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public void afterSingletonCreation(String beanName) {
        if (!this.inCreationCheckExclusions.contains(beanName) && !this.singletonsCurrentlyInCreation.remove(beanName)) {
            throw new IllegalStateException("Singleton '" + beanName + "' isn't currently in creation");
        }
    }

    public void registerDisposableBean(String beanName, DisposableBean bean) {
        synchronized (this.disposableBeans) {
            this.disposableBeans.put(beanName, bean);
        }
    }

    public void registerContainedBean(String containedBeanName, String containingBeanName) {
        synchronized (this.containedBeanMap) {
            Set<String> containedBeans = this.containedBeanMap.computeIfAbsent(containingBeanName, k -> {
                return new LinkedHashSet(8);
            });
            if (containedBeans.add(containedBeanName)) {
                registerDependentBean(containedBeanName, containingBeanName);
            }
        }
    }

    public void registerDependentBean(String beanName, String dependentBeanName) {
        String canonicalName = canonicalName(beanName);
        synchronized (this.dependentBeanMap) {
            Set<String> dependentBeans = this.dependentBeanMap.computeIfAbsent(canonicalName, k -> {
                return new LinkedHashSet(8);
            });
            if (dependentBeans.add(dependentBeanName)) {
                synchronized (this.dependenciesForBeanMap) {
                    Set<String> dependenciesForBean = this.dependenciesForBeanMap.computeIfAbsent(dependentBeanName, k2 -> {
                        return new LinkedHashSet(8);
                    });
                    dependenciesForBean.add(canonicalName);
                }
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public boolean isDependent(String beanName, String dependentBeanName) {
        boolean isDependent;
        synchronized (this.dependentBeanMap) {
            isDependent = isDependent(beanName, dependentBeanName, null);
        }
        return isDependent;
    }

    private boolean isDependent(String beanName, String dependentBeanName, @Nullable Set<String> alreadySeen) {
        if (alreadySeen != null && alreadySeen.contains(beanName)) {
            return false;
        }
        String canonicalName = canonicalName(beanName);
        Set<String> dependentBeans = this.dependentBeanMap.get(canonicalName);
        if (dependentBeans == null || dependentBeans.isEmpty()) {
            return false;
        }
        if (dependentBeans.contains(dependentBeanName)) {
            return true;
        }
        if (alreadySeen == null) {
            alreadySeen = new HashSet();
        }
        alreadySeen.add(beanName);
        for (String transitiveDependency : dependentBeans) {
            if (isDependent(transitiveDependency, dependentBeanName, alreadySeen)) {
                return true;
            }
        }
        return false;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public boolean hasDependentBean(String beanName) {
        return this.dependentBeanMap.containsKey(beanName);
    }

    public String[] getDependentBeans(String beanName) {
        String[] stringArray;
        Set<String> dependentBeans = this.dependentBeanMap.get(beanName);
        if (dependentBeans == null) {
            return new String[0];
        }
        synchronized (this.dependentBeanMap) {
            stringArray = StringUtils.toStringArray(dependentBeans);
        }
        return stringArray;
    }

    public String[] getDependenciesForBean(String beanName) {
        String[] stringArray;
        Set<String> dependenciesForBean = this.dependenciesForBeanMap.get(beanName);
        if (dependenciesForBean == null) {
            return new String[0];
        }
        synchronized (this.dependenciesForBeanMap) {
            stringArray = StringUtils.toStringArray(dependenciesForBean);
        }
        return stringArray;
    }

    public void destroySingletons() {
        String[] disposableBeanNames;
        if (this.logger.isTraceEnabled()) {
            this.logger.trace("Destroying singletons in " + this);
        }
        synchronized (this.singletonObjects) {
            this.singletonsCurrentlyInDestruction = true;
        }
        synchronized (this.disposableBeans) {
            disposableBeanNames = StringUtils.toStringArray(this.disposableBeans.keySet());
        }
        for (int i = disposableBeanNames.length - 1; i >= 0; i--) {
            destroySingleton(disposableBeanNames[i]);
        }
        this.containedBeanMap.clear();
        this.dependentBeanMap.clear();
        this.dependenciesForBeanMap.clear();
        clearSingletonCache();
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public void clearSingletonCache() {
        synchronized (this.singletonObjects) {
            this.singletonObjects.clear();
            this.singletonFactories.clear();
            this.earlySingletonObjects.clear();
            this.registeredSingletons.clear();
            this.singletonsCurrentlyInDestruction = false;
        }
    }

    public void destroySingleton(String beanName) {
        DisposableBean disposableBean;
        removeSingleton(beanName);
        synchronized (this.disposableBeans) {
            disposableBean = this.disposableBeans.remove(beanName);
        }
        destroyBean(beanName, disposableBean);
    }

    protected void destroyBean(String beanName, @Nullable DisposableBean bean) {
        Set<String> dependencies;
        Set<String> containedBeans;
        synchronized (this.dependentBeanMap) {
            dependencies = this.dependentBeanMap.remove(beanName);
        }
        if (dependencies != null) {
            if (this.logger.isTraceEnabled()) {
                this.logger.trace("Retrieved dependent beans for bean '" + beanName + "': " + dependencies);
            }
            for (String dependentBeanName : dependencies) {
                destroySingleton(dependentBeanName);
            }
        }
        if (bean != null) {
            try {
                bean.destroy();
            } catch (Throwable ex) {
                if (this.logger.isWarnEnabled()) {
                    this.logger.warn("Destruction of bean with name '" + beanName + "' threw an exception", ex);
                }
            }
        }
        synchronized (this.containedBeanMap) {
            containedBeans = this.containedBeanMap.remove(beanName);
        }
        if (containedBeans != null) {
            for (String containedBeanName : containedBeans) {
                destroySingleton(containedBeanName);
            }
        }
        synchronized (this.dependentBeanMap) {
            Iterator<Map.Entry<String, Set<String>>> it = this.dependentBeanMap.entrySet().iterator();
            while (it.hasNext()) {
                Map.Entry<String, Set<String>> entry = it.next();
                Set<String> dependenciesToClean = entry.getValue();
                dependenciesToClean.remove(beanName);
                if (dependenciesToClean.isEmpty()) {
                    it.remove();
                }
            }
        }
        this.dependenciesForBeanMap.remove(beanName);
    }

    @Override // org.springframework.beans.factory.config.SingletonBeanRegistry
    public final Object getSingletonMutex() {
        return this.singletonObjects;
    }
}
