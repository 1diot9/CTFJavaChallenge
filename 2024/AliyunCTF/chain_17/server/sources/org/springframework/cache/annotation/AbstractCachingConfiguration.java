package org.springframework.cache.annotation;

import java.util.List;
import java.util.function.Function;
import java.util.function.Supplier;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.CacheManager;
import org.springframework.cache.interceptor.CacheErrorHandler;
import org.springframework.cache.interceptor.CacheResolver;
import org.springframework.cache.interceptor.KeyGenerator;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.ImportAware;
import org.springframework.core.annotation.AnnotationAttributes;
import org.springframework.core.type.AnnotationMetadata;
import org.springframework.lang.Nullable;
import org.springframework.util.CollectionUtils;
import org.springframework.util.function.SingletonSupplier;

@Configuration(proxyBeanMethods = false)
/* loaded from: server.jar:BOOT-INF/lib/spring-context-6.1.3.jar:org/springframework/cache/annotation/AbstractCachingConfiguration.class */
public abstract class AbstractCachingConfiguration implements ImportAware {

    @Nullable
    protected AnnotationAttributes enableCaching;

    @Nullable
    protected Supplier<CacheManager> cacheManager;

    @Nullable
    protected Supplier<CacheResolver> cacheResolver;

    @Nullable
    protected Supplier<KeyGenerator> keyGenerator;

    @Nullable
    protected Supplier<CacheErrorHandler> errorHandler;

    @Override // org.springframework.context.annotation.ImportAware
    public void setImportMetadata(AnnotationMetadata importMetadata) {
        this.enableCaching = AnnotationAttributes.fromMap(importMetadata.getAnnotationAttributes(EnableCaching.class.getName()));
        if (this.enableCaching == null) {
            throw new IllegalArgumentException("@EnableCaching is not present on importing class " + importMetadata.getClassName());
        }
    }

    @Autowired
    void setConfigurers(ObjectProvider<CachingConfigurer> configurers) {
        Supplier<CachingConfigurer> configurer = () -> {
            List<CachingConfigurer> candidates = configurers.stream().toList();
            if (CollectionUtils.isEmpty(candidates)) {
                return null;
            }
            if (candidates.size() > 1) {
                throw new IllegalStateException(candidates.size() + " implementations of CachingConfigurer were found when only 1 was expected. Refactor the configuration such that CachingConfigurer is implemented only once or not at all.");
            }
            return candidates.get(0);
        };
        useCachingConfigurer(new CachingConfigurerSupplier(configurer));
    }

    protected void useCachingConfigurer(CachingConfigurerSupplier cachingConfigurerSupplier) {
        this.cacheManager = cachingConfigurerSupplier.adapt((v0) -> {
            return v0.cacheManager();
        });
        this.cacheResolver = cachingConfigurerSupplier.adapt((v0) -> {
            return v0.cacheResolver();
        });
        this.keyGenerator = cachingConfigurerSupplier.adapt((v0) -> {
            return v0.keyGenerator();
        });
        this.errorHandler = cachingConfigurerSupplier.adapt((v0) -> {
            return v0.errorHandler();
        });
    }

    /* JADX INFO: Access modifiers changed from: protected */
    /* loaded from: server.jar:BOOT-INF/lib/spring-context-6.1.3.jar:org/springframework/cache/annotation/AbstractCachingConfiguration$CachingConfigurerSupplier.class */
    public static class CachingConfigurerSupplier {
        private final Supplier<CachingConfigurer> supplier;

        public CachingConfigurerSupplier(Supplier<CachingConfigurer> supplier) {
            this.supplier = SingletonSupplier.of((Supplier) supplier);
        }

        @Nullable
        public <T> Supplier<T> adapt(Function<CachingConfigurer, T> provider) {
            return () -> {
                CachingConfigurer cachingConfigurer = this.supplier.get();
                if (cachingConfigurer != null) {
                    return provider.apply(cachingConfigurer);
                }
                return null;
            };
        }
    }
}
