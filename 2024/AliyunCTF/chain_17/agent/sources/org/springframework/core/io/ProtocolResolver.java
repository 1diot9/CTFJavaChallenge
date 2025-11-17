package org.springframework.core.io;

import org.springframework.lang.Nullable;

@FunctionalInterface
/* loaded from: agent.jar:BOOT-INF/lib/spring-core-6.1.3.jar:org/springframework/core/io/ProtocolResolver.class */
public interface ProtocolResolver {
    @Nullable
    Resource resolve(String location, ResourceLoader resourceLoader);
}
