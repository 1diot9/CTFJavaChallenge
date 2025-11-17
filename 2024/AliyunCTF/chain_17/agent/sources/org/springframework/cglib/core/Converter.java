package org.springframework.cglib.core;

/* loaded from: agent.jar:BOOT-INF/lib/spring-core-6.1.3.jar:org/springframework/cglib/core/Converter.class */
public interface Converter {
    Object convert(Object value, Class target, Object context);
}
