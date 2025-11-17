package org.springframework.cglib.core;

import org.springframework.asm.Type;

/* loaded from: server.jar:BOOT-INF/lib/spring-core-6.1.3.jar:org/springframework/cglib/core/HashCodeCustomizer.class */
public interface HashCodeCustomizer extends KeyFactoryCustomizer {
    boolean customize(CodeEmitter e, Type type);
}
