package com.fasterxml.jackson.databind.deser.std;

import com.fasterxml.jackson.core.JacksonException;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.BeanProperty;
import com.fasterxml.jackson.databind.DeserializationConfig;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.deser.ContextualDeserializer;
import com.fasterxml.jackson.databind.deser.ResolvableDeserializer;
import com.fasterxml.jackson.databind.jsontype.TypeDeserializer;
import com.fasterxml.jackson.databind.type.LogicalType;
import com.fasterxml.jackson.databind.util.AccessPattern;
import com.fasterxml.jackson.databind.util.ClassUtil;
import com.fasterxml.jackson.databind.util.Converter;
import com.fasterxml.jackson.databind.util.NameTransformer;
import java.io.IOException;
import java.util.Collection;

/* loaded from: server.jar:BOOT-INF/lib/jackson-databind-2.15.3.jar:com/fasterxml/jackson/databind/deser/std/StdDelegatingDeserializer.class */
public class StdDelegatingDeserializer<T> extends StdDeserializer<T> implements ContextualDeserializer, ResolvableDeserializer {
    private static final long serialVersionUID = 1;
    protected final Converter<Object, T> _converter;
    protected final JavaType _delegateType;
    protected final JsonDeserializer<Object> _delegateDeserializer;

    public StdDelegatingDeserializer(Converter<?, T> converter) {
        super((Class<?>) Object.class);
        this._converter = converter;
        this._delegateType = null;
        this._delegateDeserializer = null;
    }

    public StdDelegatingDeserializer(Converter<Object, T> converter, JavaType delegateType, JsonDeserializer<?> delegateDeserializer) {
        super(delegateType);
        this._converter = converter;
        this._delegateType = delegateType;
        this._delegateDeserializer = delegateDeserializer;
    }

    protected StdDelegatingDeserializer(StdDelegatingDeserializer<T> src) {
        super(src);
        this._converter = src._converter;
        this._delegateType = src._delegateType;
        this._delegateDeserializer = src._delegateDeserializer;
    }

    protected StdDelegatingDeserializer<T> withDelegate(Converter<Object, T> converter, JavaType delegateType, JsonDeserializer<?> delegateDeserializer) {
        ClassUtil.verifyMustOverride(StdDelegatingDeserializer.class, this, "withDelegate");
        return new StdDelegatingDeserializer<>(converter, delegateType, delegateDeserializer);
    }

    @Override // com.fasterxml.jackson.databind.JsonDeserializer
    public JsonDeserializer<T> unwrappingDeserializer(NameTransformer unwrapper) {
        ClassUtil.verifyMustOverride(StdDelegatingDeserializer.class, this, "unwrappingDeserializer");
        return replaceDelegatee(this._delegateDeserializer.unwrappingDeserializer(unwrapper));
    }

    @Override // com.fasterxml.jackson.databind.JsonDeserializer
    public JsonDeserializer<T> replaceDelegatee(JsonDeserializer<?> delegatee) {
        ClassUtil.verifyMustOverride(StdDelegatingDeserializer.class, this, "replaceDelegatee");
        if (delegatee == this._delegateDeserializer) {
            return this;
        }
        return new StdDelegatingDeserializer(this._converter, this._delegateType, delegatee);
    }

    @Override // com.fasterxml.jackson.databind.deser.ResolvableDeserializer
    public void resolve(DeserializationContext ctxt) throws JsonMappingException {
        if (this._delegateDeserializer != null && (this._delegateDeserializer instanceof ResolvableDeserializer)) {
            ((ResolvableDeserializer) this._delegateDeserializer).resolve(ctxt);
        }
    }

    @Override // com.fasterxml.jackson.databind.deser.ContextualDeserializer
    public JsonDeserializer<?> createContextual(DeserializationContext ctxt, BeanProperty property) throws JsonMappingException {
        if (this._delegateDeserializer != null) {
            JsonDeserializer<?> deser = ctxt.handleSecondaryContextualization(this._delegateDeserializer, property, this._delegateType);
            if (deser != this._delegateDeserializer) {
                return withDelegate(this._converter, this._delegateType, deser);
            }
            return this;
        }
        JavaType delegateType = this._converter.getInputType(ctxt.getTypeFactory());
        return withDelegate(this._converter, delegateType, ctxt.findContextualValueDeserializer(delegateType, property));
    }

