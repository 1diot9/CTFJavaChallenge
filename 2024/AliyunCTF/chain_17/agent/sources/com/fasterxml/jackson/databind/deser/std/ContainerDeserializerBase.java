package com.fasterxml.jackson.databind.deser.std;

import com.fasterxml.jackson.databind.DeserializationConfig;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.deser.NullValueProvider;
import com.fasterxml.jackson.databind.deser.SettableBeanProperty;
import com.fasterxml.jackson.databind.deser.ValueInstantiator;
import com.fasterxml.jackson.databind.deser.impl.NullsConstantProvider;
import com.fasterxml.jackson.databind.type.TypeFactory;
import com.fasterxml.jackson.databind.util.AccessPattern;
import com.fasterxml.jackson.databind.util.ClassUtil;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;

/* loaded from: agent.jar:BOOT-INF/lib/jackson-databind-2.15.3.jar:com/fasterxml/jackson/databind/deser/std/ContainerDeserializerBase.class */
public abstract class ContainerDeserializerBase<T> extends StdDeserializer<T> implements ValueInstantiator.Gettable {
    protected final JavaType _containerType;
    protected final NullValueProvider _nullProvider;
    protected final boolean _skipNullValues;
    protected final Boolean _unwrapSingle;

    public abstract JsonDeserializer<Object> getContentDeserializer();

    /* JADX INFO: Access modifiers changed from: protected */
    public ContainerDeserializerBase(JavaType selfType, NullValueProvider nuller, Boolean unwrapSingle) {
        super(selfType);
        this._containerType = selfType;
        this._unwrapSingle = unwrapSingle;
        this._nullProvider = nuller;
        this._skipNullValues = NullsConstantProvider.isSkipper(nuller);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public ContainerDeserializerBase(JavaType selfType) {
        this(selfType, (NullValueProvider) null, (Boolean) null);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public ContainerDeserializerBase(ContainerDeserializerBase<?> base) {
        this(base, base._nullProvider, base._unwrapSingle);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public ContainerDeserializerBase(ContainerDeserializerBase<?> base, NullValueProvider nuller, Boolean unwrapSingle) {
        super(base._containerType);
        this._containerType = base._containerType;
        this._nullProvider = nuller;
        this._unwrapSingle = unwrapSingle;
        this._skipNullValues = NullsConstantProvider.isSkipper(nuller);
    }

    @Override // com.fasterxml.jackson.databind.deser.std.StdDeserializer
    public JavaType getValueType() {
        return this._containerType;
    }

    @Override // com.fasterxml.jackson.databind.JsonDeserializer
    public Boolean supportsUpdate(DeserializationConfig config) {
        return Boolean.TRUE;
    }

    @Override // com.fasterxml.jackson.databind.JsonDeserializer
    public SettableBeanProperty findBackReference(String refName) {
        JsonDeserializer<Object> valueDeser = getContentDeserializer();
        if (valueDeser == null) {
            throw new IllegalArgumentException(String.format("Cannot handle managed/back reference '%s': type: container deserializer of type %s returned null for 'getContentDeserializer()'", refName, getClass().getName()));
        }
        return valueDeser.findBackReference(refName);
    }

    public JavaType getContentType() {
        if (this._containerType == null) {
            return TypeFactory.unknownType();
        }
        return this._containerType.getContentType();
    }

    @Override // com.fasterxml.jackson.databind.JsonDeserializer
    public AccessPattern getEmptyAccessPattern() {
        return AccessPattern.DYNAMIC;
    }

    @Override // com.fasterxml.jackson.databind.JsonDeserializer
    public Object getEmptyValue(DeserializationContext ctxt) throws JsonMappingException {
        ValueInstantiator vi = getValueInstantiator();
        if (vi == null || !vi.canCreateUsingDefault()) {
            JavaType type = getValueType();
            ctxt.reportBadDefinition(type, String.format("Cannot create empty instance of %s, no default Creator", type));
        }
        try {
            return vi.createUsingDefault(ctxt);
        } catch (IOException e) {
            return ClassUtil.throwAsMappingException(ctxt, e);
        }
    }

    @Deprecated
    protected <BOGUS> BOGUS wrapAndThrow(Throwable th, Object obj, String str) throws IOException {
        return (BOGUS) wrapAndThrow(null, th, obj, str);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public <BOGUS> BOGUS wrapAndThrow(DeserializationContext ctxt, Throwable t, Object ref, String key) throws IOException {
        while ((t instanceof InvocationTargetException) && t.getCause() != null) {
            t = t.getCause();
        }
        ClassUtil.throwIfError(t);
        if (ctxt != null && !ctxt.isEnabled(DeserializationFeature.WRAP_EXCEPTIONS)) {
            ClassUtil.throwIfRTE(t);
        }
        if ((t instanceof IOException) && !(t instanceof JsonMappingException)) {
            throw ((IOException) t);
        }
        throw JsonMappingException.wrapWithPath(t, ref, (String) ClassUtil.nonNull(key, "N/A"));
    }
}
