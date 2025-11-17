package org.springframework.cache.annotation;

import java.io.Serializable;
import java.lang.annotation.Annotation;
import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Stream;
import org.springframework.cache.interceptor.CacheEvictOperation;
import org.springframework.cache.interceptor.CacheOperation;
import org.springframework.cache.interceptor.CachePutOperation;
import org.springframework.cache.interceptor.CacheableOperation;
import org.springframework.core.annotation.AnnotatedElementUtils;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.lang.Nullable;
import org.springframework.util.StringUtils;

/* loaded from: agent.jar:BOOT-INF/lib/spring-context-6.1.3.jar:org/springframework/cache/annotation/SpringCacheAnnotationParser.class */
public class SpringCacheAnnotationParser implements CacheAnnotationParser, Serializable {
    private static final Set<Class<? extends Annotation>> CACHE_OPERATION_ANNOTATIONS = Set.of(Cacheable.class, CacheEvict.class, CachePut.class, Caching.class);

    @Override // org.springframework.cache.annotation.CacheAnnotationParser
    public boolean isCandidateClass(Class<?> targetClass) {
        return AnnotationUtils.isCandidateClass(targetClass, CACHE_OPERATION_ANNOTATIONS);
    }

    @Override // org.springframework.cache.annotation.CacheAnnotationParser
    @Nullable
    public Collection<CacheOperation> parseCacheAnnotations(Class<?> type) {
        DefaultCacheConfig defaultConfig = new DefaultCacheConfig(type);
        return parseCacheAnnotations(defaultConfig, type);
    }

    @Override // org.springframework.cache.annotation.CacheAnnotationParser
    @Nullable
    public Collection<CacheOperation> parseCacheAnnotations(Method method) {
        DefaultCacheConfig defaultConfig = new DefaultCacheConfig(method.getDeclaringClass());
        return parseCacheAnnotations(defaultConfig, method);
    }

    @Nullable
    private Collection<CacheOperation> parseCacheAnnotations(DefaultCacheConfig cachingConfig, AnnotatedElement ae) {
        Collection<CacheOperation> localOps;
        Collection<CacheOperation> ops = parseCacheAnnotations(cachingConfig, ae, false);
        if (ops != null && ops.size() > 1 && (localOps = parseCacheAnnotations(cachingConfig, ae, true)) != null) {
            return localOps;
        }
        return ops;
    }

