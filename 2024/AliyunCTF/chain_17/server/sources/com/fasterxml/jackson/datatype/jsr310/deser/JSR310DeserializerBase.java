package com.fasterxml.jackson.datatype.jsr310.deser;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.core.io.NumberInput;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.deser.std.StdScalarDeserializer;
import com.fasterxml.jackson.databind.jsontype.TypeDeserializer;
import com.fasterxml.jackson.databind.type.LogicalType;
import com.fasterxml.jackson.databind.util.ClassUtil;
import java.io.IOException;
import java.time.DateTimeException;
import java.util.Arrays;

/* loaded from: server.jar:BOOT-INF/lib/jackson-datatype-jsr310-2.15.3.jar:com/fasterxml/jackson/datatype/jsr310/deser/JSR310DeserializerBase.class */
abstract class JSR310DeserializerBase<T> extends StdScalarDeserializer<T> {
    private static final long serialVersionUID = 1;
    protected final boolean _isLenient;

    protected abstract JSR310DeserializerBase<T> withLeniency(Boolean bool);

    /* JADX INFO: Access modifiers changed from: protected */
    public JSR310DeserializerBase(Class<T> supportedType) {
        super((Class<?>) supportedType);
        this._isLenient = true;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public JSR310DeserializerBase(Class<T> supportedType, Boolean leniency) {
        super((Class<?>) supportedType);
        this._isLenient = !Boolean.FALSE.equals(leniency);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public JSR310DeserializerBase(JSR310DeserializerBase<T> base) {
        super(base);
        this._isLenient = base._isLenient;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public JSR310DeserializerBase(JSR310DeserializerBase<T> base, Boolean leniency) {
        super(base);
        this._isLenient = !Boolean.FALSE.equals(leniency);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public boolean isLenient() {
        return this._isLenient;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public T _fromEmptyString(JsonParser jsonParser, DeserializationContext deserializationContext, String str) throws IOException {
        switch (_checkFromStringCoercion(deserializationContext, str)) {
            case AsEmpty:
                return (T) getEmptyValue(deserializationContext);
            case TryConvert:
            case AsNull:
            default:
                if (!this._isLenient) {
                    return _failForNotLenient(jsonParser, deserializationContext, JsonToken.VALUE_STRING);
                }
                return null;
        }
    }

    @Override // com.fasterxml.jackson.databind.deser.std.StdScalarDeserializer, com.fasterxml.jackson.databind.JsonDeserializer
    public LogicalType logicalType() {
        return LogicalType.DateTime;
    }

    @Override // com.fasterxml.jackson.databind.deser.std.StdScalarDeserializer, com.fasterxml.jackson.databind.deser.std.StdDeserializer, com.fasterxml.jackson.databind.JsonDeserializer
    public Object deserializeWithType(JsonParser parser, DeserializationContext context, TypeDeserializer typeDeserializer) throws IOException {
        return typeDeserializer.deserializeTypedFromAny(parser, context);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public boolean _isValidTimestampString(String str) {
        if (_isIntNumber(str)) {
            if (NumberInput.inLongRange(str, str.charAt(0) == '-')) {
                return true;
            }
        }
        return false;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public <BOGUS> BOGUS _reportWrongToken(DeserializationContext context, JsonToken exp, String unit) throws IOException {
        context.reportWrongTokenException(this, exp, "Expected %s for '%s' of %s value", exp.name(), unit, handledType().getName());
        return null;
    }

    protected <BOGUS> BOGUS _reportWrongToken(JsonParser jsonParser, DeserializationContext deserializationContext, JsonToken... jsonTokenArr) throws IOException {
        return (BOGUS) deserializationContext.reportInputMismatch(handledType(), "Unexpected token (%s), expected one of %s for %s value", jsonParser.getCurrentToken(), Arrays.asList(jsonTokenArr).toString(), handledType().getName());
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public <R> R _handleDateTimeException(DeserializationContext deserializationContext, DateTimeException dateTimeException, String str) throws JsonMappingException {
        try {
            return (R) deserializationContext.handleWeirdStringValue(handledType(), str, "Failed to deserialize %s: (%s) %s", handledType().getName(), dateTimeException.getClass().getName(), dateTimeException.getMessage());
        } catch (JsonMappingException e) {
            e.initCause(dateTimeException);
            throw e;
        } catch (IOException e2) {
            if (null == e2.getCause()) {
                e2.initCause(dateTimeException);
            }
            throw JsonMappingException.fromUnexpectedIOE(e2);
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public <R> R _handleUnexpectedToken(DeserializationContext deserializationContext, JsonParser jsonParser, String str, Object... objArr) throws JsonMappingException {
        try {
            return (R) deserializationContext.handleUnexpectedToken(handledType(), jsonParser.getCurrentToken(), jsonParser, str, objArr);
        } catch (JsonMappingException e) {
            throw e;
        } catch (IOException e2) {
            throw JsonMappingException.fromUnexpectedIOE(e2);
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public <R> R _handleUnexpectedToken(DeserializationContext deserializationContext, JsonParser jsonParser, JsonToken... jsonTokenArr) throws JsonMappingException {
        return (R) _handleUnexpectedToken(deserializationContext, jsonParser, "Unexpected token (%s), expected one of %s for %s value", jsonParser.currentToken(), Arrays.asList(jsonTokenArr), handledType().getName());
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public T _failForNotLenient(JsonParser jsonParser, DeserializationContext deserializationContext, JsonToken jsonToken) throws IOException {
        return (T) deserializationContext.handleUnexpectedToken(handledType(), jsonToken, jsonParser, "Cannot deserialize instance of %s out of %s token: not allowed because 'strict' mode set for property or type (enable 'lenient' handling to allow)", ClassUtil.nameOf(handledType()), jsonParser.currentToken());
    }

    protected DateTimeException _peelDTE(DateTimeException e) {
        while (true) {
            Throwable t = e.getCause();
            if (t == null || !(t instanceof DateTimeException)) {
                break;
            }
            e = (DateTimeException) t;
        }
        return e;
    }
}
