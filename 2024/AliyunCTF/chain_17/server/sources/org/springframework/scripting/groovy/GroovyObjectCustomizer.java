package org.springframework.scripting.groovy;

import groovy.lang.GroovyObject;

@FunctionalInterface
/* loaded from: server.jar:BOOT-INF/lib/spring-context-6.1.3.jar:org/springframework/scripting/groovy/GroovyObjectCustomizer.class */
public interface GroovyObjectCustomizer {
    void customize(GroovyObject goo);
}
