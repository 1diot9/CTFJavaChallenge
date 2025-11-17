package org.springframework.context.annotation;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import org.springframework.beans.factory.annotation.AnnotatedBeanDefinition;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.lang.Nullable;

/* loaded from: server.jar:BOOT-INF/lib/spring-context-6.1.3.jar:org/springframework/context/annotation/Jsr330ScopeMetadataResolver.class */
public class Jsr330ScopeMetadataResolver implements ScopeMetadataResolver {
    private final Map<String, String> scopeMap = new HashMap();

    public Jsr330ScopeMetadataResolver() {
        registerScope("jakarta.inject.Singleton", "singleton");
    }

    public final void registerScope(Class<?> annotationType, String scopeName) {
        this.scopeMap.put(annotationType.getName(), scopeName);
    }

    public final void registerScope(String annotationType, String scopeName) {
        this.scopeMap.put(annotationType, scopeName);
    }

    @Nullable
    protected String resolveScopeName(String annotationType) {
        return this.scopeMap.get(annotationType);
    }

    @Override // org.springframework.context.annotation.ScopeMetadataResolver
    public ScopeMetadata resolveScopeMetadata(BeanDefinition definition) {
        ScopeMetadata metadata = new ScopeMetadata();
        metadata.setScopeName("prototype");
        if (definition instanceof AnnotatedBeanDefinition) {
            AnnotatedBeanDefinition annDef = (AnnotatedBeanDefinition) definition;
            Set<String> annTypes = annDef.getMetadata().getAnnotationTypes();
            String found = null;
            for (String annType : annTypes) {
                Set<String> metaAnns = annDef.getMetadata().getMetaAnnotationTypes(annType);
                if (metaAnns.contains("jakarta.inject.Scope")) {
                    if (found != null) {
                        throw new IllegalStateException("Found ambiguous scope annotations on bean class [" + definition.getBeanClassName() + "]: " + found + ", " + annType);
                    }
                    found = annType;
                    String scopeName = resolveScopeName(annType);
                    if (scopeName == null) {
                        throw new IllegalStateException("Unsupported scope annotation - not mapped onto Spring scope name: " + annType);
                    }
                    metadata.setScopeName(scopeName);
                }
            }
        }
        return metadata;
    }
}
