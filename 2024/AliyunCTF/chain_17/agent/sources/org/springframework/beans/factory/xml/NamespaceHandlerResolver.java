package org.springframework.beans.factory.xml;

import org.springframework.lang.Nullable;

@FunctionalInterface
/* loaded from: agent.jar:BOOT-INF/lib/spring-beans-6.1.3.jar:org/springframework/beans/factory/xml/NamespaceHandlerResolver.class */
public interface NamespaceHandlerResolver {
    @Nullable
    NamespaceHandler resolve(String namespaceUri);
}
