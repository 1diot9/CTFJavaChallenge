package org.springframework.context.event;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Predicate;
import org.springframework.aop.framework.AopProxyUtils;
import org.springframework.beans.factory.BeanClassLoaderAware;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.ApplicationEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.core.ResolvableType;
import org.springframework.core.annotation.AnnotationAwareOrderComparator;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;
import org.springframework.util.ClassUtils;
import org.springframework.util.ObjectUtils;

/* loaded from: server.jar:BOOT-INF/lib/spring-context-6.1.3.jar:org/springframework/context/event/AbstractApplicationEventMulticaster.class */
public abstract class AbstractApplicationEventMulticaster implements ApplicationEventMulticaster, BeanClassLoaderAware, BeanFactoryAware {
    private final DefaultListenerRetriever defaultRetriever = new DefaultListenerRetriever();
    final Map<ListenerCacheKey, CachedListenerRetriever> retrieverCache = new ConcurrentHashMap(64);

    @Nullable
    private ClassLoader beanClassLoader;

    @Nullable
    private ConfigurableBeanFactory beanFactory;

    @Override // org.springframework.beans.factory.BeanClassLoaderAware
    public void setBeanClassLoader(ClassLoader classLoader) {
        this.beanClassLoader = classLoader;
    }

    @Override // org.springframework.beans.factory.BeanFactoryAware
    public void setBeanFactory(BeanFactory beanFactory) {
        if (!(beanFactory instanceof ConfigurableBeanFactory)) {
            throw new IllegalStateException("Not running in a ConfigurableBeanFactory: " + beanFactory);
        }
        ConfigurableBeanFactory cbf = (ConfigurableBeanFactory) beanFactory;
        this.beanFactory = cbf;
        if (this.beanClassLoader == null) {
            this.beanClassLoader = this.beanFactory.getBeanClassLoader();
        }
    }

    private ConfigurableBeanFactory getBeanFactory() {
        if (this.beanFactory == null) {
            throw new IllegalStateException("ApplicationEventMulticaster cannot retrieve listener beans because it is not associated with a BeanFactory");
        }
        return this.beanFactory;
    }

    @Override // org.springframework.context.event.ApplicationEventMulticaster
    public void addApplicationListener(ApplicationListener<?> listener) {
        synchronized (this.defaultRetriever) {
            Object singletonTarget = AopProxyUtils.getSingletonTarget(listener);
            if (singletonTarget instanceof ApplicationListener) {
                this.defaultRetriever.applicationListeners.remove(singletonTarget);
            }
            this.defaultRetriever.applicationListeners.add(listener);
            this.retrieverCache.clear();
        }
    }

    @Override // org.springframework.context.event.ApplicationEventMulticaster
    public void addApplicationListenerBean(String listenerBeanName) {
        synchronized (this.defaultRetriever) {
            this.defaultRetriever.applicationListenerBeans.add(listenerBeanName);
            this.retrieverCache.clear();
        }
    }

    @Override // org.springframework.context.event.ApplicationEventMulticaster
    public void removeApplicationListener(ApplicationListener<?> listener) {
        synchronized (this.defaultRetriever) {
            this.defaultRetriever.applicationListeners.remove(listener);
            this.retrieverCache.clear();
        }
    }

    @Override // org.springframework.context.event.ApplicationEventMulticaster
    public void removeApplicationListenerBean(String listenerBeanName) {
        synchronized (this.defaultRetriever) {
            this.defaultRetriever.applicationListenerBeans.remove(listenerBeanName);
            this.retrieverCache.clear();
        }
    }

    @Override // org.springframework.context.event.ApplicationEventMulticaster
    public void removeApplicationListeners(Predicate<ApplicationListener<?>> predicate) {
        synchronized (this.defaultRetriever) {
            this.defaultRetriever.applicationListeners.removeIf(predicate);
            this.retrieverCache.clear();
        }
    }

    @Override // org.springframework.context.event.ApplicationEventMulticaster
    public void removeApplicationListenerBeans(Predicate<String> predicate) {
        synchronized (this.defaultRetriever) {
            this.defaultRetriever.applicationListenerBeans.removeIf(predicate);
            this.retrieverCache.clear();
        }
    }

    @Override // org.springframework.context.event.ApplicationEventMulticaster
    public void removeAllListeners() {
        synchronized (this.defaultRetriever) {
            this.defaultRetriever.applicationListeners.clear();
            this.defaultRetriever.applicationListenerBeans.clear();
            this.retrieverCache.clear();
        }
    }