    @Nullable
    private Collection<CacheOperation> parseCacheAnnotations(DefaultCacheConfig cachingConfig, AnnotatedElement ae, boolean localOnly) {
        Collection<? extends Annotation> findAllMergedAnnotations;
        if (localOnly) {
            findAllMergedAnnotations = AnnotatedElementUtils.getAllMergedAnnotations(ae, CACHE_OPERATION_ANNOTATIONS);
        } else {
            findAllMergedAnnotations = AnnotatedElementUtils.findAllMergedAnnotations(ae, CACHE_OPERATION_ANNOTATIONS);
        }
        Collection<? extends Annotation> annotations = findAllMergedAnnotations;
        if (annotations.isEmpty()) {
            return null;
        }
        Collection<CacheOperation> ops = new ArrayList<>(1);
        Stream<? extends Annotation> stream = annotations.stream();
        Class<Cacheable> cls = Cacheable.class;
        Objects.requireNonNull(Cacheable.class);
        Stream<? extends Annotation> filter = stream.filter((v1) -> {
            return r1.isInstance(v1);
        });
        Class<Cacheable> cls2 = Cacheable.class;
        Objects.requireNonNull(Cacheable.class);
        filter.map((v1) -> {
            return r1.cast(v1);
        }).forEach(cacheable -> {
            ops.add(parseCacheableAnnotation(ae, cachingConfig, cacheable));
        });
        Stream<? extends Annotation> stream2 = annotations.stream();
        Class<CacheEvict> cls3 = CacheEvict.class;
        Objects.requireNonNull(CacheEvict.class);
        Stream<? extends Annotation> filter2 = stream2.filter((v1) -> {
            return r1.isInstance(v1);
        });
        Class<CacheEvict> cls4 = CacheEvict.class;
        Objects.requireNonNull(CacheEvict.class);
        filter2.map((v1) -> {
            return r1.cast(v1);
        }).forEach(cacheEvict -> {
            ops.add(parseEvictAnnotation(ae, cachingConfig, cacheEvict));
        });
        Stream<? extends Annotation> stream3 = annotations.stream();
        Class<CachePut> cls5 = CachePut.class;
        Objects.requireNonNull(CachePut.class);
        Stream<? extends Annotation> filter3 = stream3.filter((v1) -> {
            return r1.isInstance(v1);
        });
        Class<CachePut> cls6 = CachePut.class;
        Objects.requireNonNull(CachePut.class);
        filter3.map((v1) -> {
            return r1.cast(v1);
        }).forEach(cachePut -> {
            ops.add(parsePutAnnotation(ae, cachingConfig, cachePut));
        });
        Stream<? extends Annotation> stream4 = annotations.stream();
        Class<Caching> cls7 = Caching.class;
        Objects.requireNonNull(Caching.class);
        Stream<? extends Annotation> filter4 = stream4.filter((v1) -> {
            return r1.isInstance(v1);
        });
        Class<Caching> cls8 = Caching.class;
        Objects.requireNonNull(Caching.class);
        filter4.map((v1) -> {
            return r1.cast(v1);
        }).forEach(caching -> {
            parseCachingAnnotation(ae, cachingConfig, caching, ops);
        });
        return ops;
    }

    private CacheableOperation parseCacheableAnnotation(AnnotatedElement ae, DefaultCacheConfig defaultConfig, Cacheable cacheable) {
        CacheableOperation.Builder builder = new CacheableOperation.Builder();
        builder.setName(ae.toString());
        builder.setCacheNames(cacheable.cacheNames());
        builder.setCondition(cacheable.condition());
        builder.setUnless(cacheable.unless());
        builder.setKey(cacheable.key());
        builder.setKeyGenerator(cacheable.keyGenerator());
        builder.setCacheManager(cacheable.cacheManager());
        builder.setCacheResolver(cacheable.cacheResolver());
        builder.setSync(cacheable.sync());
        defaultConfig.applyDefault(builder);
        CacheableOperation op = builder.build();
        validateCacheOperation(ae, op);
        return op;
    }

    private CacheEvictOperation parseEvictAnnotation(AnnotatedElement ae, DefaultCacheConfig defaultConfig, CacheEvict cacheEvict) {
        CacheEvictOperation.Builder builder = new CacheEvictOperation.Builder();
        builder.setName(ae.toString());
        builder.setCacheNames(cacheEvict.cacheNames());
        builder.setCondition(cacheEvict.condition());
        builder.setKey(cacheEvict.key());
        builder.setKeyGenerator(cacheEvict.keyGenerator());
        builder.setCacheManager(cacheEvict.cacheManager());
        builder.setCacheResolver(cacheEvict.cacheResolver());
        builder.setCacheWide(cacheEvict.allEntries());
        builder.setBeforeInvocation(cacheEvict.beforeInvocation());
        defaultConfig.applyDefault(builder);
        CacheEvictOperation op = builder.build();
        validateCacheOperation(ae, op);
        return op;
    }

    private CacheOperation parsePutAnnotation(AnnotatedElement ae, DefaultCacheConfig defaultConfig, CachePut cachePut) {
        CachePutOperation.Builder builder = new CachePutOperation.Builder();
        builder.setName(ae.toString());
        builder.setCacheNames(cachePut.cacheNames());
        builder.setCondition(cachePut.condition());
        builder.setUnless(cachePut.unless());
        builder.setKey(cachePut.key());
        builder.setKeyGenerator(cachePut.keyGenerator());
        builder.setCacheManager(cachePut.cacheManager());
        builder.setCacheResolver(cachePut.cacheResolver());
        defaultConfig.applyDefault(builder);
        CachePutOperation op = builder.build();
        validateCacheOperation(ae, op);
        return op;
    }

