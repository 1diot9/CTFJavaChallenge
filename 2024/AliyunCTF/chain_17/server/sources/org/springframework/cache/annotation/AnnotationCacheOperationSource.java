package org.springframework.cache.annotation;

import java.io.Serializable;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Set;
import org.springframework.cache.interceptor.AbstractFallbackCacheOperationSource;
import org.springframework.cache.interceptor.CacheOperation;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;

/* loaded from: server.jar:BOOT-INF/lib/spring-context-6.1.3.jar:org/springframework/cache/annotation/AnnotationCacheOperationSource.class */
public class AnnotationCacheOperationSource extends AbstractFallbackCacheOperationSource implements Serializable {
    private final boolean publicMethodsOnly;
    private final Set<CacheAnnotationParser> annotationParsers;

    /* JADX INFO: Access modifiers changed from: protected */
    @FunctionalInterface
    /* loaded from: server.jar:BOOT-INF/lib/spring-context-6.1.3.jar:org/springframework/cache/annotation/AnnotationCacheOperationSource$CacheOperationProvider.class */
    public interface CacheOperationProvider {
        @Nullable
        Collection<CacheOperation> getCacheOperations(CacheAnnotationParser parser);
    }

    public AnnotationCacheOperationSource() {
        this(true);
    }

    public AnnotationCacheOperationSource(boolean publicMethodsOnly) {
        this.publicMethodsOnly = publicMethodsOnly;
        this.annotationParsers = Collections.singleton(new SpringCacheAnnotationParser());
    }

    public AnnotationCacheOperationSource(CacheAnnotationParser annotationParser) {
        this.publicMethodsOnly = true;
        Assert.notNull(annotationParser, "CacheAnnotationParser must not be null");
        this.annotationParsers = Collections.singleton(annotationParser);
    }

    public AnnotationCacheOperationSource(CacheAnnotationParser... annotationParsers) {
        this.publicMethodsOnly = true;
        Assert.notEmpty(annotationParsers, "At least one CacheAnnotationParser needs to be specified");
        this.annotationParsers = new LinkedHashSet(Arrays.asList(annotationParsers));
    }

    public AnnotationCacheOperationSource(Set<CacheAnnotationParser> annotationParsers) {
        this.publicMethodsOnly = true;
        Assert.notEmpty(annotationParsers, "At least one CacheAnnotationParser needs to be specified");
        this.annotationParsers = annotationParsers;
    }

    @Override // org.springframework.cache.interceptor.CacheOperationSource
    public boolean isCandidateClass(Class<?> targetClass) {
        for (CacheAnnotationParser parser : this.annotationParsers) {
            if (parser.isCandidateClass(targetClass)) {
                return true;
            }
        }
        return false;
    }

    @Override // org.springframework.cache.interceptor.AbstractFallbackCacheOperationSource
    @Nullable
    protected Collection<CacheOperation> findCacheOperations(Class<?> clazz) {
        return determineCacheOperations(parser -> {
            return parser.parseCacheAnnotations((Class<?>) clazz);
        });
    }

    @Override // org.springframework.cache.interceptor.AbstractFallbackCacheOperationSource
    @Nullable
    protected Collection<CacheOperation> findCacheOperations(Method method) {
        return determineCacheOperations(parser -> {
            return parser.parseCacheAnnotations(method);
        });
    }

    @Nullable
    protected Collection<CacheOperation> determineCacheOperations(CacheOperationProvider provider) {
        Collection<CacheOperation> ops = null;
        for (CacheAnnotationParser parser : this.annotationParsers) {
            Collection<CacheOperation> annOps = provider.getCacheOperations(parser);
            if (annOps != null) {
                if (ops == null) {
                    ops = annOps;
                } else {
                    Collection<CacheOperation> combined = new ArrayList<>(ops.size() + annOps.size());
                    combined.addAll(ops);
                    combined.addAll(annOps);
                    ops = combined;
                }
            }
        }
        return ops;
    }

    @Override // org.springframework.cache.interceptor.AbstractFallbackCacheOperationSource
    protected boolean allowPublicMethodsOnly() {
        return this.publicMethodsOnly;
    }

    public boolean equals(@Nullable Object other) {
        if (this != other) {
            if (other instanceof AnnotationCacheOperationSource) {
                AnnotationCacheOperationSource otherCos = (AnnotationCacheOperationSource) other;
                if (!this.annotationParsers.equals(otherCos.annotationParsers) || this.publicMethodsOnly != otherCos.publicMethodsOnly) {
                }
            }
            return false;
        }
        return true;
    }

    public int hashCode() {
        return this.annotationParsers.hashCode();
    }
}
