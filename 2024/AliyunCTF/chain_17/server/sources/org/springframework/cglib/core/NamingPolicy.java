package org.springframework.cglib.core;

/* loaded from: server.jar:BOOT-INF/lib/spring-core-6.1.3.jar:org/springframework/cglib/core/NamingPolicy.class */
public interface NamingPolicy {
    String getClassName(String prefix, String source, Object key, Predicate names);

    boolean equals(Object o);
}
