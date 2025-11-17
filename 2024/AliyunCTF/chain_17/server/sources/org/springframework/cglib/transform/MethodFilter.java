package org.springframework.cglib.transform;

/* loaded from: server.jar:BOOT-INF/lib/spring-core-6.1.3.jar:org/springframework/cglib/transform/MethodFilter.class */
public interface MethodFilter {
    boolean accept(int access, String name, String desc, String signature, String[] exceptions);
}
