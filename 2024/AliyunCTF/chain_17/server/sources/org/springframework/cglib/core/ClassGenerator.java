package org.springframework.cglib.core;

import org.springframework.asm.ClassVisitor;

/* loaded from: server.jar:BOOT-INF/lib/spring-core-6.1.3.jar:org/springframework/cglib/core/ClassGenerator.class */
public interface ClassGenerator {
    void generateClass(ClassVisitor v) throws Exception;
}
