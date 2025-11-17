package org.springframework.context.support;

import org.springframework.context.EmbeddedValueResolverAware;
import org.springframework.lang.Nullable;
import org.springframework.util.StringValueResolver;

/* loaded from: agent.jar:BOOT-INF/lib/spring-context-6.1.3.jar:org/springframework/context/support/EmbeddedValueResolutionSupport.class */
public class EmbeddedValueResolutionSupport implements EmbeddedValueResolverAware {

    @Nullable
    private StringValueResolver embeddedValueResolver;

    @Override // org.springframework.context.EmbeddedValueResolverAware
    public void setEmbeddedValueResolver(StringValueResolver resolver) {
        this.embeddedValueResolver = resolver;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Nullable
    public String resolveEmbeddedValue(String value) {
        return this.embeddedValueResolver != null ? this.embeddedValueResolver.resolveStringValue(value) : value;
    }
}
