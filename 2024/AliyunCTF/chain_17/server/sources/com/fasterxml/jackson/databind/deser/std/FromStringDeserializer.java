package com.fasterxml.jackson.databind.deser.std;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.core.util.VersionUtil;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.cfg.CoercionAction;
import com.fasterxml.jackson.databind.cfg.CoercionInputShape;
import com.fasterxml.jackson.databind.exc.InvalidFormatException;
import com.fasterxml.jackson.databind.type.LogicalType;
import com.fasterxml.jackson.databind.util.ClassUtil;
import java.io.File;
import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.Currency;
import java.util.IllformedLocaleException;
import java.util.Locale;
import java.util.TimeZone;
import java.util.regex.Pattern;
import org.springframework.beans.PropertyAccessor;

/* loaded from: server.jar:BOOT-INF/lib/jackson-databind-2.15.3.jar:com/fasterxml/jackson/databind/deser/std/FromStringDeserializer.class */
public abstract class FromStringDeserializer<T> extends StdScalarDeserializer<T> {
    /* JADX INFO: Access modifiers changed from: protected */
    public abstract T _deserialize(String str, DeserializationContext deserializationContext) throws IOException;

    public static Class<?>[] types() {
        return new Class[]{File.class, URL.class, URI.class, Class.class, JavaType.class, Currency.class, Pattern.class, Locale.class, Charset.class, TimeZone.class, InetAddress.class, InetSocketAddress.class, StringBuilder.class, StringBuffer.class};
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public FromStringDeserializer(Class<?> vc) {
        super(vc);
    }

    public static FromStringDeserializer<?> findDeserializer(Class<?> rawType) {
        int kind;
        if (rawType == File.class) {
            kind = 1;
        } else if (rawType == URL.class) {
            kind = 2;
        } else if (rawType == URI.class) {
            kind = 3;
        } else if (rawType == Class.class) {
            kind = 4;
        } else if (rawType == JavaType.class) {
            kind = 5;
        } else if (rawType == Currency.class) {
            kind = 6;
        } else if (rawType == Pattern.class) {
            kind = 7;
        } else if (rawType == Locale.class) {
            kind = 8;
        } else if (rawType == Charset.class) {
            kind = 9;
        } else if (rawType == TimeZone.class) {
            kind = 10;
        } else if (rawType == InetAddress.class) {
            kind = 11;
        } else if (rawType == InetSocketAddress.class) {
            kind = 12;
        } else {
            if (rawType == StringBuilder.class) {
                return new StringBuilderDeserializer();
            }
            if (rawType == StringBuffer.class) {
                return new StringBufferDeserializer();
            }
            return null;
        }
        return new Std(rawType, kind);
    }

    @Override // com.fasterxml.jackson.databind.deser.std.StdScalarDeserializer, com.fasterxml.jackson.databind.JsonDeserializer
    public LogicalType logicalType() {
        return LogicalType.OtherScalar;
    }

    @Override // com.fasterxml.jackson.databind.JsonDeserializer
    public T deserialize(JsonParser jsonParser, DeserializationContext deserializationContext) throws IOException {
        String valueAsString = jsonParser.getValueAsString();
        if (valueAsString == null) {
            JsonToken currentToken = jsonParser.currentToken();
            if (currentToken != JsonToken.START_OBJECT) {
                return (T) _deserializeFromOther(jsonParser, deserializationContext, currentToken);
            }
            valueAsString = deserializationContext.extractScalarFromObject(jsonParser, this, this._valueClass);
        }
        if (valueAsString.isEmpty()) {
            return (T) _deserializeFromEmptyString(deserializationContext);
        }
        if (_shouldTrim()) {
            String str = valueAsString;
            valueAsString = valueAsString.trim();
            if (valueAsString != str && valueAsString.isEmpty()) {
                return (T) _deserializeFromEmptyString(deserializationContext);
            }
        }
        try {
            return _deserialize(valueAsString, deserializationContext);
        } catch (IllegalArgumentException | MalformedURLException e) {
            String str2 = "not a valid textual representation";
            String message = e.getMessage();
            if (message != null) {
                str2 = str2 + ", problem: " + message;
            }
            throw deserializationContext.weirdStringException(valueAsString, this._valueClass, str2).withCause(e);
        }
    }

    protected boolean _shouldTrim() {
        return true;
    }

    protected Object _deserializeFromOther(JsonParser p, DeserializationContext ctxt, JsonToken t) throws IOException {
        if (t == JsonToken.START_ARRAY) {
            return _deserializeFromArray(p, ctxt);
        }
        if (t == JsonToken.VALUE_EMBEDDED_OBJECT) {
            Object ob = p.getEmbeddedObject();
            if (ob == null) {
                return null;
            }
            if (this._valueClass.isAssignableFrom(ob.getClass())) {
                return ob;
            }
            return _deserializeEmbedded(ob, ctxt);
        }
        return ctxt.handleUnexpectedToken(this._valueClass, p);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public T _deserializeEmbedded(Object ob, DeserializationContext ctxt) throws IOException {
        ctxt.reportInputMismatch(this, "Don't know how to convert embedded Object of type %s into %s", ob.getClass().getName(), this._valueClass.getName());
        return null;
    }

    @Deprecated
    protected final T _deserializeFromEmptyString() throws IOException {
        return null;
    }

    protected Object _deserializeFromEmptyString(DeserializationContext ctxt) throws IOException {
        CoercionAction act = ctxt.findCoercionAction(logicalType(), this._valueClass, CoercionInputShape.EmptyString);
        if (act == CoercionAction.Fail) {
            ctxt.reportInputMismatch(this, "Cannot coerce empty String (\"\") to %s (but could if enabling coercion using `CoercionConfig`)", _coercedTypeDesc());
        }
        if (act == CoercionAction.AsNull) {
            return getNullValue(ctxt);
        }
        if (act == CoercionAction.AsEmpty) {
            return getEmptyValue(ctxt);
        }
        return _deserializeFromEmptyStringDefault(ctxt);
    }

    protected Object _deserializeFromEmptyStringDefault(DeserializationContext ctxt) throws IOException {
        return getNullValue(ctxt);
    }

    /* loaded from: server.jar:BOOT-INF/lib/jackson-databind-2.15.3.jar:com/fasterxml/jackson/databind/deser/std/FromStringDeserializer$Std.class */
    public static class Std extends FromStringDeserializer<Object> {
        private static final long serialVersionUID = 1;
        public static final int STD_FILE = 1;
        public static final int STD_URL = 2;
        public static final int STD_URI = 3;
        public static final int STD_CLASS = 4;
        public static final int STD_JAVA_TYPE = 5;
        public static final int STD_CURRENCY = 6;
        public static final int STD_PATTERN = 7;
        public static final int STD_LOCALE = 8;
        public static final int STD_CHARSET = 9;
        public static final int STD_TIME_ZONE = 10;
        public static final int STD_INET_ADDRESS = 11;
        public static final int STD_INET_SOCKET_ADDRESS = 12;
        protected static final String LOCALE_EXT_MARKER = "_#";
        protected final int _kind;

        protected Std(Class<?> valueType, int kind) {
            super(valueType);
            this._kind = kind;
        }

        @Override // com.fasterxml.jackson.databind.deser.std.FromStringDeserializer
        protected Object _deserialize(String value, DeserializationContext ctxt) throws IOException {
            switch (this._kind) {
                case 1:
                    return new File(value);
                case 2:
                    return new URL(value);
                case 3:
                    return URI.create(value);
                case 4:
                    try {
                        return ctxt.findClass(value);
                    } catch (Exception e) {
                        return ctxt.handleInstantiationProblem(this._valueClass, value, ClassUtil.getRootCause(e));
                    }
                case 5:
                    return ctxt.getTypeFactory().constructFromCanonical(value);
                case 6:
                    return Currency.getInstance(value);
                case 7:
                    return Pattern.compile(value);
                case 8:
                    return _deserializeLocale(value, ctxt);
                case 9:
                    return Charset.forName(value);
                case 10:
                    return TimeZone.getTimeZone(value);
                case 11:
                    return InetAddress.getByName(value);
                case 12:
                    if (value.startsWith(PropertyAccessor.PROPERTY_KEY_PREFIX)) {
                        int i = value.lastIndexOf(93);
                        if (i == -1) {
                            throw new InvalidFormatException(ctxt.getParser(), "Bracketed IPv6 address must contain closing bracket", value, (Class<?>) InetSocketAddress.class);
                        }
                        int j = value.indexOf(58, i);
                        int port = j > -1 ? Integer.parseInt(value.substring(j + 1)) : 0;
                        return new InetSocketAddress(value.substring(0, i + 1), port);
                    }
                    int ix = value.indexOf(58);
                    if (ix >= 0 && value.indexOf(58, ix + 1) < 0) {
                        int port2 = Integer.parseInt(value.substring(ix + 1));
                        return new InetSocketAddress(value.substring(0, ix), port2);
                    }
                    return new InetSocketAddress(value, 0);
                default:
                    VersionUtil.throwInternal();
                    return null;
            }
        }

        @Override // com.fasterxml.jackson.databind.JsonDeserializer
        public Object getEmptyValue(DeserializationContext ctxt) throws JsonMappingException {
            switch (this._kind) {
                case 3:
                    return URI.create("");
                case 8:
                    return Locale.ROOT;
                default:
                    return super.getEmptyValue(ctxt);
            }
        }

        @Override // com.fasterxml.jackson.databind.deser.std.FromStringDeserializer
        protected Object _deserializeFromEmptyStringDefault(DeserializationContext ctxt) throws IOException {
            return getEmptyValue(ctxt);
        }

        @Override // com.fasterxml.jackson.databind.deser.std.FromStringDeserializer
        protected boolean _shouldTrim() {
            return this._kind != 7;
        }

        protected int _firstHyphenOrUnderscore(String str) {
            int end = str.length();
            for (int i = 0; i < end; i++) {
                char c = str.charAt(i);
                if (c == '_' || c == '-') {
                    return i;
                }
            }
            return -1;
        }

        private Locale _deserializeLocale(String value, DeserializationContext ctxt) throws IOException {
            int ix = _firstHyphenOrUnderscore(value);
            if (ix < 0) {
                return new Locale(value);
            }
            String first = value.substring(0, ix);
            String value2 = value.substring(ix + 1);
            int ix2 = _firstHyphenOrUnderscore(value2);
            if (ix2 < 0) {
                return new Locale(first, value2);
            }
            String second = value2.substring(0, ix2);
            int extMarkerIx = value2.indexOf(LOCALE_EXT_MARKER);
            if (extMarkerIx < 0) {
                return new Locale(first, second, value2.substring(ix2 + 1));
            }
            return _deSerializeBCP47Locale(value2, ix2, first, second, extMarkerIx);
        }

        private Locale _deSerializeBCP47Locale(String value, int ix, String first, String second, int extMarkerIx) {
            String third = "";
            if (extMarkerIx > 0 && extMarkerIx > ix) {
                try {
                    third = value.substring(ix + 1, extMarkerIx);
                } catch (IllformedLocaleException e) {
                    return new Locale(first, second, third);
                }
            }
            String value2 = value.substring(extMarkerIx + 2);
            int underscoreIx = value2.indexOf(95);
            if (underscoreIx < 0) {
                int hyphenIx = value2.indexOf(45);
                if (hyphenIx < 0) {
                    return new Locale.Builder().setLanguage(first).setRegion(second).setVariant(third).setScript(value2).build();
                }
                return new Locale.Builder().setLanguage(first).setRegion(second).setVariant(third).setExtension(value2.charAt(0), value2.substring(hyphenIx + 1)).build();
            }
            int len = value2.length();
            Locale.Builder b = new Locale.Builder().setLanguage(first).setRegion(second).setVariant(third).setScript(value2.substring(0, underscoreIx));
            if (underscoreIx + 1 < len) {
                b = b.setExtension(value2.charAt(underscoreIx + 1), value2.substring(Math.min(len, underscoreIx + 3)));
            }
            return b.build();
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* loaded from: server.jar:BOOT-INF/lib/jackson-databind-2.15.3.jar:com/fasterxml/jackson/databind/deser/std/FromStringDeserializer$StringBuilderDeserializer.class */
    public static class StringBuilderDeserializer extends FromStringDeserializer<Object> {
        public StringBuilderDeserializer() {
            super(StringBuilder.class);
        }

        @Override // com.fasterxml.jackson.databind.deser.std.FromStringDeserializer, com.fasterxml.jackson.databind.deser.std.StdScalarDeserializer, com.fasterxml.jackson.databind.JsonDeserializer
        public LogicalType logicalType() {
            return LogicalType.Textual;
        }

        @Override // com.fasterxml.jackson.databind.JsonDeserializer
        public Object getEmptyValue(DeserializationContext ctxt) throws JsonMappingException {
            return new StringBuilder();
        }

        @Override // com.fasterxml.jackson.databind.deser.std.FromStringDeserializer, com.fasterxml.jackson.databind.JsonDeserializer
        public Object deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
            String text = p.getValueAsString();
            if (text != null) {
                return _deserialize(text, ctxt);
            }
            return super.deserialize(p, ctxt);
        }

        @Override // com.fasterxml.jackson.databind.deser.std.FromStringDeserializer
        protected Object _deserialize(String value, DeserializationContext ctxt) throws IOException {
            return new StringBuilder(value);
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* loaded from: server.jar:BOOT-INF/lib/jackson-databind-2.15.3.jar:com/fasterxml/jackson/databind/deser/std/FromStringDeserializer$StringBufferDeserializer.class */
    public static class StringBufferDeserializer extends FromStringDeserializer<Object> {
        public StringBufferDeserializer() {
            super(StringBuffer.class);
        }

        @Override // com.fasterxml.jackson.databind.deser.std.FromStringDeserializer, com.fasterxml.jackson.databind.deser.std.StdScalarDeserializer, com.fasterxml.jackson.databind.JsonDeserializer
        public LogicalType logicalType() {
            return LogicalType.Textual;
        }

        @Override // com.fasterxml.jackson.databind.JsonDeserializer
        public Object getEmptyValue(DeserializationContext ctxt) {
            return new StringBuffer();
        }

        @Override // com.fasterxml.jackson.databind.deser.std.FromStringDeserializer, com.fasterxml.jackson.databind.JsonDeserializer
        public Object deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
            String text = p.getValueAsString();
            if (text != null) {
                return _deserialize(text, ctxt);
            }
            return super.deserialize(p, ctxt);
        }

        @Override // com.fasterxml.jackson.databind.deser.std.FromStringDeserializer
        protected Object _deserialize(String value, DeserializationContext ctxt) throws IOException {
            return new StringBuffer(value);
        }
    }
}
