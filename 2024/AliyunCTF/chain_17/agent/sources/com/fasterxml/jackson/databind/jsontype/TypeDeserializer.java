package com.fasterxml.jackson.databind.jsontype;

import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.databind.BeanProperty;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JavaType;
import java.io.IOException;

/* loaded from: agent.jar:BOOT-INF/lib/jackson-databind-2.15.3.jar:com/fasterxml/jackson/databind/jsontype/TypeDeserializer.class */
public abstract class TypeDeserializer {
    public abstract TypeDeserializer forProperty(BeanProperty beanProperty);

    public abstract JsonTypeInfo.As getTypeInclusion();

    public abstract String getPropertyName();

    public abstract TypeIdResolver getTypeIdResolver();

    public abstract Class<?> getDefaultImpl();

    public abstract Object deserializeTypedFromObject(JsonParser jsonParser, DeserializationContext deserializationContext) throws IOException;

    public abstract Object deserializeTypedFromArray(JsonParser jsonParser, DeserializationContext deserializationContext) throws IOException;

    public abstract Object deserializeTypedFromScalar(JsonParser jsonParser, DeserializationContext deserializationContext) throws IOException;

    public abstract Object deserializeTypedFromAny(JsonParser jsonParser, DeserializationContext deserializationContext) throws IOException;

    public boolean hasDefaultImpl() {
        return getDefaultImpl() != null;
    }

    public static Object deserializeIfNatural(JsonParser p, DeserializationContext ctxt, JavaType baseType) throws IOException {
        return deserializeIfNatural(p, ctxt, baseType.getRawClass());
    }

    public static Object deserializeIfNatural(JsonParser p, DeserializationContext ctxt, Class<?> base) throws IOException {
        JsonToken t = p.currentToken();
        if (t == null) {
            return null;
        }
        switch (t) {
            case VALUE_STRING:
                if (base.isAssignableFrom(String.class)) {
                    return p.getText();
                }
                return null;
            case VALUE_NUMBER_INT:
                if (base.isAssignableFrom(Integer.class)) {
                    return Integer.valueOf(p.getIntValue());
                }
                return null;
            case VALUE_NUMBER_FLOAT:
                if (base.isAssignableFrom(Double.class)) {
                    return Double.valueOf(p.getDoubleValue());
                }
                return null;
            case VALUE_TRUE:
                if (base.isAssignableFrom(Boolean.class)) {
                    return Boolean.TRUE;
                }
                return null;
            case VALUE_FALSE:
                if (base.isAssignableFrom(Boolean.class)) {
                    return Boolean.FALSE;
                }
                return null;
            default:
                return null;
        }
    }
}
