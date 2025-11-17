package com.fasterxml.jackson.databind.jsontype.impl;

import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.core.util.JsonParserSequence;
import com.fasterxml.jackson.databind.BeanProperty;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.jsontype.TypeDeserializer;
import com.fasterxml.jackson.databind.jsontype.TypeIdResolver;
import com.fasterxml.jackson.databind.util.TokenBuffer;
import java.io.IOException;

/* loaded from: agent.jar:BOOT-INF/lib/jackson-databind-2.15.3.jar:com/fasterxml/jackson/databind/jsontype/impl/AsPropertyTypeDeserializer.class */
public class AsPropertyTypeDeserializer extends AsArrayTypeDeserializer {
    private static final long serialVersionUID = 1;
    protected final JsonTypeInfo.As _inclusion;
    protected final boolean _strictTypeIdHandling;
    protected final String _msgForMissingId;

    @Deprecated
    public AsPropertyTypeDeserializer(JavaType bt, TypeIdResolver idRes, String typePropertyName, boolean typeIdVisible, JavaType defaultImpl) {
        this(bt, idRes, typePropertyName, typeIdVisible, defaultImpl, JsonTypeInfo.As.PROPERTY);
    }

    @Deprecated
    public AsPropertyTypeDeserializer(JavaType bt, TypeIdResolver idRes, String typePropertyName, boolean typeIdVisible, JavaType defaultImpl, JsonTypeInfo.As inclusion) {
        this(bt, idRes, typePropertyName, typeIdVisible, defaultImpl, inclusion, true);
    }

    public AsPropertyTypeDeserializer(AsPropertyTypeDeserializer src, BeanProperty property) {
        super(src, property);
        this._msgForMissingId = this._property == null ? String.format("missing type id property '%s'", this._typePropertyName) : String.format("missing type id property '%s' (for POJO property '%s')", this._typePropertyName, this._property.getName());
        this._inclusion = src._inclusion;
        this._strictTypeIdHandling = src._strictTypeIdHandling;
    }

    public AsPropertyTypeDeserializer(JavaType bt, TypeIdResolver idRes, String typePropertyName, boolean typeIdVisible, JavaType defaultImpl, JsonTypeInfo.As inclusion, boolean strictTypeIdHandling) {
        super(bt, idRes, typePropertyName, typeIdVisible, defaultImpl);
        this._msgForMissingId = this._property == null ? String.format("missing type id property '%s'", this._typePropertyName) : String.format("missing type id property '%s' (for POJO property '%s')", this._typePropertyName, this._property.getName());
        this._inclusion = inclusion;
        this._strictTypeIdHandling = strictTypeIdHandling;
    }

    @Override // com.fasterxml.jackson.databind.jsontype.impl.AsArrayTypeDeserializer, com.fasterxml.jackson.databind.jsontype.impl.TypeDeserializerBase, com.fasterxml.jackson.databind.jsontype.TypeDeserializer
    public TypeDeserializer forProperty(BeanProperty prop) {
        return prop == this._property ? this : new AsPropertyTypeDeserializer(this, prop);
    }

    @Override // com.fasterxml.jackson.databind.jsontype.impl.AsArrayTypeDeserializer, com.fasterxml.jackson.databind.jsontype.impl.TypeDeserializerBase, com.fasterxml.jackson.databind.jsontype.TypeDeserializer
    public JsonTypeInfo.As getTypeInclusion() {
        return this._inclusion;
    }

