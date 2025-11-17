package com.fasterxml.jackson.databind.deser.std;

import com.fasterxml.jackson.core.JacksonException;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.databind.BeanProperty;
import com.fasterxml.jackson.databind.DeserializationConfig;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.deser.ContextualDeserializer;
import com.fasterxml.jackson.databind.deser.SettableBeanProperty;
import com.fasterxml.jackson.databind.deser.ValueInstantiator;
import com.fasterxml.jackson.databind.deser.impl.PropertyBasedCreator;
import com.fasterxml.jackson.databind.introspect.AnnotatedMethod;
import com.fasterxml.jackson.databind.jsontype.TypeDeserializer;
import com.fasterxml.jackson.databind.type.LogicalType;
import com.fasterxml.jackson.databind.util.ClassUtil;
import java.io.IOException;

/* loaded from: server.jar:BOOT-INF/lib/jackson-databind-2.15.3.jar:com/fasterxml/jackson/databind/deser/std/FactoryBasedEnumDeserializer.class */
class FactoryBasedEnumDeserializer extends StdDeserializer<Object> implements ContextualDeserializer {
    private static final long serialVersionUID = 1;
    protected final JavaType _inputType;
    protected final AnnotatedMethod _factory;
    protected final JsonDeserializer<?> _deser;
    protected final ValueInstantiator _valueInstantiator;
    protected final SettableBeanProperty[] _creatorProps;
    protected final boolean _hasArgs;
    private volatile transient PropertyBasedCreator _propCreator;

    public FactoryBasedEnumDeserializer(Class<?> cls, AnnotatedMethod f, JavaType paramType, ValueInstantiator valueInstantiator, SettableBeanProperty[] creatorProps) {
        super(cls);
        this._factory = f;
        this._hasArgs = true;
        this._inputType = (paramType.hasRawClass(String.class) || paramType.hasRawClass(CharSequence.class)) ? null : paramType;
        this._deser = null;
        this._valueInstantiator = valueInstantiator;
        this._creatorProps = creatorProps;
    }

    public FactoryBasedEnumDeserializer(Class<?> cls, AnnotatedMethod f) {
        super(cls);
        this._factory = f;
        this._hasArgs = false;
        this._inputType = null;
        this._deser = null;
        this._valueInstantiator = null;
        this._creatorProps = null;
    }

    protected FactoryBasedEnumDeserializer(FactoryBasedEnumDeserializer base, JsonDeserializer<?> deser) {
        super(base._valueClass);
        this._inputType = base._inputType;
        this._factory = base._factory;
        this._hasArgs = base._hasArgs;
        this._valueInstantiator = base._valueInstantiator;
        this._creatorProps = base._creatorProps;
        this._deser = deser;
    }

    @Override // com.fasterxml.jackson.databind.deser.ContextualDeserializer
    public JsonDeserializer<?> createContextual(DeserializationContext ctxt, BeanProperty property) throws JsonMappingException {
        if (this._deser == null && this._inputType != null && this._creatorProps == null) {
            return new FactoryBasedEnumDeserializer(this, (JsonDeserializer<?>) ctxt.findContextualValueDeserializer(this._inputType, property));
        }
        return this;
    }

    @Override // com.fasterxml.jackson.databind.JsonDeserializer
    public Boolean supportsUpdate(DeserializationConfig config) {
        return Boolean.FALSE;
    }

    @Override // com.fasterxml.jackson.databind.JsonDeserializer
    public LogicalType logicalType() {
        return LogicalType.Enum;
    }

    @Override // com.fasterxml.jackson.databind.JsonDeserializer
    public boolean isCachable() {
        return true;
    }

    @Override // com.fasterxml.jackson.databind.deser.std.StdDeserializer, com.fasterxml.jackson.databind.deser.ValueInstantiator.Gettable
    public ValueInstantiator getValueInstantiator() {
        return this._valueInstantiator;
    }

