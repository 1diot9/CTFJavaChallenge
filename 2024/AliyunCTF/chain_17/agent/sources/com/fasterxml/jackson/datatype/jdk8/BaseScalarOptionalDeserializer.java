package com.fasterxml.jackson.datatype.jdk8;

import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.deser.std.StdScalarDeserializer;

/* loaded from: agent.jar:BOOT-INF/lib/jackson-datatype-jdk8-2.15.3.jar:com/fasterxml/jackson/datatype/jdk8/BaseScalarOptionalDeserializer.class */
public abstract class BaseScalarOptionalDeserializer<T> extends StdScalarDeserializer<T> {
    protected final T _empty;

    /* JADX INFO: Access modifiers changed from: protected */
    public BaseScalarOptionalDeserializer(Class<T> cls, T empty) {
        super((Class<?>) cls);
        this._empty = empty;
    }

    @Override // com.fasterxml.jackson.databind.JsonDeserializer, com.fasterxml.jackson.databind.deser.NullValueProvider
    public T getNullValue(DeserializationContext ctxt) {
        return this._empty;
    }
}
