package org.springframework.cglib.proxy;

import java.lang.reflect.Method;

/* loaded from: server.jar:BOOT-INF/lib/spring-core-6.1.3.jar:org/springframework/cglib/proxy/InvocationHandler.class */
public interface InvocationHandler extends Callback {
    Object invoke(Object proxy, Method method, Object[] args) throws Throwable;
}
