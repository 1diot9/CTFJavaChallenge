package org.springframework.boot.env;

import java.util.List;
import org.springframework.boot.BootstrapContext;
import org.springframework.boot.BootstrapRegistry;
import org.springframework.boot.ConfigurableBootstrapContext;
import org.springframework.boot.logging.DeferredLogFactory;
import org.springframework.core.io.support.SpringFactoriesLoader;

/* loaded from: server.jar:BOOT-INF/lib/spring-boot-3.2.2.jar:org/springframework/boot/env/SpringFactoriesEnvironmentPostProcessorsFactory.class */
class SpringFactoriesEnvironmentPostProcessorsFactory implements EnvironmentPostProcessorsFactory {
    private final SpringFactoriesLoader loader;

    /* JADX INFO: Access modifiers changed from: package-private */
    public SpringFactoriesEnvironmentPostProcessorsFactory(SpringFactoriesLoader loader) {
        this.loader = loader;
    }

    @Override // org.springframework.boot.env.EnvironmentPostProcessorsFactory
    public List<EnvironmentPostProcessor> getEnvironmentPostProcessors(DeferredLogFactory logFactory, ConfigurableBootstrapContext bootstrapContext) {
        SpringFactoriesLoader.ArgumentResolver argumentResolver = SpringFactoriesLoader.ArgumentResolver.of(DeferredLogFactory.class, logFactory);
        return this.loader.load(EnvironmentPostProcessor.class, argumentResolver.and(ConfigurableBootstrapContext.class, bootstrapContext).and(BootstrapContext.class, bootstrapContext).and(BootstrapRegistry.class, bootstrapContext));
    }
}
