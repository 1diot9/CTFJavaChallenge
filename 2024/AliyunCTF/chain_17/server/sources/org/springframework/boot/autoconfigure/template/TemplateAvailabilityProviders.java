package org.springframework.boot.autoconfigure.template;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import org.springframework.context.ApplicationContext;
import org.springframework.core.env.Environment;
import org.springframework.core.io.ResourceLoader;
import org.springframework.core.io.support.SpringFactoriesLoader;
import org.springframework.util.Assert;

/* loaded from: server.jar:BOOT-INF/lib/spring-boot-autoconfigure-3.2.2.jar:org/springframework/boot/autoconfigure/template/TemplateAvailabilityProviders.class */
public class TemplateAvailabilityProviders {
    private final List<TemplateAvailabilityProvider> providers;
    private static final int CACHE_LIMIT = 1024;
    private static final TemplateAvailabilityProvider NONE = new NoTemplateAvailabilityProvider();
    private final Map<String, TemplateAvailabilityProvider> resolved;
    private final Map<String, TemplateAvailabilityProvider> cache;

    public TemplateAvailabilityProviders(ApplicationContext applicationContext) {
        this(applicationContext != null ? applicationContext.getClassLoader() : null);
    }

    public TemplateAvailabilityProviders(ClassLoader classLoader) {
        this.resolved = new ConcurrentHashMap(1024);
        this.cache = new LinkedHashMap<String, TemplateAvailabilityProvider>(1024, 0.75f, true) { // from class: org.springframework.boot.autoconfigure.template.TemplateAvailabilityProviders.1
            @Override // java.util.LinkedHashMap
            protected boolean removeEldestEntry(Map.Entry<String, TemplateAvailabilityProvider> eldest) {
                if (size() > 1024) {
                    TemplateAvailabilityProviders.this.resolved.remove(eldest.getKey());
                    return true;
                }
                return false;
            }
        };
        Assert.notNull(classLoader, "ClassLoader must not be null");
        this.providers = SpringFactoriesLoader.loadFactories(TemplateAvailabilityProvider.class, classLoader);
    }

    protected TemplateAvailabilityProviders(Collection<? extends TemplateAvailabilityProvider> providers) {
        this.resolved = new ConcurrentHashMap(1024);
        this.cache = new LinkedHashMap<String, TemplateAvailabilityProvider>(1024, 0.75f, true) { // from class: org.springframework.boot.autoconfigure.template.TemplateAvailabilityProviders.1
            @Override // java.util.LinkedHashMap
            protected boolean removeEldestEntry(Map.Entry<String, TemplateAvailabilityProvider> eldest) {
                if (size() > 1024) {
                    TemplateAvailabilityProviders.this.resolved.remove(eldest.getKey());
                    return true;
                }
                return false;
            }
        };
        Assert.notNull(providers, "Providers must not be null");
        this.providers = new ArrayList(providers);
    }

    public List<TemplateAvailabilityProvider> getProviders() {
        return this.providers;
    }

    public TemplateAvailabilityProvider getProvider(String view, ApplicationContext applicationContext) {
        Assert.notNull(applicationContext, "ApplicationContext must not be null");
        return getProvider(view, applicationContext.getEnvironment(), applicationContext.getClassLoader(), applicationContext);
    }

    public TemplateAvailabilityProvider getProvider(String view, Environment environment, ClassLoader classLoader, ResourceLoader resourceLoader) {
        Assert.notNull(view, "View must not be null");
        Assert.notNull(environment, "Environment must not be null");
        Assert.notNull(classLoader, "ClassLoader must not be null");
        Assert.notNull(resourceLoader, "ResourceLoader must not be null");
        Boolean useCache = (Boolean) environment.getProperty("spring.template.provider.cache", Boolean.class, true);
        if (!useCache.booleanValue()) {
            return findProvider(view, environment, classLoader, resourceLoader);
        }
        TemplateAvailabilityProvider provider = this.resolved.get(view);
        if (provider == null) {
            synchronized (this.cache) {
                TemplateAvailabilityProvider provider2 = findProvider(view, environment, classLoader, resourceLoader);
                provider = provider2 != null ? provider2 : NONE;
                this.resolved.put(view, provider);
                this.cache.put(view, provider);
            }
        }
        if (provider != NONE) {
            return provider;
        }
        return null;
    }

    private TemplateAvailabilityProvider findProvider(String view, Environment environment, ClassLoader classLoader, ResourceLoader resourceLoader) {
        for (TemplateAvailabilityProvider candidate : this.providers) {
            if (candidate.isTemplateAvailable(view, environment, classLoader, resourceLoader)) {
                return candidate;
            }
        }
        return null;
    }

    /* loaded from: server.jar:BOOT-INF/lib/spring-boot-autoconfigure-3.2.2.jar:org/springframework/boot/autoconfigure/template/TemplateAvailabilityProviders$NoTemplateAvailabilityProvider.class */
    private static final class NoTemplateAvailabilityProvider implements TemplateAvailabilityProvider {
        private NoTemplateAvailabilityProvider() {
        }

        @Override // org.springframework.boot.autoconfigure.template.TemplateAvailabilityProvider
        public boolean isTemplateAvailable(String view, Environment environment, ClassLoader classLoader, ResourceLoader resourceLoader) {
            return false;
        }
    }
}
