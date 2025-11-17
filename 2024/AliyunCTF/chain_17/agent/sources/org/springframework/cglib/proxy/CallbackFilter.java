package org.springframework.cglib.proxy;

import java.lang.reflect.Method;

/* loaded from: agent.jar:BOOT-INF/lib/spring-core-6.1.3.jar:org/springframework/cglib/proxy/CallbackFilter.class */
public interface CallbackFilter {
    int accept(Method method);

    boolean equals(Object o);
}
