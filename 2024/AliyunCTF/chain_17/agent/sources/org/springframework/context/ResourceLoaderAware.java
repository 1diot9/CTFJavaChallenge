package org.springframework.context;

import org.springframework.beans.factory.Aware;
import org.springframework.core.io.ResourceLoader;

/* loaded from: agent.jar:BOOT-INF/lib/spring-context-6.1.3.jar:org/springframework/context/ResourceLoaderAware.class */
public interface ResourceLoaderAware extends Aware {
    void setResourceLoader(ResourceLoader resourceLoader);
}
