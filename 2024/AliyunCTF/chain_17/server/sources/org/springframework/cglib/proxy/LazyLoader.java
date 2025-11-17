package org.springframework.cglib.proxy;

/* loaded from: server.jar:BOOT-INF/lib/spring-core-6.1.3.jar:org/springframework/cglib/proxy/LazyLoader.class */
public interface LazyLoader extends Callback {
    Object loadObject() throws Exception;
}