    protected Collection<ApplicationListener<?>> getApplicationListeners() {
        Collection<ApplicationListener<?>> applicationListeners;
        synchronized (this.defaultRetriever) {
            applicationListeners = this.defaultRetriever.getApplicationListeners();
        }
        return applicationListeners;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public Collection<ApplicationListener<?>> getApplicationListeners(ApplicationEvent event, ResolvableType eventType) {
        Collection<ApplicationListener<?>> result;
        Object source = event.getSource();
        Class<?> sourceType = source != null ? source.getClass() : null;
        ListenerCacheKey cacheKey = new ListenerCacheKey(eventType, sourceType);
        CachedListenerRetriever newRetriever = null;
        CachedListenerRetriever existingRetriever = this.retrieverCache.get(cacheKey);
        if (existingRetriever == null && (this.beanClassLoader == null || (ClassUtils.isCacheSafe(event.getClass(), this.beanClassLoader) && (sourceType == null || ClassUtils.isCacheSafe(sourceType, this.beanClassLoader))))) {
            newRetriever = new CachedListenerRetriever();
            existingRetriever = this.retrieverCache.putIfAbsent(cacheKey, newRetriever);
            if (existingRetriever != null) {
                newRetriever = null;
            }
        }
        if (existingRetriever != null && (result = existingRetriever.getApplicationListeners()) != null) {
            return result;
        }
        return retrieveApplicationListeners(eventType, sourceType, newRetriever);
    }

    private Collection<ApplicationListener<?>> retrieveApplicationListeners(ResolvableType eventType, @Nullable Class<?> sourceType, @Nullable CachedListenerRetriever retriever) {
        Set<ApplicationListener<?>> listeners;
        Set<String> listenerBeans;
        List<ApplicationListener<?>> allListeners = new ArrayList<>();
        Set<ApplicationListener<?>> filteredListeners = retriever != null ? new LinkedHashSet<>() : null;
        Set<String> filteredListenerBeans = retriever != null ? new LinkedHashSet<>() : null;
        synchronized (this.defaultRetriever) {
            listeners = new LinkedHashSet<>(this.defaultRetriever.applicationListeners);
            listenerBeans = new LinkedHashSet<>(this.defaultRetriever.applicationListenerBeans);
        }
        for (ApplicationListener<?> listener : listeners) {
            if (supportsEvent(listener, eventType, sourceType)) {
                if (retriever != null) {
                    filteredListeners.add(listener);
                }
                allListeners.add(listener);
            }
        }
        if (!listenerBeans.isEmpty()) {
            ConfigurableBeanFactory beanFactory = getBeanFactory();
            for (String listenerBeanName : listenerBeans) {
                try {
                    if (supportsEvent(beanFactory, listenerBeanName, eventType)) {
                        ApplicationListener<?> listener2 = (ApplicationListener) beanFactory.getBean(listenerBeanName, ApplicationListener.class);
                        ApplicationListener<?> unwrappedListener = (ApplicationListener) AopProxyUtils.getSingletonTarget(listener2);
                        if (listener2 != unwrappedListener) {
                            if (filteredListeners != null && filteredListeners.contains(unwrappedListener)) {
                                filteredListeners.remove(unwrappedListener);
                                filteredListeners.add(listener2);
                            }
                            if (allListeners.contains(unwrappedListener)) {
                                allListeners.remove(unwrappedListener);
                                allListeners.add(listener2);
                            }
                        }
                        if (!allListeners.contains(listener2) && supportsEvent(listener2, eventType, sourceType)) {
                            if (retriever != null) {
                                if (beanFactory.isSingleton(listenerBeanName)) {
                                    filteredListeners.add(listener2);
                                } else {
                                    filteredListenerBeans.add(listenerBeanName);
                                }
                            }
                            allListeners.add(listener2);
                        }
                    } else {
                        Object listener3 = beanFactory.getSingleton(listenerBeanName);
                        if (retriever != null) {
                            filteredListeners.remove(listener3);
                        }
                        allListeners.remove(listener3);
                    }
                } catch (NoSuchBeanDefinitionException e) {
                }
            }
        }
        AnnotationAwareOrderComparator.sort(allListeners);
        if (retriever != null) {
            if (filteredListenerBeans.isEmpty()) {
                retriever.applicationListeners = new LinkedHashSet(allListeners);
                retriever.applicationListenerBeans = filteredListenerBeans;
            } else {
                retriever.applicationListeners = filteredListeners;
                retriever.applicationListenerBeans = filteredListenerBeans;
            }
        }
        return allListeners;
    }

    private boolean supportsEvent(ConfigurableBeanFactory beanFactory, String listenerBeanName, ResolvableType eventType) {
        Class<?> listenerType = beanFactory.getType(listenerBeanName);
        if (listenerType == null || GenericApplicationListener.class.isAssignableFrom(listenerType) || SmartApplicationListener.class.isAssignableFrom(listenerType)) {
            return true;
        }
        if (!supportsEvent(listenerType, eventType)) {
            return false;
        }
        try {
            BeanDefinition bd = beanFactory.getMergedBeanDefinition(listenerBeanName);
            ResolvableType genericEventType = bd.getResolvableType().as(ApplicationListener.class).getGeneric(new int[0]);
            if (genericEventType != ResolvableType.NONE) {
                if (!genericEventType.isAssignableFrom(eventType)) {
                    return false;
                }
            }
            return true;
        } catch (NoSuchBeanDefinitionException e) {
            return true;
        }
    }

    protected boolean supportsEvent(Class<?> listenerType, ResolvableType eventType) {
        ResolvableType declaredEventType = GenericApplicationListenerAdapter.resolveDeclaredEventType(listenerType);
        return declaredEventType == null || declaredEventType.isAssignableFrom(eventType);
    }

    protected boolean supportsEvent(ApplicationListener<?> listener, ResolvableType eventType, @Nullable Class<?> sourceType) {
        GenericApplicationListener genericApplicationListenerAdapter;
        if (listener instanceof GenericApplicationListener) {
            GenericApplicationListener gal = (GenericApplicationListener) listener;
            genericApplicationListenerAdapter = gal;
        } else {
            genericApplicationListenerAdapter = new GenericApplicationListenerAdapter(listener);
        }
        GenericApplicationListener smartListener = genericApplicationListenerAdapter;
        return smartListener.supportsEventType(eventType) && smartListener.supportsSourceType(sourceType);
    }

    /* loaded from: server.jar:BOOT-INF/lib/spring-context-6.1.3.jar:org/springframework/context/event/AbstractApplicationEventMulticaster$ListenerCacheKey.class */
    private static final class ListenerCacheKey implements Comparable<ListenerCacheKey> {
        private final ResolvableType eventType;

        @Nullable
        private final Class<?> sourceType;

        public ListenerCacheKey(ResolvableType eventType, @Nullable Class<?> sourceType) {
            Assert.notNull(eventType, "Event type must not be null");
            this.eventType = eventType;
            this.sourceType = sourceType;
        }

        public boolean equals(@Nullable Object other) {
            if (this != other) {
                if (other instanceof ListenerCacheKey) {
                    ListenerCacheKey that = (ListenerCacheKey) other;
                    if (!this.eventType.equals(that.eventType) || !ObjectUtils.nullSafeEquals(this.sourceType, that.sourceType)) {
                    }
                }
                return false;
            }
            return true;
        }

        public int hashCode() {
            return Objects.hash(this.eventType, this.sourceType);
        }

        public String toString() {
            return "ListenerCacheKey [eventType = " + this.eventType + ", sourceType = " + this.sourceType + "]";
        }

        @Override // java.lang.Comparable
        public int compareTo(ListenerCacheKey other) {
            int result = this.eventType.toString().compareTo(other.eventType.toString());
            if (result == 0) {
                if (this.sourceType == null) {
                    return other.sourceType == null ? 0 : -1;
                }
                if (other.sourceType == null) {
                    return 1;
                }
                result = this.sourceType.getName().compareTo(other.sourceType.getName());
            }
            return result;
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: server.jar:BOOT-INF/lib/spring-context-6.1.3.jar:org/springframework/context/event/AbstractApplicationEventMulticaster$CachedListenerRetriever.class */
    public class CachedListenerRetriever {

        @Nullable
        public volatile Set<ApplicationListener<?>> applicationListeners;

        @Nullable
        public volatile Set<String> applicationListenerBeans;

        private CachedListenerRetriever() {
        }

        @Nullable
        public Collection<ApplicationListener<?>> getApplicationListeners() {
            Set<ApplicationListener<?>> applicationListeners = this.applicationListeners;
            Set<String> applicationListenerBeans = this.applicationListenerBeans;
            if (applicationListeners == null || applicationListenerBeans == null) {
                return null;
            }
            List<ApplicationListener<?>> allListeners = new ArrayList<>(applicationListeners.size() + applicationListenerBeans.size());
            allListeners.addAll(applicationListeners);
            if (!applicationListenerBeans.isEmpty()) {
                BeanFactory beanFactory = AbstractApplicationEventMulticaster.this.getBeanFactory();
                for (String listenerBeanName : applicationListenerBeans) {
                    try {
                        allListeners.add((ApplicationListener) beanFactory.getBean(listenerBeanName, ApplicationListener.class));
                    } catch (NoSuchBeanDefinitionException e) {
                    }
                }
            }
            if (!applicationListenerBeans.isEmpty()) {
                AnnotationAwareOrderComparator.sort(allListeners);
            }
            return allListeners;
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: server.jar:BOOT-INF/lib/spring-context-6.1.3.jar:org/springframework/context/event/AbstractApplicationEventMulticaster$DefaultListenerRetriever.class */
    public class DefaultListenerRetriever {
        public final Set<ApplicationListener<?>> applicationListeners = new LinkedHashSet();
        public final Set<String> applicationListenerBeans = new LinkedHashSet();

        private DefaultListenerRetriever() {
        }

        public Collection<ApplicationListener<?>> getApplicationListeners() {
            List<ApplicationListener<?>> allListeners = new ArrayList<>(this.applicationListeners.size() + this.applicationListenerBeans.size());
            allListeners.addAll(this.applicationListeners);
            if (!this.applicationListenerBeans.isEmpty()) {
                BeanFactory beanFactory = AbstractApplicationEventMulticaster.this.getBeanFactory();
                for (String listenerBeanName : this.applicationListenerBeans) {
                    try {
                        ApplicationListener<?> listener = (ApplicationListener) beanFactory.getBean(listenerBeanName, ApplicationListener.class);
                        if (!allListeners.contains(listener)) {
                            allListeners.add(listener);
                        }
                    } catch (NoSuchBeanDefinitionException e) {
                    }
                }
            }
            AnnotationAwareOrderComparator.sort(allListeners);
            return allListeners;
        }
    }
}
