package org.springframework.scheduling.annotation;

import java.util.List;
import java.util.concurrent.Executor;
import java.util.function.Function;
import java.util.function.Supplier;
import org.springframework.aop.interceptor.AsyncUncaughtExceptionHandler;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.ImportAware;
import org.springframework.core.annotation.AnnotationAttributes;
import org.springframework.core.type.AnnotationMetadata;
import org.springframework.lang.Nullable;
import org.springframework.util.CollectionUtils;
import org.springframework.util.function.SingletonSupplier;

@Configuration(proxyBeanMethods = false)
/* loaded from: agent.jar:BOOT-INF/lib/spring-context-6.1.3.jar:org/springframework/scheduling/annotation/AbstractAsyncConfiguration.class */
public abstract class AbstractAsyncConfiguration implements ImportAware {

    @Nullable
    protected AnnotationAttributes enableAsync;

    @Nullable
    protected Supplier<Executor> executor;

    @Nullable
    protected Supplier<AsyncUncaughtExceptionHandler> exceptionHandler;

    @Override // org.springframework.context.annotation.ImportAware
    public void setImportMetadata(AnnotationMetadata importMetadata) {
        this.enableAsync = AnnotationAttributes.fromMap(importMetadata.getAnnotationAttributes(EnableAsync.class.getName()));
        if (this.enableAsync == null) {
            throw new IllegalArgumentException("@EnableAsync is not present on importing class " + importMetadata.getClassName());
        }
    }

    @Autowired
    void setConfigurers(ObjectProvider<AsyncConfigurer> configurers) {
        Supplier<AsyncConfigurer> configurer = SingletonSupplier.of(() -> {
            List<AsyncConfigurer> candidates = configurers.stream().toList();
            if (CollectionUtils.isEmpty(candidates)) {
                return null;
            }
            if (candidates.size() > 1) {
                throw new IllegalStateException("Only one AsyncConfigurer may exist");
            }
            return candidates.get(0);
        });
        this.executor = adapt(configurer, (v0) -> {
            return v0.getAsyncExecutor();
        });
        this.exceptionHandler = adapt(configurer, (v0) -> {
            return v0.getAsyncUncaughtExceptionHandler();
        });
    }

    private <T> Supplier<T> adapt(Supplier<AsyncConfigurer> supplier, Function<AsyncConfigurer, T> provider) {
        return () -> {
            AsyncConfigurer configurer = (AsyncConfigurer) supplier.get();
            if (configurer != null) {
                return provider.apply(configurer);
            }
            return null;
        };
    }
}
