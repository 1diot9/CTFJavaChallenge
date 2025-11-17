package org.springframework.boot.context.config;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.Supplier;
import org.apache.commons.logging.Log;
import org.springframework.boot.BootstrapContext;
import org.springframework.boot.BootstrapRegistry;
import org.springframework.boot.ConfigurableBootstrapContext;
import org.springframework.boot.logging.DeferredLogFactory;
import org.springframework.core.ResolvableType;
import org.springframework.core.io.support.SpringFactoriesLoader;
import org.springframework.core.log.LogMessage;
import org.springframework.util.Assert;

/* loaded from: server.jar:BOOT-INF/lib/spring-boot-3.2.2.jar:org/springframework/boot/context/config/ConfigDataLoaders.class */
class ConfigDataLoaders {
    private final Log logger;
    private final List<ConfigDataLoader> loaders;
    private final List<Class<?>> resourceTypes;

    /* JADX INFO: Access modifiers changed from: package-private */
    public ConfigDataLoaders(DeferredLogFactory logFactory, ConfigurableBootstrapContext bootstrapContext, SpringFactoriesLoader springFactoriesLoader) {
        this.logger = logFactory.getLog(getClass());
        SpringFactoriesLoader.ArgumentResolver argumentResolver = SpringFactoriesLoader.ArgumentResolver.of(DeferredLogFactory.class, logFactory);
        this.loaders = springFactoriesLoader.load(ConfigDataLoader.class, argumentResolver.and(ConfigurableBootstrapContext.class, bootstrapContext).and(BootstrapContext.class, bootstrapContext).and(BootstrapRegistry.class, bootstrapContext).andSupplied(Log.class, () -> {
            throw new IllegalArgumentException("Log types cannot be injected, please use DeferredLogFactory");
        }));
        this.resourceTypes = getResourceTypes(this.loaders);
    }

    private List<Class<?>> getResourceTypes(List<ConfigDataLoader> loaders) {
        List<Class<?>> resourceTypes = new ArrayList<>(loaders.size());
        for (ConfigDataLoader loader : loaders) {
            resourceTypes.add(getResourceType(loader));
        }
        return Collections.unmodifiableList(resourceTypes);
    }

    private Class<?> getResourceType(ConfigDataLoader<?> loader) {
        return ResolvableType.forClass(loader.getClass()).as(ConfigDataLoader.class).resolveGeneric(new int[0]);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public <R extends ConfigDataResource> ConfigData load(ConfigDataLoaderContext context, R resource) throws IOException {
        ConfigDataLoader<R> loader = getLoader(context, resource);
        this.logger.trace(LogMessage.of(() -> {
            return "Loading " + resource + " using loader " + loader.getClass().getName();
        }));
        return loader.load(context, resource);
    }

    private <R extends ConfigDataResource> ConfigDataLoader<R> getLoader(ConfigDataLoaderContext context, R resource) {
        ConfigDataLoader<R> result = null;
        for (int i = 0; i < this.loaders.size(); i++) {
            ConfigDataLoader candidate = this.loaders.get(i);
            if (this.resourceTypes.get(i).isInstance(resource) && candidate.isLoadable(context, resource)) {
                if (result != null) {
                    throw new IllegalStateException("Multiple loaders found for resource '" + resource + "' [" + candidate.getClass().getName() + "," + result.getClass().getName() + "]");
                }
                result = candidate;
            }
        }
        Assert.state(result != null, (Supplier<String>) () -> {
            return "No loader found for resource '" + resource + "'";
        });
        return result;
    }
}
