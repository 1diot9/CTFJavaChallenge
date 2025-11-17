package org.springframework.beans.factory.aot;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.IdentityHashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;
import java.util.stream.Stream;
import org.springframework.beans.factory.BeanFactoryUtils;
import org.springframework.beans.factory.ListableBeanFactory;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.core.annotation.AnnotationAwareOrderComparator;
import org.springframework.core.io.support.SpringFactoriesLoader;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;
import org.springframework.util.ObjectUtils;

/* loaded from: server.jar:BOOT-INF/lib/spring-beans-6.1.3.jar:org/springframework/beans/factory/aot/AotServices.class */
public final class AotServices<T> implements Iterable<T> {
    public static final String FACTORIES_RESOURCE_LOCATION = "META-INF/spring/aot.factories";
    private final List<T> services;
    private final Map<String, T> beans;
    private final Map<T, Source> sources;

    /* loaded from: server.jar:BOOT-INF/lib/spring-beans-6.1.3.jar:org/springframework/beans/factory/aot/AotServices$Source.class */
    public enum Source {
        SPRING_FACTORIES_LOADER,
        BEAN_FACTORY
    }

    private AotServices(List<T> loaded, Map<String, T> beans) {
        this.services = collectServices(loaded, beans);
        this.sources = collectSources(loaded, beans.values());
        this.beans = beans;
    }

    private List<T> collectServices(List<T> loaded, Map<String, T> beans) {
        List<T> services = new ArrayList<>();
        services.addAll(beans.values());
        services.addAll(loaded);
        AnnotationAwareOrderComparator.sort((List<?>) services);
        return Collections.unmodifiableList(services);
    }

    private Map<T, Source> collectSources(Collection<T> loaded, Collection<T> beans) {
        Map<T, Source> sources = new IdentityHashMap<>();
        loaded.forEach(service -> {
            sources.put(service, Source.SPRING_FACTORIES_LOADER);
        });
        beans.forEach(service2 -> {
            sources.put(service2, Source.BEAN_FACTORY);
        });
        return Collections.unmodifiableMap(sources);
    }

    public static Loader factories() {
        return factories((ClassLoader) null);
    }

    public static Loader factories(@Nullable ClassLoader classLoader) {
        return factories(getSpringFactoriesLoader(classLoader));
    }

    public static Loader factories(SpringFactoriesLoader springFactoriesLoader) {
        Assert.notNull(springFactoriesLoader, "'springFactoriesLoader' must not be null");
        return new Loader(springFactoriesLoader, null);
    }

    public static Loader factoriesAndBeans(ListableBeanFactory beanFactory) {
        ClassLoader classLoader;
        if (beanFactory instanceof ConfigurableBeanFactory) {
            ConfigurableBeanFactory configurableBeanFactory = (ConfigurableBeanFactory) beanFactory;
            classLoader = configurableBeanFactory.getBeanClassLoader();
        } else {
            classLoader = null;
        }
        ClassLoader classLoader2 = classLoader;
        return factoriesAndBeans(getSpringFactoriesLoader(classLoader2), beanFactory);
    }

    public static Loader factoriesAndBeans(SpringFactoriesLoader springFactoriesLoader, ListableBeanFactory beanFactory) {
        Assert.notNull(beanFactory, "'beanFactory' must not be null");
        Assert.notNull(springFactoriesLoader, "'springFactoriesLoader' must not be null");
        return new Loader(springFactoriesLoader, beanFactory);
    }

    private static SpringFactoriesLoader getSpringFactoriesLoader(@Nullable ClassLoader classLoader) {
        return SpringFactoriesLoader.forResourceLocation(FACTORIES_RESOURCE_LOCATION, classLoader);
    }

    @Override // java.lang.Iterable
    public Iterator<T> iterator() {
        return this.services.iterator();
    }

    public Stream<T> stream() {
        return this.services.stream();
    }

    public List<T> asList() {
        return this.services;
    }

    @Nullable
    public T findByBeanName(String beanName) {
        return this.beans.get(beanName);
    }

    public Source getSource(T service) {
        Source source = this.sources.get(service);
        Assert.state(source != null, (Supplier<String>) () -> {
            return "Unable to find service " + ObjectUtils.identityToString(source);
        });
        return source;
    }

    /* loaded from: server.jar:BOOT-INF/lib/spring-beans-6.1.3.jar:org/springframework/beans/factory/aot/AotServices$Loader.class */
    public static class Loader {
        private final SpringFactoriesLoader springFactoriesLoader;

        @Nullable
        private final ListableBeanFactory beanFactory;

        Loader(SpringFactoriesLoader springFactoriesLoader, @Nullable ListableBeanFactory beanFactory) {
            this.springFactoriesLoader = springFactoriesLoader;
            this.beanFactory = beanFactory;
        }

        public <T> AotServices<T> load(Class<T> type) {
            return new AotServices<>(this.springFactoriesLoader.load(type), loadBeans(type));
        }

        private <T> Map<String, T> loadBeans(Class<T> type) {
            if (this.beanFactory != null) {
                return BeanFactoryUtils.beansOfTypeIncludingAncestors(this.beanFactory, type, true, false);
            }
            return Collections.emptyMap();
        }
    }
}
