package org.springframework.util;

import java.util.UUID;

/* loaded from: server.jar:BOOT-INF/lib/spring-core-6.1.3.jar:org/springframework/util/JdkIdGenerator.class */
public class JdkIdGenerator implements IdGenerator {
    @Override // org.springframework.util.IdGenerator
    public UUID generateId() {
        return UUID.randomUUID();
    }
}
