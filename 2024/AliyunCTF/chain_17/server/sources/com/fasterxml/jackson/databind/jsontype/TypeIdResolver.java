package com.fasterxml.jackson.databind.jsontype;

import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.databind.DatabindContext;
import com.fasterxml.jackson.databind.JavaType;
import java.io.IOException;

/* loaded from: server.jar:BOOT-INF/lib/jackson-databind-2.15.3.jar:com/fasterxml/jackson/databind/jsontype/TypeIdResolver.class */
public interface TypeIdResolver {
    void init(JavaType javaType);

    String idFromValue(Object obj);

    String idFromValueAndType(Object obj, Class<?> cls);

    String idFromBaseType();

    JavaType typeFromId(DatabindContext databindContext, String str) throws IOException;

    String getDescForKnownTypeIds();

    JsonTypeInfo.Id getMechanism();
}