    @Override // com.fasterxml.jackson.databind.JsonDeserializer
    public Object deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
        Object value;
        if (this._deser != null) {
            value = this._deser.deserialize(p, ctxt);
        } else if (this._hasArgs) {
            if (this._creatorProps != null) {
                if (p.isExpectedStartObjectToken()) {
                    PropertyBasedCreator pc = this._propCreator;
                    if (pc == null) {
                        PropertyBasedCreator construct = PropertyBasedCreator.construct(ctxt, this._valueInstantiator, this._creatorProps, ctxt.isEnabled(MapperFeature.ACCEPT_CASE_INSENSITIVE_PROPERTIES));
                        pc = construct;
                        this._propCreator = construct;
                    }
                    p.nextToken();
                    return deserializeEnumUsingPropertyBased(p, ctxt, pc);
                }
                if (!this._valueInstantiator.canCreateFromString()) {
                    JavaType targetType = getValueType(ctxt);
                    ctxt.reportInputMismatch(targetType, "Input mismatch reading Enum %s: properties-based `@JsonCreator` (%s) expects JSON Object (JsonToken.START_OBJECT), got JsonToken.%s", ClassUtil.getTypeDescription(targetType), this._factory, p.currentToken());
                }
            }
            JsonToken t = p.currentToken();
            boolean unwrapping = t == JsonToken.START_ARRAY && ctxt.isEnabled(DeserializationFeature.UNWRAP_SINGLE_VALUE_ARRAYS);
            if (unwrapping) {
                t = p.nextToken();
            }
            if (t == null || !t.isScalarValue()) {
                value = "";
                p.skipChildren();
            } else {
                value = p.getValueAsString();
            }
            if (unwrapping && p.nextToken() != JsonToken.END_ARRAY) {
                handleMissingEndArrayForSingle(p, ctxt);
            }
        } else {
            p.skipChildren();
            try {
                return this._factory.call();
            } catch (Exception e) {
                return ctxt.handleInstantiationProblem(this._valueClass, null, ClassUtil.throwRootCauseIfIOE(e));
            }
        }
        try {
            return this._factory.callOnWith(this._valueClass, value);
        } catch (Exception e2) {
            Throwable t2 = ClassUtil.throwRootCauseIfIOE(e2);
            if ((t2 instanceof IllegalArgumentException) && ctxt.isEnabled(DeserializationFeature.READ_UNKNOWN_ENUM_VALUES_AS_NULL)) {
                return null;
            }
            return ctxt.handleInstantiationProblem(this._valueClass, value, t2);
        }
    }

    @Override // com.fasterxml.jackson.databind.deser.std.StdDeserializer, com.fasterxml.jackson.databind.JsonDeserializer
    public Object deserializeWithType(JsonParser p, DeserializationContext ctxt, TypeDeserializer typeDeserializer) throws IOException {
        return typeDeserializer.deserializeTypedFromAny(p, ctxt);
    }

    /* JADX WARN: Incorrect condition in loop: B:3:0x0014 */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct code enable 'Show inconsistent code' option in preferences
    */
    protected java.lang.Object deserializeEnumUsingPropertyBased(com.fasterxml.jackson.core.JsonParser r8, com.fasterxml.jackson.databind.DeserializationContext r9, com.fasterxml.jackson.databind.deser.impl.PropertyBasedCreator r10) throws java.io.IOException {
        /*
            r7 = this;
            r0 = r10
            r1 = r8
            r2 = r9
            r3 = 0
            com.fasterxml.jackson.databind.deser.impl.PropertyValueBuffer r0 = r0.startBuilding(r1, r2, r3)
            r11 = r0
            r0 = r8
            com.fasterxml.jackson.core.JsonToken r0 = r0.currentToken()
            r12 = r0
        Lf:
            r0 = r12
            com.fasterxml.jackson.core.JsonToken r1 = com.fasterxml.jackson.core.JsonToken.FIELD_NAME
            if (r0 != r1) goto L62
            r0 = r8
            java.lang.String r0 = r0.currentName()
            r13 = r0
            r0 = r8
            com.fasterxml.jackson.core.JsonToken r0 = r0.nextToken()
            r0 = r10
            r1 = r13
            com.fasterxml.jackson.databind.deser.SettableBeanProperty r0 = r0.findCreatorProperty(r1)
            r14 = r0
            r0 = r11
            r1 = r13
            boolean r0 = r0.readIdProperty(r1)
            if (r0 == 0) goto L3c
            r0 = r14
            if (r0 != 0) goto L3c
            goto L59
        L3c:
            r0 = r14
            if (r0 == 0) goto L54
            r0 = r11
            r1 = r14
            r2 = r7
            r3 = r8
            r4 = r9
            r5 = r14
            java.lang.Object r2 = r2._deserializeWithErrorWrapping(r3, r4, r5)
            boolean r0 = r0.assignParameter(r1, r2)
            goto L59
        L54:
            r0 = r8
            com.fasterxml.jackson.core.JsonParser r0 = r0.skipChildren()
        L59:
            r0 = r8
            com.fasterxml.jackson.core.JsonToken r0 = r0.nextToken()
            r12 = r0
            goto Lf
        L62:
            r0 = r10
            r1 = r9
            r2 = r11
            java.lang.Object r0 = r0.build(r1, r2)
            return r0
        */
        throw new UnsupportedOperationException("Method not decompiled: com.fasterxml.jackson.databind.deser.std.FactoryBasedEnumDeserializer.deserializeEnumUsingPropertyBased(com.fasterxml.jackson.core.JsonParser, com.fasterxml.jackson.databind.DeserializationContext, com.fasterxml.jackson.databind.deser.impl.PropertyBasedCreator):java.lang.Object");
    }

    protected final Object _deserializeWithErrorWrapping(JsonParser p, DeserializationContext ctxt, SettableBeanProperty prop) throws IOException {
        try {
            return prop.deserialize(p, ctxt);
        } catch (Exception e) {
            return wrapAndThrow(e, handledType(), prop.getName(), ctxt);
        }
    }

    protected Object wrapAndThrow(Throwable t, Object bean, String fieldName, DeserializationContext ctxt) throws IOException {
        throw JsonMappingException.wrapWithPath(throwOrReturnThrowable(t, ctxt), bean, fieldName);
    }

    private Throwable throwOrReturnThrowable(Throwable t, DeserializationContext ctxt) throws IOException {
        Throwable t2 = ClassUtil.getRootCause(t);
        ClassUtil.throwIfError(t2);
        boolean wrap = ctxt == null || ctxt.isEnabled(DeserializationFeature.WRAP_EXCEPTIONS);
        if (t2 instanceof IOException) {
            if (!wrap || !(t2 instanceof JacksonException)) {
                throw ((IOException) t2);
            }
        } else if (!wrap) {
            ClassUtil.throwIfRTE(t2);
        }
        return t2;
    }
}
