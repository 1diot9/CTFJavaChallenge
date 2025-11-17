package com.fasterxml.jackson.databind;

import com.fasterxml.jackson.core.JacksonException;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.deser.NullValueProvider;
import com.fasterxml.jackson.databind.deser.SettableBeanProperty;
import com.fasterxml.jackson.databind.deser.impl.ObjectIdReader;
import com.fasterxml.jackson.databind.jsontype.TypeDeserializer;
import com.fasterxml.jackson.databind.type.LogicalType;
import com.fasterxml.jackson.databind.util.AccessPattern;
import com.fasterxml.jackson.databind.util.NameTransformer;
import java.io.IOException;
import java.util.Collection;

/* loaded from: agent.jar:BOOT-INF/lib/jackson-databind-2.15.3.jar:com/fasterxml/jackson/databind/JsonDeserializer.class */
public abstract class JsonDeserializer<T> implements NullValueProvider {
    public abstract T deserialize(JsonParser jsonParser, DeserializationContext deserializationContext) throws IOException, JacksonException;

    public T deserialize(JsonParser p, DeserializationContext ctxt, T intoValue) throws IOException, JacksonException {
        ctxt.handleBadMerge(this);
        return deserialize(p, ctxt);
    }

    public Object deserializeWithType(JsonParser p, DeserializationContext ctxt, TypeDeserializer typeDeserializer) throws IOException, JacksonException {
        return typeDeserializer.deserializeTypedFromAny(p, ctxt);
    }

    public Object deserializeWithType(JsonParser p, DeserializationContext ctxt, TypeDeserializer typeDeserializer, T intoValue) throws IOException, JacksonException {
        ctxt.handleBadMerge(this);
        return deserializeWithType(p, ctxt, typeDeserializer);
    }

    public JsonDeserializer<T> unwrappingDeserializer(NameTransformer unwrapper) {
        return this;
    }

    public JsonDeserializer<?> replaceDelegatee(JsonDeserializer<?> delegatee) {
        throw new UnsupportedOperationException();
    }

    public Class<?> handledType() {
        return null;
    }

    public LogicalType logicalType() {
        return null;
    }

    public boolean isCachable() {
        return false;
    }

    public JsonDeserializer<?> getDelegatee() {
        return null;
    }

    public Collection<Object> getKnownPropertyNames() {
        return null;
    }

    @Override // com.fasterxml.jackson.databind.deser.NullValueProvider
    public T getNullValue(DeserializationContext ctxt) throws JsonMappingException {
        return getNullValue();
    }

    @Override // com.fasterxml.jackson.databind.deser.NullValueProvider
    public AccessPattern getNullAccessPattern() {
        return AccessPattern.CONSTANT;
    }

    @Override // com.fasterxml.jackson.databind.deser.NullValueProvider
    public Object getAbsentValue(DeserializationContext ctxt) throws JsonMappingException {
        return getNullValue(ctxt);
    }

    public Object getEmptyValue(DeserializationContext ctxt) throws JsonMappingException {
        return getNullValue(ctxt);
    }

    public AccessPattern getEmptyAccessPattern() {
        return AccessPattern.DYNAMIC;
    }

    public ObjectIdReader getObjectIdReader() {
        return null;
    }

    public SettableBeanProperty findBackReference(String refName) {
        throw new IllegalArgumentException("Cannot handle managed/back reference '" + refName + "': type: value deserializer of type " + getClass().getName() + " does not support them");
    }

    public Boolean supportsUpdate(DeserializationConfig config) {
        return null;
    }

    @Deprecated
    public T getNullValue() {
        return null;
    }

    @Deprecated
    public Object getEmptyValue() {
        return getNullValue();
    }

    /* loaded from: agent.jar:BOOT-INF/lib/jackson-databind-2.15.3.jar:com/fasterxml/jackson/databind/JsonDeserializer$None.class */
    public static abstract class None extends JsonDeserializer<Object> {
        private None() {
        }
    }
}
