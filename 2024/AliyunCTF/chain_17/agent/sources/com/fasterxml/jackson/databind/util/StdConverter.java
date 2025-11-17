package com.fasterxml.jackson.databind.util;

import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.type.TypeFactory;

/* loaded from: agent.jar:BOOT-INF/lib/jackson-databind-2.15.3.jar:com/fasterxml/jackson/databind/util/StdConverter.class */
public abstract class StdConverter<IN, OUT> implements Converter<IN, OUT> {
    @Override // com.fasterxml.jackson.databind.util.Converter
    public abstract OUT convert(IN in);

    @Override // com.fasterxml.jackson.databind.util.Converter
    public JavaType getInputType(TypeFactory typeFactory) {
        return _findConverterType(typeFactory).containedType(0);
    }

    @Override // com.fasterxml.jackson.databind.util.Converter
    public JavaType getOutputType(TypeFactory typeFactory) {
        return _findConverterType(typeFactory).containedType(1);
    }

    protected JavaType _findConverterType(TypeFactory tf) {
        JavaType thisType = tf.constructType(getClass());
        JavaType convType = thisType.findSuperType(Converter.class);
        if (convType == null || convType.containedTypeCount() < 2) {
            throw new IllegalStateException("Cannot find OUT type parameter for Converter of type " + getClass().getName());
        }
        return convType;
    }
}
