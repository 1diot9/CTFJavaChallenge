package com.fasterxml.jackson.datatype.jsr310.deser;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.core.util.VersionUtil;
import com.fasterxml.jackson.databind.BeanProperty;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.cfg.CoercionAction;
import com.fasterxml.jackson.databind.cfg.CoercionInputShape;
import com.fasterxml.jackson.databind.deser.ContextualDeserializer;
import com.fasterxml.jackson.databind.jsontype.TypeDeserializer;
import com.fasterxml.jackson.databind.type.LogicalType;
import java.io.IOException;
import java.time.DateTimeException;
import java.time.Period;
import java.time.ZoneId;
import java.time.ZoneOffset;

/* loaded from: server.jar:BOOT-INF/lib/jackson-datatype-jsr310-2.15.3.jar:com/fasterxml/jackson/datatype/jsr310/deser/JSR310StringParsableDeserializer.class */
public class JSR310StringParsableDeserializer extends JSR310DeserializerBase<Object> implements ContextualDeserializer {
    private static final long serialVersionUID = 1;
    protected static final int TYPE_PERIOD = 1;
    protected static final int TYPE_ZONE_ID = 2;
    protected static final int TYPE_ZONE_OFFSET = 3;
    public static final JsonDeserializer<Period> PERIOD = createDeserializer(Period.class, 1);
    public static final JsonDeserializer<ZoneId> ZONE_ID = createDeserializer(ZoneId.class, 2);
    public static final JsonDeserializer<ZoneOffset> ZONE_OFFSET = createDeserializer(ZoneOffset.class, 3);
    protected final int _typeSelector;

    @Override // com.fasterxml.jackson.datatype.jsr310.deser.JSR310DeserializerBase, com.fasterxml.jackson.databind.deser.std.StdScalarDeserializer, com.fasterxml.jackson.databind.JsonDeserializer
    public /* bridge */ /* synthetic */ LogicalType logicalType() {
        return super.logicalType();
    }

    protected JSR310StringParsableDeserializer(Class<?> supportedType, int typeSelector) {
        super(supportedType);
        this._typeSelector = typeSelector;
    }

    protected JSR310StringParsableDeserializer(JSR310StringParsableDeserializer base, Boolean leniency) {
        super(base, leniency);
        this._typeSelector = base._typeSelector;
    }

    protected static <T> JsonDeserializer<T> createDeserializer(Class<T> type, int typeId) {
        return new JSR310StringParsableDeserializer((Class<?>) type, typeId);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.fasterxml.jackson.datatype.jsr310.deser.JSR310DeserializerBase
    /* renamed from: withLeniency, reason: merged with bridge method [inline-methods] */
    public JSR310DeserializerBase<Object> withLeniency2(Boolean leniency) {
        if (this._isLenient == (!Boolean.FALSE.equals(leniency))) {
            return this;
        }
        return new JSR310StringParsableDeserializer(this, leniency);
    }

    @Override // com.fasterxml.jackson.databind.deser.ContextualDeserializer
    public JsonDeserializer<?> createContextual(DeserializationContext ctxt, BeanProperty property) throws JsonMappingException {
        Boolean leniency;
        JsonFormat.Value format = findFormatOverrides(ctxt, property, handledType());
        JSR310StringParsableDeserializer deser = this;
        if (format != null && format.hasLenient() && (leniency = format.getLenient()) != null) {
            deser = withLeniency2(leniency);
        }
        return deser;
    }

    @Override // com.fasterxml.jackson.databind.JsonDeserializer
    public Object deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
        if (p.hasToken(JsonToken.VALUE_STRING)) {
            return _fromString(p, ctxt, p.getText());
        }
        if (p.isExpectedStartObjectToken()) {
            return _fromString(p, ctxt, ctxt.extractScalarFromObject(p, this, handledType()));
        }
        if (p.hasToken(JsonToken.VALUE_EMBEDDED_OBJECT)) {
            return p.getEmbeddedObject();
        }
        if (p.isExpectedStartArrayToken()) {
            return _deserializeFromArray(p, ctxt);
        }
        throw ctxt.wrongTokenException(p, handledType(), JsonToken.VALUE_STRING, (String) null);
    }

    @Override // com.fasterxml.jackson.datatype.jsr310.deser.JSR310DeserializerBase, com.fasterxml.jackson.databind.deser.std.StdScalarDeserializer, com.fasterxml.jackson.databind.deser.std.StdDeserializer, com.fasterxml.jackson.databind.JsonDeserializer
    public Object deserializeWithType(JsonParser parser, DeserializationContext context, TypeDeserializer deserializer) throws IOException {
        JsonToken t = parser.getCurrentToken();
        if (t != null && t.isScalarValue()) {
            return deserialize(parser, context);
        }
        return deserializer.deserializeTypedFromAny(parser, context);
    }

    protected Object _fromString(JsonParser p, DeserializationContext ctxt, String string) throws IOException {
        String string2 = string.trim();
        if (string2.length() == 0) {
            CoercionAction act = ctxt.findCoercionAction(logicalType(), this._valueClass, CoercionInputShape.EmptyString);
            if (act == CoercionAction.Fail) {
                ctxt.reportInputMismatch(this, "Cannot coerce empty String (\"\") to %s (but could if enabling coercion using `CoercionConfig`)", _coercedTypeDesc());
            }
            if (!isLenient()) {
                return _failForNotLenient(p, ctxt, JsonToken.VALUE_STRING);
            }
            if (act == CoercionAction.AsEmpty) {
                return getEmptyValue(ctxt);
            }
            return null;
        }
        try {
            switch (this._typeSelector) {
                case 1:
                    return Period.parse(string2);
                case 2:
                    return ZoneId.of(string2);
                case 3:
                    return ZoneOffset.of(string2);
                default:
                    VersionUtil.throwInternal();
                    return null;
            }
        } catch (DateTimeException e) {
            return _handleDateTimeException(ctxt, e, string2);
        }
    }
}
