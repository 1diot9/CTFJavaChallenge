package org.springframework.cache.annotation;

import java.util.ArrayList;
import java.util.List;
import org.springframework.context.annotation.AdviceMode;
import org.springframework.context.annotation.AdviceModeImportSelector;
import org.springframework.context.annotation.AutoProxyRegistrar;
import org.springframework.util.ClassUtils;
import org.springframework.util.StringUtils;

/* loaded from: agent.jar:BOOT-INF/lib/spring-context-6.1.3.jar:org/springframework/cache/annotation/CachingConfigurationSelector.class */
public class CachingConfigurationSelector extends AdviceModeImportSelector<EnableCaching> {
    private static final String PROXY_JCACHE_CONFIGURATION_CLASS = "org.springframework.cache.jcache.config.ProxyJCacheConfiguration";
    private static final String CACHE_ASPECT_CONFIGURATION_CLASS_NAME = "org.springframework.cache.aspectj.AspectJCachingConfiguration";
    private static final String JCACHE_ASPECT_CONFIGURATION_CLASS_NAME = "org.springframework.cache.aspectj.AspectJJCacheConfiguration";
    private static final boolean jsr107Present;
    private static final boolean jcacheImplPresent;

    static {
        ClassLoader classLoader = CachingConfigurationSelector.class.getClassLoader();
        jsr107Present = ClassUtils.isPresent("javax.cache.Cache", classLoader);
        jcacheImplPresent = ClassUtils.isPresent(PROXY_JCACHE_CONFIGURATION_CLASS, classLoader);
    }

    @Override // org.springframework.context.annotation.AdviceModeImportSelector
    public String[] selectImports(AdviceMode adviceMode) {
        switch (adviceMode) {
            case PROXY:
                return getProxyImports();
            case ASPECTJ:
                return getAspectJImports();
            default:
                throw new IncompatibleClassChangeError();
        }
    }

    private String[] getProxyImports() {
        List<String> result = new ArrayList<>(3);
        result.add(AutoProxyRegistrar.class.getName());
        result.add(ProxyCachingConfiguration.class.getName());
        if (jsr107Present && jcacheImplPresent) {
            result.add(PROXY_JCACHE_CONFIGURATION_CLASS);
        }
        return StringUtils.toStringArray(result);
    }

    private String[] getAspectJImports() {
        List<String> result = new ArrayList<>(2);
        result.add(CACHE_ASPECT_CONFIGURATION_CLASS_NAME);
        if (jsr107Present && jcacheImplPresent) {
            result.add(JCACHE_ASPECT_CONFIGURATION_CLASS_NAME);
        }
        return StringUtils.toStringArray(result);
    }
}