    private void parseCachingAnnotation(AnnotatedElement ae, DefaultCacheConfig defaultConfig, Caching caching, Collection<CacheOperation> ops) {
        Cacheable[] cacheables = caching.cacheable();
        for (Cacheable cacheable : cacheables) {
            ops.add(parseCacheableAnnotation(ae, defaultConfig, cacheable));
        }
        CacheEvict[] cacheEvicts = caching.evict();
        for (CacheEvict cacheEvict : cacheEvicts) {
            ops.add(parseEvictAnnotation(ae, defaultConfig, cacheEvict));
        }
        CachePut[] cachePuts = caching.put();
        for (CachePut cachePut : cachePuts) {
            ops.add(parsePutAnnotation(ae, defaultConfig, cachePut));
        }
    }

    private void validateCacheOperation(AnnotatedElement ae, CacheOperation operation) {
        if (StringUtils.hasText(operation.getKey()) && StringUtils.hasText(operation.getKeyGenerator())) {
            throw new IllegalStateException("Invalid cache annotation configuration on '" + ae.toString() + "'. Both 'key' and 'keyGenerator' attributes have been set. These attributes are mutually exclusive: either set the SpEL expression used tocompute the key at runtime or set the name of the KeyGenerator bean to use.");
        }
        if (StringUtils.hasText(operation.getCacheManager()) && StringUtils.hasText(operation.getCacheResolver())) {
            throw new IllegalStateException("Invalid cache annotation configuration on '" + ae.toString() + "'. Both 'cacheManager' and 'cacheResolver' attributes have been set. These attributes are mutually exclusive: the cache manager is used to configure adefault cache resolver if none is set. If a cache resolver is set, the cache managerwon't be used.");
        }
    }

    public boolean equals(@Nullable Object other) {
        return other instanceof SpringCacheAnnotationParser;
    }

    public int hashCode() {
        return SpringCacheAnnotationParser.class.hashCode();
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: agent.jar:BOOT-INF/lib/spring-context-6.1.3.jar:org/springframework/cache/annotation/SpringCacheAnnotationParser$DefaultCacheConfig.class */
    public static class DefaultCacheConfig {
        private final Class<?> target;

        @Nullable
        private String[] cacheNames;

        @Nullable
        private String keyGenerator;

        @Nullable
        private String cacheManager;

        @Nullable
        private String cacheResolver;
        private boolean initialized = false;

        public DefaultCacheConfig(Class<?> target) {
            this.target = target;
        }

        public void applyDefault(CacheOperation.Builder builder) {
            if (!this.initialized) {
                CacheConfig annotation = (CacheConfig) AnnotatedElementUtils.findMergedAnnotation(this.target, CacheConfig.class);
                if (annotation != null) {
                    this.cacheNames = annotation.cacheNames();
                    this.keyGenerator = annotation.keyGenerator();
                    this.cacheManager = annotation.cacheManager();
                    this.cacheResolver = annotation.cacheResolver();
                }
                this.initialized = true;
            }
            if (builder.getCacheNames().isEmpty() && this.cacheNames != null) {
                builder.setCacheNames(this.cacheNames);
            }
            if (!StringUtils.hasText(builder.getKey()) && !StringUtils.hasText(builder.getKeyGenerator()) && StringUtils.hasText(this.keyGenerator)) {
                builder.setKeyGenerator(this.keyGenerator);
            }
            if (!StringUtils.hasText(builder.getCacheManager()) && !StringUtils.hasText(builder.getCacheResolver())) {
                if (StringUtils.hasText(this.cacheResolver)) {
                    builder.setCacheResolver(this.cacheResolver);
                } else if (StringUtils.hasText(this.cacheManager)) {
                    builder.setCacheManager(this.cacheManager);
                }
            }
        }
    }
}
