package org.springframework.util;

import java.util.UUID;

@FunctionalInterface
/* loaded from: agent.jar:BOOT-INF/lib/spring-core-6.1.3.jar:org/springframework/util/IdGenerator.class */
public interface IdGenerator {
    UUID generateId();
}
