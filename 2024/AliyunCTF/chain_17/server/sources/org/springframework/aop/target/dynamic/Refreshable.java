package org.springframework.aop.target.dynamic;

/* loaded from: server.jar:BOOT-INF/lib/spring-aop-6.1.3.jar:org/springframework/aop/target/dynamic/Refreshable.class */
public interface Refreshable {
    void refresh();

    long getRefreshCount();

    long getLastRefreshTime();
}
