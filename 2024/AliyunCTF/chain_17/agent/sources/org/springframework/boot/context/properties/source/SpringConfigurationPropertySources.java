package org.springframework.boot.context.properties.source;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.Iterator;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.function.Function;
import org.springframework.boot.origin.OriginLookup;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.PropertySource;
import org.springframework.util.Assert;
import org.springframework.util.ConcurrentReferenceHashMap;

/* JADX INFO: Access modifiers changed from: package-private */
/* loaded from: agent.jar:BOOT-INF/lib/spring-boot-3.2.2.jar:org/springframework/boot/context/properties/source/SpringConfigurationPropertySources.class */
public class SpringConfigurationPropertySources implements Iterable<ConfigurationPropertySource> {
    private final Iterable<PropertySource<?>> sources;
    private final Map<PropertySource<?>, ConfigurationPropertySource> cache = new ConcurrentReferenceHashMap(16, ConcurrentReferenceHashMap.ReferenceType.SOFT);

    /* JADX INFO: Access modifiers changed from: package-private */
    public SpringConfigurationPropertySources(Iterable<PropertySource<?>> sources) {
        Assert.notNull(sources, "Sources must not be null");
        this.sources = sources;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public boolean isUsingSources(Iterable<PropertySource<?>> sources) {
        return this.sources == sources;
    }

    @Override // java.lang.Iterable
    public Iterator<ConfigurationPropertySource> iterator() {
        return new SourcesIterator(this.sources.iterator(), this::adapt);
    }

    /* JADX WARN: Multi-variable type inference failed */
    private ConfigurationPropertySource adapt(PropertySource<?> source) {
        ConfigurationPropertySource result = this.cache.get(source);
        if (result != null && result.getUnderlyingSource() == source) {
            return result;
        }
        ConfigurationPropertySource result2 = SpringConfigurationPropertySource.from(source);
        if (source instanceof OriginLookup) {
            result2 = result2.withPrefix(((OriginLookup) source).getPrefix());
        }
        this.cache.put(source, result2);
        return result2;
    }

    /* loaded from: agent.jar:BOOT-INF/lib/spring-boot-3.2.2.jar:org/springframework/boot/context/properties/source/SpringConfigurationPropertySources$SourcesIterator.class */
    private static class SourcesIterator implements Iterator<ConfigurationPropertySource> {
        private final Deque<Iterator<PropertySource<?>>> iterators = new ArrayDeque(4);
        private ConfigurationPropertySource next;
        private final Function<PropertySource<?>, ConfigurationPropertySource> adapter;

        SourcesIterator(Iterator<PropertySource<?>> iterator, Function<PropertySource<?>, ConfigurationPropertySource> adapter) {
            this.iterators.push(iterator);
            this.adapter = adapter;
        }

        @Override // java.util.Iterator
        public boolean hasNext() {
            return fetchNext() != null;
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // java.util.Iterator
        public ConfigurationPropertySource next() {
            ConfigurationPropertySource next = fetchNext();
            if (next == null) {
                throw new NoSuchElementException();
            }
            this.next = null;
            return next;
        }

        private ConfigurationPropertySource fetchNext() {
            if (this.next == null) {
                if (this.iterators.isEmpty()) {
                    return null;
                }
                if (!this.iterators.peek().hasNext()) {
                    this.iterators.pop();
                    return fetchNext();
                }
                PropertySource<?> candidate = this.iterators.peek().next();
                Object source = candidate.getSource();
                if (source instanceof ConfigurableEnvironment) {
                    ConfigurableEnvironment configurableEnvironment = (ConfigurableEnvironment) source;
                    push(configurableEnvironment);
                    return fetchNext();
                }
                if (isIgnored(candidate)) {
                    return fetchNext();
                }
                this.next = this.adapter.apply(candidate);
            }
            return this.next;
        }

        private void push(ConfigurableEnvironment environment) {
            this.iterators.push(environment.getPropertySources().iterator());
        }

        private boolean isIgnored(PropertySource<?> candidate) {
            return (candidate instanceof PropertySource.StubPropertySource) || (candidate instanceof ConfigurationPropertySourcesPropertySource);
        }
    }
}
