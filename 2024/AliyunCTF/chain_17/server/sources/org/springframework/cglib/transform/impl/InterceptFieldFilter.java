package org.springframework.cglib.transform.impl;

import org.springframework.asm.Type;

/* loaded from: server.jar:BOOT-INF/lib/spring-core-6.1.3.jar:org/springframework/cglib/transform/impl/InterceptFieldFilter.class */
public interface InterceptFieldFilter {
    boolean acceptRead(Type owner, String name);

    boolean acceptWrite(Type owner, String name);
}
