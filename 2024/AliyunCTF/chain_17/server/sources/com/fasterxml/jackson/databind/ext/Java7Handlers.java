package com.fasterxml.jackson.databind.ext;

import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonSerializer;

/* loaded from: server.jar:BOOT-INF/lib/jackson-databind-2.15.3.jar:com/fasterxml/jackson/databind/ext/Java7Handlers.class */
public abstract class Java7Handlers {
    private static final Java7Handlers IMPL = new Java7HandlersImpl();

    public abstract Class<?> getClassJavaNioFilePath();

    public abstract JsonDeserializer<?> getDeserializerForJavaNioFilePath(Class<?> cls);

    public abstract JsonSerializer<?> getSerializerForJavaNioFilePath(Class<?> cls);

    public static Java7Handlers instance() {
        return IMPL;
    }
}
