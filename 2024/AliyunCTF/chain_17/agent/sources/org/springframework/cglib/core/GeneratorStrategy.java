package org.springframework.cglib.core;

/* loaded from: agent.jar:BOOT-INF/lib/spring-core-6.1.3.jar:org/springframework/cglib/core/GeneratorStrategy.class */
public interface GeneratorStrategy {
    byte[] generate(ClassGenerator cg) throws Exception;

    boolean equals(Object o);
}
