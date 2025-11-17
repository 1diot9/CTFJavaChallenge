package com.fasterxml.jackson.databind.introspect;

import com.fasterxml.jackson.databind.BeanDescription;
import com.fasterxml.jackson.databind.DeserializationConfig;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.SerializationConfig;
import com.fasterxml.jackson.databind.cfg.MapperConfig;

/* loaded from: server.jar:BOOT-INF/lib/jackson-databind-2.15.3.jar:com/fasterxml/jackson/databind/introspect/ClassIntrospector.class */
public abstract class ClassIntrospector {

    /* loaded from: server.jar:BOOT-INF/lib/jackson-databind-2.15.3.jar:com/fasterxml/jackson/databind/introspect/ClassIntrospector$MixInResolver.class */
    public interface MixInResolver {
        Class<?> findMixInClassFor(Class<?> cls);

        MixInResolver copy();
    }

    public abstract ClassIntrospector copy();

    public abstract BeanDescription forSerialization(SerializationConfig serializationConfig, JavaType javaType, MixInResolver mixInResolver);

    public abstract BeanDescription forDeserialization(DeserializationConfig deserializationConfig, JavaType javaType, MixInResolver mixInResolver);

    public abstract BeanDescription forDeserializationWithBuilder(DeserializationConfig deserializationConfig, JavaType javaType, MixInResolver mixInResolver, BeanDescription beanDescription);

    @Deprecated
    public abstract BeanDescription forDeserializationWithBuilder(DeserializationConfig deserializationConfig, JavaType javaType, MixInResolver mixInResolver);

    public abstract BeanDescription forCreation(DeserializationConfig deserializationConfig, JavaType javaType, MixInResolver mixInResolver);

    public abstract BeanDescription forClassAnnotations(MapperConfig<?> mapperConfig, JavaType javaType, MixInResolver mixInResolver);

    public abstract BeanDescription forDirectClassAnnotations(MapperConfig<?> mapperConfig, JavaType javaType, MixInResolver mixInResolver);
}
