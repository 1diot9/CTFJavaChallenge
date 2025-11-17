package org.springframework.context.annotation;

import java.lang.annotation.Annotation;
import org.springframework.beans.factory.annotation.AnnotatedBeanDefinition;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.core.annotation.AnnotationAttributes;
import org.springframework.util.Assert;

/* loaded from: agent.jar:BOOT-INF/lib/spring-context-6.1.3.jar:org/springframework/context/annotation/AnnotationScopeMetadataResolver.class */
public class AnnotationScopeMetadataResolver implements ScopeMetadataResolver {
    private final ScopedProxyMode defaultProxyMode;
    protected Class<? extends Annotation> scopeAnnotationType;

    public AnnotationScopeMetadataResolver() {
        this.scopeAnnotationType = Scope.class;
        this.defaultProxyMode = ScopedProxyMode.NO;
    }

    public AnnotationScopeMetadataResolver(ScopedProxyMode defaultProxyMode) {
        this.scopeAnnotationType = Scope.class;
        Assert.notNull(defaultProxyMode, "'defaultProxyMode' must not be null");
        this.defaultProxyMode = defaultProxyMode;
    }

    public void setScopeAnnotationType(Class<? extends Annotation> scopeAnnotationType) {
        Assert.notNull(scopeAnnotationType, "'scopeAnnotationType' must not be null");
        this.scopeAnnotationType = scopeAnnotationType;
    }

    @Override // org.springframework.context.annotation.ScopeMetadataResolver
    public ScopeMetadata resolveScopeMetadata(BeanDefinition definition) {
        ScopeMetadata metadata = new ScopeMetadata();
        if (definition instanceof AnnotatedBeanDefinition) {
            AnnotatedBeanDefinition annDef = (AnnotatedBeanDefinition) definition;
            AnnotationAttributes attributes = AnnotationConfigUtils.attributesFor(annDef.getMetadata(), this.scopeAnnotationType);
            if (attributes != null) {
                metadata.setScopeName(attributes.getString("value"));
                ScopedProxyMode proxyMode = (ScopedProxyMode) attributes.getEnum("proxyMode");
                if (proxyMode == ScopedProxyMode.DEFAULT) {
                    proxyMode = this.defaultProxyMode;
                }
                metadata.setScopedProxyMode(proxyMode);
            }
        }
        return metadata;
    }
}
