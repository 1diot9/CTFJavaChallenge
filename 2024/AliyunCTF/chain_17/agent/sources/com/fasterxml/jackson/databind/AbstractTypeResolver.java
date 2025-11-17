package com.fasterxml.jackson.databind;

/* loaded from: agent.jar:BOOT-INF/lib/jackson-databind-2.15.3.jar:com/fasterxml/jackson/databind/AbstractTypeResolver.class */
public abstract class AbstractTypeResolver {
    public JavaType findTypeMapping(DeserializationConfig config, JavaType type) {
        return null;
    }

    @Deprecated
    public JavaType resolveAbstractType(DeserializationConfig config, JavaType type) {
        return null;
    }

    public JavaType resolveAbstractType(DeserializationConfig config, BeanDescription typeDesc) {
        return null;
    }
}