    @Override // com.fasterxml.jackson.databind.JsonDeserializer
    public T deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
        Object delegateValue = this._delegateDeserializer.deserialize(p, ctxt);
        if (delegateValue == null) {
            return null;
        }
        return convertValue(delegateValue);
    }

    @Override // com.fasterxml.jackson.databind.deser.std.StdDeserializer, com.fasterxml.jackson.databind.JsonDeserializer
    public Object deserializeWithType(JsonParser p, DeserializationContext ctxt, TypeDeserializer typeDeserializer) throws IOException {
        Object delegateValue = this._delegateDeserializer.deserialize(p, ctxt);
        if (delegateValue == null) {
            return null;
        }
        return convertValue(delegateValue);
    }

    @Override // com.fasterxml.jackson.databind.JsonDeserializer
    public T deserialize(JsonParser jsonParser, DeserializationContext deserializationContext, Object obj) throws IOException {
        if (this._delegateType.getRawClass().isAssignableFrom(obj.getClass())) {
            return (T) this._delegateDeserializer.deserialize(jsonParser, deserializationContext, obj);
        }
        return (T) _handleIncompatibleUpdateValue(jsonParser, deserializationContext, obj);
    }

    @Override // com.fasterxml.jackson.databind.JsonDeserializer
    public Object deserializeWithType(JsonParser p, DeserializationContext ctxt, TypeDeserializer typeDeserializer, T intoValue) throws IOException, JacksonException {
        if (!this._delegateType.getRawClass().isAssignableFrom(intoValue.getClass())) {
            return _handleIncompatibleUpdateValue(p, ctxt, intoValue);
        }
        return this._delegateDeserializer.deserialize(p, ctxt, intoValue);
    }

    protected Object _handleIncompatibleUpdateValue(JsonParser p, DeserializationContext ctxt, Object intoValue) throws IOException {
        throw new UnsupportedOperationException(String.format("Cannot update object of type %s (using deserializer for type %s)" + intoValue.getClass().getName(), this._delegateType));
    }

    @Override // com.fasterxml.jackson.databind.deser.std.StdDeserializer, com.fasterxml.jackson.databind.JsonDeserializer
    public Class<?> handledType() {
        return this._delegateDeserializer.handledType();
    }

    @Override // com.fasterxml.jackson.databind.JsonDeserializer
    public LogicalType logicalType() {
        return this._delegateDeserializer.logicalType();
    }

    @Override // com.fasterxml.jackson.databind.JsonDeserializer
    public boolean isCachable() {
        return this._delegateDeserializer != null && this._delegateDeserializer.isCachable();
    }

    @Override // com.fasterxml.jackson.databind.JsonDeserializer
    public JsonDeserializer<?> getDelegatee() {
        return this._delegateDeserializer;
    }

    @Override // com.fasterxml.jackson.databind.JsonDeserializer
    public Collection<Object> getKnownPropertyNames() {
        return this._delegateDeserializer.getKnownPropertyNames();
    }

    @Override // com.fasterxml.jackson.databind.JsonDeserializer, com.fasterxml.jackson.databind.deser.NullValueProvider
    public T getNullValue(DeserializationContext ctxt) throws JsonMappingException {
        return _convertIfNonNull(this._delegateDeserializer.getNullValue(ctxt));
    }

    @Override // com.fasterxml.jackson.databind.JsonDeserializer, com.fasterxml.jackson.databind.deser.NullValueProvider
    public AccessPattern getNullAccessPattern() {
        return this._delegateDeserializer.getNullAccessPattern();
    }

    @Override // com.fasterxml.jackson.databind.JsonDeserializer, com.fasterxml.jackson.databind.deser.NullValueProvider
    public Object getAbsentValue(DeserializationContext ctxt) throws JsonMappingException {
        return _convertIfNonNull(this._delegateDeserializer.getAbsentValue(ctxt));
    }

    @Override // com.fasterxml.jackson.databind.JsonDeserializer
    public Object getEmptyValue(DeserializationContext ctxt) throws JsonMappingException {
        return _convertIfNonNull(this._delegateDeserializer.getEmptyValue(ctxt));
    }

    @Override // com.fasterxml.jackson.databind.JsonDeserializer
    public AccessPattern getEmptyAccessPattern() {
        return this._delegateDeserializer.getEmptyAccessPattern();
    }

    @Override // com.fasterxml.jackson.databind.JsonDeserializer
    public Boolean supportsUpdate(DeserializationConfig config) {
        return this._delegateDeserializer.supportsUpdate(config);
    }

    protected T convertValue(Object delegateValue) {
        return this._converter.convert(delegateValue);
    }

    protected T _convertIfNonNull(Object delegateValue) {
        if (delegateValue == null) {
            return null;
        }
        return this._converter.convert(delegateValue);
    }
}
