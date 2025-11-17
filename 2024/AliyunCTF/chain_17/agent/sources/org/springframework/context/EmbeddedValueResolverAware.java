package org.springframework.context;

import org.springframework.beans.factory.Aware;
import org.springframework.util.StringValueResolver;

/* loaded from: agent.jar:BOOT-INF/lib/spring-context-6.1.3.jar:org/springframework/context/EmbeddedValueResolverAware.class */
public interface EmbeddedValueResolverAware extends Aware {
    void setEmbeddedValueResolver(StringValueResolver resolver);
}
