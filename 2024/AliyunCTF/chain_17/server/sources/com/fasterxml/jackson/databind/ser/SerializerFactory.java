package com.fasterxml.jackson.databind.ser;

import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializationConfig;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.jsontype.TypeSerializer;

/* loaded from: server.jar:BOOT-INF/lib/jackson-databind-2.15.3.jar:com/fasterxml/jackson/databind/ser/SerializerFactory.class */
public abstract class SerializerFactory {
    public abstract SerializerFactory withAdditionalSerializers(Serializers serializers);

    public abstract SerializerFactory withAdditionalKeySerializers(Serializers serializers);

    public abstract SerializerFactory withSerializerModifier(BeanSerializerModifier beanSerializerModifier);

    public abstract JsonSerializer<Object> createSerializer(SerializerProvider serializerProvider, JavaType javaType) throws JsonMappingException;

    public abstract TypeSerializer createTypeSerializer(SerializationConfig serializationConfig, JavaType javaType) throws JsonMappingException;

    @Deprecated
    public abstract JsonSerializer<Object> createKeySerializer(SerializationConfig serializationConfig, JavaType javaType, JsonSerializer<Object> jsonSerializer) throws JsonMappingException;

    public JsonSerializer<Object> createKeySerializer(SerializerProvider prov, JavaType type, JsonSerializer<Object> defaultImpl) throws JsonMappingException {
        return createKeySerializer(prov.getConfig(), type, defaultImpl);
    }
}
