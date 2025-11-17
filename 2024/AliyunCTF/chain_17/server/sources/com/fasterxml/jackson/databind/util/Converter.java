package com.fasterxml.jackson.databind.util;

import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.type.TypeFactory;

/* loaded from: server.jar:BOOT-INF/lib/jackson-databind-2.15.3.jar:com/fasterxml/jackson/databind/util/Converter.class */
public interface Converter<IN, OUT> {

    /* loaded from: server.jar:BOOT-INF/lib/jackson-databind-2.15.3.jar:com/fasterxml/jackson/databind/util/Converter$None.class */
    public static abstract class None implements Converter<Object, Object> {
    }

    OUT convert(IN in);

    JavaType getInputType(TypeFactory typeFactory);

    JavaType getOutputType(TypeFactory typeFactory);
}
