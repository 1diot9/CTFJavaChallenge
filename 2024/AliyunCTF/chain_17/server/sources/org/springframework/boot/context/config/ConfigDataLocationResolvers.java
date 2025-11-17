package org.springframework.boot.context.config;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.Supplier;
import org.apache.commons.logging.Log;
import org.springframework.boot.BootstrapContext;
import org.springframework.boot.BootstrapRegistry;
import org.springframework.boot.ConfigurableBootstrapContext;
import org.springframework.boot.context.properties.bind.Binder;
import org.springframework.boot.logging.DeferredLogFactory;
import org.springframework.core.io.ResourceLoader;
import org.springframework.core.io.support.SpringFactoriesLoader;

/* loaded from: server.jar:BOOT-INF/lib/spring-boot-3.2.2.jar:org/springframework/boot/context/config/ConfigDataLocationResolvers.class */
class ConfigDataLocationResolvers {
    private final List<ConfigDataLocationResolver<?>> resolvers;

    /* JADX INFO: Access modifiers changed from: package-private */
    public ConfigDataLocationResolvers(DeferredLogFactory logFactory, ConfigurableBootstrapContext bootstrapContext, Binder binder, ResourceLoader resourceLoader, SpringFactoriesLoader springFactoriesLoader) {
        SpringFactoriesLoader.ArgumentResolver argumentResolver = SpringFactoriesLoader.ArgumentResolver.of(DeferredLogFactory.class, logFactory);
        this.resolvers = reorder(springFactoriesLoader.load(ConfigDataLocationResolver.class, argumentResolver.and(Binder.class, binder).and(ResourceLoader.class, resourceLoader).and(ConfigurableBootstrapContext.class, bootstrapContext).and(BootstrapContext.class, bootstrapContext).and(BootstrapRegistry.class, bootstrapContext).andSupplied(Log.class, () -> {
            throw new IllegalArgumentException("Log types cannot be injected, please use DeferredLogFactory");
        })));
    }

    private List<ConfigDataLocationResolver<?>> reorder(List<ConfigDataLocationResolver> resolvers) {
        List<ConfigDataLocationResolver<?>> reordered = new ArrayList<>(resolvers.size());
        StandardConfigDataLocationResolver resourceResolver = null;
        for (ConfigDataLocationResolver<?> resolver : resolvers) {
            if (resolver instanceof StandardConfigDataLocationResolver) {
                StandardConfigDataLocationResolver configDataLocationResolver = (StandardConfigDataLocationResolver) resolver;
                resourceResolver = configDataLocationResolver;
            } else {
                reordered.add(resolver);
            }
        }
        if (resourceResolver != null) {
            reordered.add(resourceResolver);
        }
        return Collections.unmodifiableList(reordered);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public List<ConfigDataResolutionResult> resolve(ConfigDataLocationResolverContext context, ConfigDataLocation location, Profiles profiles) {
        if (location == null) {
            return Collections.emptyList();
        }
        for (ConfigDataLocationResolver<?> resolver : getResolvers()) {
            if (resolver.isResolvable(context, location)) {
                return resolve(resolver, context, location, profiles);
            }
        }
        throw new UnsupportedConfigDataLocationException(location);
    }

    private List<ConfigDataResolutionResult> resolve(ConfigDataLocationResolver<?> resolver, ConfigDataLocationResolverContext context, ConfigDataLocation location, Profiles profiles) {
        List<ConfigDataResolutionResult> resolved = resolve(location, false, () -> {
            return resolver.resolve(context, location);
        });
        if (profiles == null) {
            return resolved;
        }
        List<ConfigDataResolutionResult> profileSpecific = resolve(location, true, () -> {
            return resolver.resolveProfileSpecific(context, location, profiles);
        });
        return merge(resolved, profileSpecific);
    }

    private List<ConfigDataResolutionResult> resolve(ConfigDataLocation location, boolean profileSpecific, Supplier<List<? extends ConfigDataResource>> resolveAction) {
        List<ConfigDataResource> resources = nonNullList(resolveAction.get());
        List<ConfigDataResolutionResult> resolved = new ArrayList<>(resources.size());
        for (ConfigDataResource resource : resources) {
            resolved.add(new ConfigDataResolutionResult(location, resource, profileSpecific));
        }
        return resolved;
    }

    /* JADX WARN: Multi-variable type inference failed */
    private <T> List<T> nonNullList(List<? extends T> list) {
        return list != 0 ? list : Collections.emptyList();
    }

    private <T> List<T> merge(List<T> list1, List<T> list2) {
        List<T> merged = new ArrayList<>(list1.size() + list2.size());
        merged.addAll(list1);
        merged.addAll(list2);
        return merged;
    }

    List<ConfigDataLocationResolver<?>> getResolvers() {
        return this.resolvers;
    }
}