    @Override // com.fasterxml.jackson.databind.jsontype.impl.AsArrayTypeDeserializer, com.fasterxml.jackson.databind.jsontype.TypeDeserializer
    public Object deserializeTypedFromObject(JsonParser p, DeserializationContext ctxt) throws IOException {
        String typeId;
        Object typeId2;
        if (p.canReadTypeId() && (typeId2 = p.getTypeId()) != null) {
            return _deserializeWithNativeTypeId(p, ctxt, typeId2);
        }
        JsonToken t = p.currentToken();
        if (t == JsonToken.START_OBJECT) {
            t = p.nextToken();
        } else if (t != JsonToken.FIELD_NAME) {
            return _deserializeTypedUsingDefaultImpl(p, ctxt, null, this._msgForMissingId);
        }
        TokenBuffer tb = null;
        boolean ignoreCase = ctxt.isEnabled(MapperFeature.ACCEPT_CASE_INSENSITIVE_PROPERTIES);
        while (t == JsonToken.FIELD_NAME) {
            String name = p.currentName();
            p.nextToken();
            if ((name.equals(this._typePropertyName) || (ignoreCase && name.equalsIgnoreCase(this._typePropertyName))) && (typeId = p.getValueAsString()) != null) {
                return _deserializeTypedForId(p, ctxt, tb, typeId);
            }
            if (tb == null) {
                tb = ctxt.bufferForInputBuffering(p);
            }
            tb.writeFieldName(name);
            tb.copyCurrentStructure(p);
            t = p.nextToken();
        }
        return _deserializeTypedUsingDefaultImpl(p, ctxt, tb, this._msgForMissingId);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public Object _deserializeTypedForId(JsonParser p, DeserializationContext ctxt, TokenBuffer tb, String typeId) throws IOException {
        JsonDeserializer<Object> deser = _findDeserializer(ctxt, typeId);
        if (this._typeIdVisible) {
            if (tb == null) {
                tb = ctxt.bufferForInputBuffering(p);
            }
            tb.writeFieldName(p.currentName());
            tb.writeString(typeId);
        }
        if (tb != null) {
            p.clearCurrentToken();
            p = JsonParserSequence.createFlattened(false, tb.asParser(p), p);
        }
        if (p.currentToken() != JsonToken.END_OBJECT) {
            p.nextToken();
        }
        return deser.deserialize(p, ctxt);
    }

    @Deprecated
    protected Object _deserializeTypedUsingDefaultImpl(JsonParser p, DeserializationContext ctxt, TokenBuffer tb) throws IOException {
        return _deserializeTypedUsingDefaultImpl(p, ctxt, tb, null);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public Object _deserializeTypedUsingDefaultImpl(JsonParser p, DeserializationContext ctxt, TokenBuffer tb, String priorFailureMsg) throws IOException {
        if (!hasDefaultImpl()) {
            Object result = TypeDeserializer.deserializeIfNatural(p, ctxt, this._baseType);
            if (result != null) {
                return result;
            }
            if (p.isExpectedStartArrayToken()) {
                return super.deserializeTypedFromAny(p, ctxt);
            }
            if (p.hasToken(JsonToken.VALUE_STRING) && ctxt.isEnabled(DeserializationFeature.ACCEPT_EMPTY_STRING_AS_NULL_OBJECT)) {
                String str = p.getText().trim();
                if (str.isEmpty()) {
                    return null;
                }
            }
        }
        JsonDeserializer<Object> deser = _findDefaultImplDeserializer(ctxt);
        if (deser == null) {
            JavaType t = this._strictTypeIdHandling ? _handleMissingTypeId(ctxt, priorFailureMsg) : this._baseType;
            if (t == null) {
                return null;
            }
            deser = ctxt.findContextualValueDeserializer(t, this._property);
        }
        if (tb != null) {
            tb.writeEndObject();
            p = tb.asParser(p);
            p.nextToken();
        }
        return deser.deserialize(p, ctxt);
    }

    @Override // com.fasterxml.jackson.databind.jsontype.impl.AsArrayTypeDeserializer, com.fasterxml.jackson.databind.jsontype.TypeDeserializer
    public Object deserializeTypedFromAny(JsonParser p, DeserializationContext ctxt) throws IOException {
        if (p.hasToken(JsonToken.START_ARRAY)) {
            return super.deserializeTypedFromArray(p, ctxt);
        }
        return deserializeTypedFromObject(p, ctxt);
    }
}
