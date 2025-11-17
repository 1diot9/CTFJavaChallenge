package com.fasterxml.jackson.databind.deser;

import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.util.AccessPattern;

/* loaded from: server.jar:BOOT-INF/lib/jackson-databind-2.15.3.jar:com/fasterxml/jackson/databind/deser/NullValueProvider.class */
public interface NullValueProvider {
    Object getNullValue(DeserializationContext deserializationContext) throws JsonMappingException;

    AccessPattern getNullAccessPattern();

    default Object getAbsentValue(DeserializationContext ctxt) throws JsonMappingException {
        return getNullValue(ctxt);
    }
}
