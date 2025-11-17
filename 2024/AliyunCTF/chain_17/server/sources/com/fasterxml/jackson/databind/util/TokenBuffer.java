package com.fasterxml.jackson.databind.util;

import com.fasterxml.jackson.core.Base64Variant;
import com.fasterxml.jackson.core.JacksonException;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonLocation;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonStreamContext;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.core.ObjectCodec;
import com.fasterxml.jackson.core.SerializableString;
import com.fasterxml.jackson.core.StreamReadCapability;
import com.fasterxml.jackson.core.StreamReadConstraints;
import com.fasterxml.jackson.core.StreamReadFeature;
import com.fasterxml.jackson.core.StreamWriteCapability;
import com.fasterxml.jackson.core.TreeNode;
import com.fasterxml.jackson.core.Version;
import com.fasterxml.jackson.core.base.ParserMinimalBase;
import com.fasterxml.jackson.core.io.NumberInput;
import com.fasterxml.jackson.core.json.JsonWriteContext;
import com.fasterxml.jackson.core.util.ByteArrayBuilder;
import com.fasterxml.jackson.core.util.JacksonFeatureSet;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonSerializable;
import com.fasterxml.jackson.databind.cfg.PackageVersion;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Reader;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.TreeMap;

/* loaded from: server.jar:BOOT-INF/lib/jackson-databind-2.15.3.jar:com/fasterxml/jackson/databind/util/TokenBuffer.class */
public class TokenBuffer extends JsonGenerator {
    protected static final int DEFAULT_GENERATOR_FEATURES = JsonGenerator.Feature.collectDefaults();
    protected ObjectCodec _objectCodec;
    protected JsonStreamContext _parentContext;
    protected int _generatorFeatures;
    protected StreamReadConstraints _streamReadConstraints;
    protected boolean _closed;
    protected boolean _hasNativeTypeIds;
    protected boolean _hasNativeObjectIds;
    protected boolean _mayHaveNativeIds;
    protected boolean _forceBigDecimal;
    protected Segment _first;
    protected Segment _last;
    protected int _appendAt;
    protected Object _typeId;
    protected Object _objectId;
    protected boolean _hasNativeId;
    protected JsonWriteContext _writeContext;

    public TokenBuffer(ObjectCodec codec, boolean hasNativeIds) {
        this._streamReadConstraints = StreamReadConstraints.defaults();
        this._hasNativeId = false;
        this._objectCodec = codec;
        this._generatorFeatures = DEFAULT_GENERATOR_FEATURES;
        this._writeContext = JsonWriteContext.createRootContext(null);
        Segment segment = new Segment();
        this._last = segment;
        this._first = segment;
        this._appendAt = 0;
        this._hasNativeTypeIds = hasNativeIds;
        this._hasNativeObjectIds = hasNativeIds;
        this._mayHaveNativeIds = this._hasNativeTypeIds || this._hasNativeObjectIds;
    }

    public TokenBuffer(JsonParser p) {
        this(p, (DeserializationContext) null);
    }

    public TokenBuffer(JsonParser p, DeserializationContext ctxt) {
        this._streamReadConstraints = StreamReadConstraints.defaults();
        this._hasNativeId = false;
        this._objectCodec = p.getCodec();
        this._streamReadConstraints = p.streamReadConstraints();
        this._parentContext = p.getParsingContext();
        this._generatorFeatures = DEFAULT_GENERATOR_FEATURES;
        this._writeContext = JsonWriteContext.createRootContext(null);
        Segment segment = new Segment();
        this._last = segment;
        this._first = segment;
        this._appendAt = 0;
        this._hasNativeTypeIds = p.canReadTypeId();
        this._hasNativeObjectIds = p.canReadObjectId();
        this._mayHaveNativeIds = this._hasNativeTypeIds || this._hasNativeObjectIds;
        this._forceBigDecimal = ctxt == null ? false : ctxt.isEnabled(DeserializationFeature.USE_BIG_DECIMAL_FOR_FLOATS);
    }

    @Deprecated
    public static TokenBuffer asCopyOfValue(JsonParser p) throws IOException {
        TokenBuffer b = new TokenBuffer(p);
        b.copyCurrentStructure(p);
        return b;
    }

    public TokenBuffer overrideParentContext(JsonStreamContext ctxt) {
        this._parentContext = ctxt;
        return this;
    }

    public TokenBuffer forceUseOfBigDecimal(boolean b) {
        this._forceBigDecimal = b;
        return this;
    }

    @Override // com.fasterxml.jackson.core.JsonGenerator, com.fasterxml.jackson.core.Versioned
    public Version version() {
        return PackageVersion.VERSION;
    }

    public JsonParser asParser() {
        return asParser(this._objectCodec);
    }

    public JsonParser asParserOnFirstToken() throws IOException {
        JsonParser p = asParser(this._objectCodec);
        p.nextToken();
        return p;
    }

    public JsonParser asParser(ObjectCodec codec) {
        return new Parser(this._first, codec, this._hasNativeTypeIds, this._hasNativeObjectIds, this._parentContext, this._streamReadConstraints);
    }

    public JsonParser asParser(StreamReadConstraints streamReadConstraints) {
        return new Parser(this._first, this._objectCodec, this._hasNativeTypeIds, this._hasNativeObjectIds, this._parentContext, streamReadConstraints);
    }

    public JsonParser asParser(JsonParser src) {
        Parser p = new Parser(this._first, src.getCodec(), this._hasNativeTypeIds, this._hasNativeObjectIds, this._parentContext, src.streamReadConstraints());
        p.setLocation(src.getTokenLocation());
        return p;
    }

    public JsonToken firstToken() {
        return this._first.type(0);
    }

    public boolean isEmpty() {
        return this._appendAt == 0 && this._first == this._last;
    }

    public TokenBuffer append(TokenBuffer other) throws IOException {
        if (!this._hasNativeTypeIds) {
            this._hasNativeTypeIds = other.canWriteTypeId();
        }
        if (!this._hasNativeObjectIds) {
            this._hasNativeObjectIds = other.canWriteObjectId();
        }
        this._mayHaveNativeIds = this._hasNativeTypeIds || this._hasNativeObjectIds;
        JsonParser p = other.asParser();
        while (p.nextToken() != null) {
            copyCurrentStructure(p);
        }
        return this;
    }

    public void serialize(JsonGenerator gen) throws IOException {
        Segment segment = this._first;
        int ptr = -1;
        boolean checkIds = this._mayHaveNativeIds;
        boolean hasIds = checkIds && segment.hasIds();
        while (true) {
            ptr++;
            if (ptr >= 16) {
                ptr = 0;
                segment = segment.next();
                if (segment != null) {
                    hasIds = checkIds && segment.hasIds();
                } else {
                    return;
                }
            }
            JsonToken t = segment.type(ptr);
            if (t != null) {
                if (hasIds) {
                    Object id = segment.findObjectId(ptr);
                    if (id != null) {
                        gen.writeObjectId(id);
                    }
                    Object id2 = segment.findTypeId(ptr);
                    if (id2 != null) {
                        gen.writeTypeId(id2);
                    }
                }
                switch (t) {
                    case START_OBJECT:
                        gen.writeStartObject();
                        break;
                    case END_OBJECT:
                        gen.writeEndObject();
                        break;
                    case START_ARRAY:
                        gen.writeStartArray();
                        break;
                    case END_ARRAY:
                        gen.writeEndArray();
                        break;
                    case FIELD_NAME:
                        Object ob = segment.get(ptr);
                        if (ob instanceof SerializableString) {
                            gen.writeFieldName((SerializableString) ob);
                            break;
                        } else {
                            gen.writeFieldName((String) ob);
                            break;
                        }
                    case VALUE_STRING:
                        Object ob2 = segment.get(ptr);
                        if (ob2 instanceof SerializableString) {
                            gen.writeString((SerializableString) ob2);
                            break;
                        } else {
                            gen.writeString((String) ob2);
                            break;
                        }
                    case VALUE_NUMBER_INT:
                        Object n = segment.get(ptr);
                        if (n instanceof Integer) {
                            gen.writeNumber(((Integer) n).intValue());
                            break;
                        } else if (n instanceof BigInteger) {
                            gen.writeNumber((BigInteger) n);
                            break;
                        } else if (n instanceof Long) {
                            gen.writeNumber(((Long) n).longValue());
                            break;
                        } else if (n instanceof Short) {
                            gen.writeNumber(((Short) n).shortValue());
                            break;
                        } else {
                            gen.writeNumber(((Number) n).intValue());
                            break;
                        }
                    case VALUE_NUMBER_FLOAT:
                        Object n2 = segment.get(ptr);
                        if (n2 instanceof Double) {
                            gen.writeNumber(((Double) n2).doubleValue());
                            break;
                        } else if (n2 instanceof BigDecimal) {
                            gen.writeNumber((BigDecimal) n2);
                            break;
                        } else if (n2 instanceof Float) {
                            gen.writeNumber(((Float) n2).floatValue());
                            break;
                        } else if (n2 == null) {
                            gen.writeNull();
                            break;
                        } else if (n2 instanceof String) {
                            gen.writeNumber((String) n2);
                            break;
                        } else {
                            _reportError(String.format("Unrecognized value type for VALUE_NUMBER_FLOAT: %s, cannot serialize", n2.getClass().getName()));
                            break;
                        }
                    case VALUE_TRUE:
                        gen.writeBoolean(true);
                        break;
                    case VALUE_FALSE:
                        gen.writeBoolean(false);
                        break;
                    case VALUE_NULL:
                        gen.writeNull();
                        break;
                    case VALUE_EMBEDDED_OBJECT:
                        Object value = segment.get(ptr);
                        if (value instanceof RawValue) {
                            ((RawValue) value).serialize(gen);
                            break;
                        } else if (value instanceof JsonSerializable) {
                            gen.writeObject(value);
                            break;
                        } else {
                            gen.writeEmbeddedObject(value);
                            break;
                        }
                    default:
                        throw new RuntimeException("Internal error: should never end up through this code path");
                }
            } else {
                return;
            }
        }
    }

    public TokenBuffer deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
        JsonToken t;
        if (!p.hasToken(JsonToken.FIELD_NAME)) {
            copyCurrentStructure(p);
            return this;
        }
        writeStartObject();
        do {
            copyCurrentStructure(p);
            t = p.nextToken();
        } while (t == JsonToken.FIELD_NAME);
        if (t != JsonToken.END_OBJECT) {
            ctxt.reportWrongTokenException(TokenBuffer.class, JsonToken.END_OBJECT, "Expected END_OBJECT after copying contents of a JsonParser into TokenBuffer, got " + t, new Object[0]);
        }
        writeEndObject();
        return this;
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("[TokenBuffer: ");
        JsonParser jp = asParser();
        int count = 0;
        boolean hasNativeIds = this._hasNativeTypeIds || this._hasNativeObjectIds;
        while (true) {
            try {
                JsonToken t = jp.nextToken();
                if (t == null) {
                    break;
                }
                if (hasNativeIds) {
                    _appendNativeIds(sb);
                }
                if (count < 100) {
                    if (count > 0) {
                        sb.append(", ");
                    }
                    sb.append(t.toString());
                    if (t == JsonToken.FIELD_NAME) {
                        sb.append('(');
                        sb.append(jp.currentName());
                        sb.append(')');
                    }
                }
                count++;
            } catch (IOException ioe) {
                throw new IllegalStateException(ioe);
            }
        }
        if (count >= 100) {
            sb.append(" ... (truncated ").append(count - 100).append(" entries)");
        }
        sb.append(']');
        return sb.toString();
    }

    private final void _appendNativeIds(StringBuilder sb) {
        Object objectId = this._last.findObjectId(this._appendAt - 1);
        if (objectId != null) {
            sb.append("[objectId=").append(String.valueOf(objectId)).append(']');
        }
        Object typeId = this._last.findTypeId(this._appendAt - 1);
        if (typeId != null) {
            sb.append("[typeId=").append(String.valueOf(typeId)).append(']');
        }
    }

    @Override // com.fasterxml.jackson.core.JsonGenerator
    public JsonGenerator enable(JsonGenerator.Feature f) {
        this._generatorFeatures |= f.getMask();
        return this;
    }

    @Override // com.fasterxml.jackson.core.JsonGenerator
    public JsonGenerator disable(JsonGenerator.Feature f) {
        this._generatorFeatures &= f.getMask() ^ (-1);
        return this;
    }

    @Override // com.fasterxml.jackson.core.JsonGenerator
    public boolean isEnabled(JsonGenerator.Feature f) {
        return (this._generatorFeatures & f.getMask()) != 0;
    }

    @Override // com.fasterxml.jackson.core.JsonGenerator
    public int getFeatureMask() {
        return this._generatorFeatures;
    }

    @Override // com.fasterxml.jackson.core.JsonGenerator
    @Deprecated
    public JsonGenerator setFeatureMask(int mask) {
        this._generatorFeatures = mask;
        return this;
    }

    @Override // com.fasterxml.jackson.core.JsonGenerator
    public JsonGenerator overrideStdFeatures(int values, int mask) {
        int oldState = getFeatureMask();
        this._generatorFeatures = (oldState & (mask ^ (-1))) | (values & mask);
        return this;
    }

    @Override // com.fasterxml.jackson.core.JsonGenerator
    public JsonGenerator useDefaultPrettyPrinter() {
        return this;
    }

    @Override // com.fasterxml.jackson.core.JsonGenerator
    public JsonGenerator setCodec(ObjectCodec oc) {
        this._objectCodec = oc;
        return this;
    }

    @Override // com.fasterxml.jackson.core.JsonGenerator
    public ObjectCodec getCodec() {
        return this._objectCodec;
    }

    @Override // com.fasterxml.jackson.core.JsonGenerator
    public final JsonWriteContext getOutputContext() {
        return this._writeContext;
    }

    @Override // com.fasterxml.jackson.core.JsonGenerator
    public boolean canWriteBinaryNatively() {
        return true;
    }

    @Override // com.fasterxml.jackson.core.JsonGenerator
    public JacksonFeatureSet<StreamWriteCapability> getWriteCapabilities() {
        return DEFAULT_WRITE_CAPABILITIES;
    }

    @Override // com.fasterxml.jackson.core.JsonGenerator, java.io.Flushable
    public void flush() throws IOException {
    }

    @Override // com.fasterxml.jackson.core.JsonGenerator, java.io.Closeable, java.lang.AutoCloseable
    public void close() throws IOException {
        this._closed = true;
    }

    @Override // com.fasterxml.jackson.core.JsonGenerator
    public boolean isClosed() {
        return this._closed;
    }

    @Override // com.fasterxml.jackson.core.JsonGenerator
    public final void writeStartArray() throws IOException {
        this._writeContext.writeValue();
        _appendStartMarker(JsonToken.START_ARRAY);
        this._writeContext = this._writeContext.createChildArrayContext();
    }

    @Override // com.fasterxml.jackson.core.JsonGenerator
    public void writeStartArray(Object forValue) throws IOException {
        this._writeContext.writeValue();
        _appendStartMarker(JsonToken.START_ARRAY);
        this._writeContext = this._writeContext.createChildArrayContext(forValue);
    }

    @Override // com.fasterxml.jackson.core.JsonGenerator
    public void writeStartArray(Object forValue, int size) throws IOException {
        this._writeContext.writeValue();
        _appendStartMarker(JsonToken.START_ARRAY);
        this._writeContext = this._writeContext.createChildArrayContext(forValue);
    }

    @Override // com.fasterxml.jackson.core.JsonGenerator
    public final void writeEndArray() throws IOException {
        _appendEndMarker(JsonToken.END_ARRAY);
        JsonWriteContext c = this._writeContext.getParent();
        if (c != null) {
            this._writeContext = c;
        }
    }

    @Override // com.fasterxml.jackson.core.JsonGenerator
    public final void writeStartObject() throws IOException {
        this._writeContext.writeValue();
        _appendStartMarker(JsonToken.START_OBJECT);
        this._writeContext = this._writeContext.createChildObjectContext();
    }

    @Override // com.fasterxml.jackson.core.JsonGenerator
    public void writeStartObject(Object forValue) throws IOException {
        this._writeContext.writeValue();
        _appendStartMarker(JsonToken.START_OBJECT);
        JsonWriteContext ctxt = this._writeContext.createChildObjectContext(forValue);
        this._writeContext = ctxt;
    }

    @Override // com.fasterxml.jackson.core.JsonGenerator
    public void writeStartObject(Object forValue, int size) throws IOException {
        this._writeContext.writeValue();
        _appendStartMarker(JsonToken.START_OBJECT);
        JsonWriteContext ctxt = this._writeContext.createChildObjectContext(forValue);
        this._writeContext = ctxt;
    }

    @Override // com.fasterxml.jackson.core.JsonGenerator
    public final void writeEndObject() throws IOException {
        _appendEndMarker(JsonToken.END_OBJECT);
        JsonWriteContext c = this._writeContext.getParent();
        if (c != null) {
            this._writeContext = c;
        }
    }

    @Override // com.fasterxml.jackson.core.JsonGenerator
    public final void writeFieldName(String name) throws IOException {
        this._writeContext.writeFieldName(name);
        _appendFieldName(name);
    }

    @Override // com.fasterxml.jackson.core.JsonGenerator
    public void writeFieldName(SerializableString name) throws IOException {
        this._writeContext.writeFieldName(name.getValue());
        _appendFieldName(name);
    }

    @Override // com.fasterxml.jackson.core.JsonGenerator
    public void writeString(String text) throws IOException {
        if (text == null) {
            writeNull();
        } else {
            _appendValue(JsonToken.VALUE_STRING, text);
        }
    }

    @Override // com.fasterxml.jackson.core.JsonGenerator
    public void writeString(char[] text, int offset, int len) throws IOException {
        writeString(new String(text, offset, len));
    }

    @Override // com.fasterxml.jackson.core.JsonGenerator
    public void writeString(SerializableString text) throws IOException {
        if (text == null) {
            writeNull();
        } else {
            _appendValue(JsonToken.VALUE_STRING, text);
        }
    }

    @Override // com.fasterxml.jackson.core.JsonGenerator
    public void writeString(Reader reader, int len) throws IOException {
        if (reader == null) {
            _reportError("null reader");
        }
        int toRead = len >= 0 ? len : Integer.MAX_VALUE;
        char[] buf = new char[1000];
        StringBuilder sb = new StringBuilder(1000);
        while (toRead > 0) {
            int toReadNow = Math.min(toRead, buf.length);
            int numRead = reader.read(buf, 0, toReadNow);
            if (numRead <= 0) {
                break;
            }
            sb.append(buf, 0, numRead);
            toRead -= numRead;
        }
        if (toRead > 0 && len >= 0) {
            _reportError("Was not able to read enough from reader");
        }
        _appendValue(JsonToken.VALUE_STRING, sb.toString());
    }

    @Override // com.fasterxml.jackson.core.JsonGenerator
    public void writeRawUTF8String(byte[] text, int offset, int length) throws IOException {
        _reportUnsupportedOperation();
    }

    @Override // com.fasterxml.jackson.core.JsonGenerator
    public void writeUTF8String(byte[] text, int offset, int length) throws IOException {
        _reportUnsupportedOperation();
    }

    @Override // com.fasterxml.jackson.core.JsonGenerator
    public void writeRaw(String text) throws IOException {
        _reportUnsupportedOperation();
    }

    @Override // com.fasterxml.jackson.core.JsonGenerator
    public void writeRaw(String text, int offset, int len) throws IOException {
        _reportUnsupportedOperation();
    }

    @Override // com.fasterxml.jackson.core.JsonGenerator
    public void writeRaw(SerializableString text) throws IOException {
        _reportUnsupportedOperation();
    }

    @Override // com.fasterxml.jackson.core.JsonGenerator
    public void writeRaw(char[] text, int offset, int len) throws IOException {
        _reportUnsupportedOperation();
    }

    @Override // com.fasterxml.jackson.core.JsonGenerator
    public void writeRaw(char c) throws IOException {
        _reportUnsupportedOperation();
    }

    @Override // com.fasterxml.jackson.core.JsonGenerator
    public void writeRawValue(String text) throws IOException {
        _appendValue(JsonToken.VALUE_EMBEDDED_OBJECT, new RawValue(text));
    }

    @Override // com.fasterxml.jackson.core.JsonGenerator
    public void writeRawValue(String text, int offset, int len) throws IOException {
        if (offset > 0 || len != text.length()) {
            text = text.substring(offset, offset + len);
        }
        _appendValue(JsonToken.VALUE_EMBEDDED_OBJECT, new RawValue(text));
    }

    @Override // com.fasterxml.jackson.core.JsonGenerator
    public void writeRawValue(char[] text, int offset, int len) throws IOException {
        _appendValue(JsonToken.VALUE_EMBEDDED_OBJECT, new String(text, offset, len));
    }

    @Override // com.fasterxml.jackson.core.JsonGenerator
    public void writeNumber(short i) throws IOException {
        _appendValue(JsonToken.VALUE_NUMBER_INT, Short.valueOf(i));
    }

    @Override // com.fasterxml.jackson.core.JsonGenerator
    public void writeNumber(int i) throws IOException {
        _appendValue(JsonToken.VALUE_NUMBER_INT, Integer.valueOf(i));
    }

    @Override // com.fasterxml.jackson.core.JsonGenerator
    public void writeNumber(long l) throws IOException {
        _appendValue(JsonToken.VALUE_NUMBER_INT, Long.valueOf(l));
    }

    @Override // com.fasterxml.jackson.core.JsonGenerator
    public void writeNumber(double d) throws IOException {
        _appendValue(JsonToken.VALUE_NUMBER_FLOAT, Double.valueOf(d));
    }

    @Override // com.fasterxml.jackson.core.JsonGenerator
    public void writeNumber(float f) throws IOException {
        _appendValue(JsonToken.VALUE_NUMBER_FLOAT, Float.valueOf(f));
    }

    @Override // com.fasterxml.jackson.core.JsonGenerator
    public void writeNumber(BigDecimal dec) throws IOException {
        if (dec == null) {
            writeNull();
        } else {
            _appendValue(JsonToken.VALUE_NUMBER_FLOAT, dec);
        }
    }

    @Override // com.fasterxml.jackson.core.JsonGenerator
    public void writeNumber(BigInteger v) throws IOException {
        if (v == null) {
            writeNull();
        } else {
            _appendValue(JsonToken.VALUE_NUMBER_INT, v);
        }
    }

    @Override // com.fasterxml.jackson.core.JsonGenerator
    public void writeNumber(String encodedValue) throws IOException {
        _appendValue(JsonToken.VALUE_NUMBER_FLOAT, encodedValue);
    }

    private void writeLazyInteger(Object encodedValue) throws IOException {
        _appendValue(JsonToken.VALUE_NUMBER_INT, encodedValue);
    }

    private void writeLazyDecimal(Object encodedValue) throws IOException {
        _appendValue(JsonToken.VALUE_NUMBER_FLOAT, encodedValue);
    }

    @Override // com.fasterxml.jackson.core.JsonGenerator
    public void writeBoolean(boolean state) throws IOException {
        _appendValue(state ? JsonToken.VALUE_TRUE : JsonToken.VALUE_FALSE);
    }

    @Override // com.fasterxml.jackson.core.JsonGenerator
    public void writeNull() throws IOException {
        _appendValue(JsonToken.VALUE_NULL);
    }

    @Override // com.fasterxml.jackson.core.JsonGenerator
    public void writeObject(Object value) throws IOException {
        if (value == null) {
            writeNull();
            return;
        }
        Class<?> raw = value.getClass();
        if (raw == byte[].class || (value instanceof RawValue)) {
            _appendValue(JsonToken.VALUE_EMBEDDED_OBJECT, value);
        } else if (this._objectCodec == null) {
            _appendValue(JsonToken.VALUE_EMBEDDED_OBJECT, value);
        } else {
            this._objectCodec.writeValue(this, value);
        }
    }

    @Override // com.fasterxml.jackson.core.JsonGenerator
    public void writeTree(TreeNode node) throws IOException {
        if (node == null) {
            writeNull();
        } else if (this._objectCodec == null) {
            _appendValue(JsonToken.VALUE_EMBEDDED_OBJECT, node);
        } else {
            this._objectCodec.writeTree(this, node);
        }
    }

    @Override // com.fasterxml.jackson.core.JsonGenerator
    public void writeBinary(Base64Variant b64variant, byte[] data, int offset, int len) throws IOException {
        byte[] copy = new byte[len];
        System.arraycopy(data, offset, copy, 0, len);
        writeObject(copy);
    }

    @Override // com.fasterxml.jackson.core.JsonGenerator
    public int writeBinary(Base64Variant b64variant, InputStream data, int dataLength) {
        throw new UnsupportedOperationException();
    }

    @Override // com.fasterxml.jackson.core.JsonGenerator
    public boolean canWriteTypeId() {
        return this._hasNativeTypeIds;
    }

    @Override // com.fasterxml.jackson.core.JsonGenerator
    public boolean canWriteObjectId() {
        return this._hasNativeObjectIds;
    }

    @Override // com.fasterxml.jackson.core.JsonGenerator
    public void writeTypeId(Object id) {
        this._typeId = id;
        this._hasNativeId = true;
    }

    @Override // com.fasterxml.jackson.core.JsonGenerator
    public void writeObjectId(Object id) {
        this._objectId = id;
        this._hasNativeId = true;
    }

    @Override // com.fasterxml.jackson.core.JsonGenerator
    public void writeEmbeddedObject(Object object) throws IOException {
        _appendValue(JsonToken.VALUE_EMBEDDED_OBJECT, object);
    }

    @Override // com.fasterxml.jackson.core.JsonGenerator
    public void copyCurrentEvent(JsonParser p) throws IOException {
        if (this._mayHaveNativeIds) {
            _checkNativeIds(p);
        }
        switch (p.currentToken()) {
            case START_OBJECT:
                writeStartObject();
                return;
            case END_OBJECT:
                writeEndObject();
                return;
            case START_ARRAY:
                writeStartArray();
                return;
            case END_ARRAY:
                writeEndArray();
                return;
            case FIELD_NAME:
                writeFieldName(p.currentName());
                return;
            case VALUE_STRING:
                if (p.hasTextCharacters()) {
                    writeString(p.getTextCharacters(), p.getTextOffset(), p.getTextLength());
                    return;
                } else {
                    writeString(p.getText());
                    return;
                }
            case VALUE_NUMBER_INT:
                switch (p.getNumberType()) {
                    case INT:
                        writeNumber(p.getIntValue());
                        return;
                    case BIG_INTEGER:
                        writeLazyInteger(p.getNumberValueDeferred());
                        return;
                    default:
                        writeNumber(p.getLongValue());
                        return;
                }
            case VALUE_NUMBER_FLOAT:
                writeLazyDecimal(p.getNumberValueDeferred());
                return;
            case VALUE_TRUE:
                writeBoolean(true);
                return;
            case VALUE_FALSE:
                writeBoolean(false);
                return;
            case VALUE_NULL:
                writeNull();
                return;
            case VALUE_EMBEDDED_OBJECT:
                writeObject(p.getEmbeddedObject());
                return;
            default:
                throw new RuntimeException("Internal error: unexpected token: " + p.currentToken());
        }
    }

    @Override // com.fasterxml.jackson.core.JsonGenerator
    public void copyCurrentStructure(JsonParser p) throws IOException {
        JsonToken t = p.currentToken();
        if (t == JsonToken.FIELD_NAME) {
            if (this._mayHaveNativeIds) {
                _checkNativeIds(p);
            }
            writeFieldName(p.currentName());
            t = p.nextToken();
        } else if (t == null) {
            throw new IllegalStateException("No token available from argument `JsonParser`");
        }
        switch (t) {
            case START_OBJECT:
                if (this._mayHaveNativeIds) {
                    _checkNativeIds(p);
                }
                writeStartObject();
                _copyBufferContents(p);
                return;
            case END_OBJECT:
                writeEndObject();
                return;
            case START_ARRAY:
                if (this._mayHaveNativeIds) {
                    _checkNativeIds(p);
                }
                writeStartArray();
                _copyBufferContents(p);
                return;
            case END_ARRAY:
                writeEndArray();
                return;
            default:
                _copyBufferValue(p, t);
                return;
        }
    }

    protected void _copyBufferContents(JsonParser p) throws IOException {
        int depth = 1;
        while (true) {
            JsonToken t = p.nextToken();
            if (t != null) {
                switch (t) {
                    case START_OBJECT:
                        if (this._mayHaveNativeIds) {
                            _checkNativeIds(p);
                        }
                        writeStartObject();
                        depth++;
                        break;
                    case END_OBJECT:
                        writeEndObject();
                        depth--;
                        if (depth != 0) {
                            break;
                        } else {
                            return;
                        }
                    case START_ARRAY:
                        if (this._mayHaveNativeIds) {
                            _checkNativeIds(p);
                        }
                        writeStartArray();
                        depth++;
                        break;
                    case END_ARRAY:
                        writeEndArray();
                        depth--;
                        if (depth != 0) {
                            break;
                        } else {
                            return;
                        }
                    case FIELD_NAME:
                        if (this._mayHaveNativeIds) {
                            _checkNativeIds(p);
                        }
                        writeFieldName(p.currentName());
                        break;
                    default:
                        _copyBufferValue(p, t);
                        break;
                }
            } else {
                return;
            }
        }
    }

    private void _copyBufferValue(JsonParser p, JsonToken t) throws IOException {
        if (this._mayHaveNativeIds) {
            _checkNativeIds(p);
        }
        switch (t) {
            case VALUE_STRING:
                if (p.hasTextCharacters()) {
                    writeString(p.getTextCharacters(), p.getTextOffset(), p.getTextLength());
                    return;
                } else {
                    writeString(p.getText());
                    return;
                }
            case VALUE_NUMBER_INT:
                switch (p.getNumberType()) {
                    case INT:
                        writeNumber(p.getIntValue());
                        return;
                    case BIG_INTEGER:
                        writeLazyInteger(p.getNumberValueDeferred());
                        return;
                    default:
                        writeNumber(p.getLongValue());
                        return;
                }
            case VALUE_NUMBER_FLOAT:
                writeLazyDecimal(p.getNumberValueDeferred());
                return;
            case VALUE_TRUE:
                writeBoolean(true);
                return;
            case VALUE_FALSE:
                writeBoolean(false);
                return;
            case VALUE_NULL:
                writeNull();
                return;
            case VALUE_EMBEDDED_OBJECT:
                writeObject(p.getEmbeddedObject());
                return;
            default:
                throw new RuntimeException("Internal error: unexpected token: " + t);
        }
    }

    private final void _checkNativeIds(JsonParser p) throws IOException {
        Object typeId = p.getTypeId();
        this._typeId = typeId;
        if (typeId != null) {
            this._hasNativeId = true;
        }
        Object objectId = p.getObjectId();
        this._objectId = objectId;
        if (objectId != null) {
            this._hasNativeId = true;
        }
    }

    protected final void _appendValue(JsonToken type) {
        Segment next;
        this._writeContext.writeValue();
        if (this._hasNativeId) {
            next = this._last.append(this._appendAt, type, this._objectId, this._typeId);
        } else {
            next = this._last.append(this._appendAt, type);
        }
        if (next == null) {
            this._appendAt++;
        } else {
            this._last = next;
            this._appendAt = 1;
        }
    }

    protected final void _appendValue(JsonToken type, Object value) {
        Segment next;
        this._writeContext.writeValue();
        if (this._hasNativeId) {
            next = this._last.append(this._appendAt, type, value, this._objectId, this._typeId);
        } else {
            next = this._last.append(this._appendAt, type, value);
        }
        if (next == null) {
            this._appendAt++;
        } else {
            this._last = next;
            this._appendAt = 1;
        }
    }

    protected final void _appendFieldName(Object value) {
        Segment next;
        if (this._hasNativeId) {
            next = this._last.append(this._appendAt, JsonToken.FIELD_NAME, value, this._objectId, this._typeId);
        } else {
            next = this._last.append(this._appendAt, JsonToken.FIELD_NAME, value);
        }
        if (next == null) {
            this._appendAt++;
        } else {
            this._last = next;
            this._appendAt = 1;
        }
    }

    protected final void _appendStartMarker(JsonToken type) {
        Segment next;
        if (this._hasNativeId) {
            next = this._last.append(this._appendAt, type, this._objectId, this._typeId);
        } else {
            next = this._last.append(this._appendAt, type);
        }
        if (next == null) {
            this._appendAt++;
        } else {
            this._last = next;
            this._appendAt = 1;
        }
    }

    protected final void _appendEndMarker(JsonToken type) {
        Segment next = this._last.append(this._appendAt, type);
        if (next == null) {
            this._appendAt++;
        } else {
            this._last = next;
            this._appendAt = 1;
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.fasterxml.jackson.core.JsonGenerator
    public void _reportUnsupportedOperation() {
        throw new UnsupportedOperationException("Called operation not supported for TokenBuffer");
    }

    /* JADX INFO: Access modifiers changed from: protected */
    /* loaded from: server.jar:BOOT-INF/lib/jackson-databind-2.15.3.jar:com/fasterxml/jackson/databind/util/TokenBuffer$Parser.class */
    public static final class Parser extends ParserMinimalBase {
        protected ObjectCodec _codec;
        protected StreamReadConstraints _streamReadConstraints;
        protected final boolean _hasNativeTypeIds;
        protected final boolean _hasNativeObjectIds;
        protected final boolean _hasNativeIds;
        protected Segment _segment;
        protected int _segmentPtr;
        protected TokenBufferReadContext _parsingContext;
        protected boolean _closed;
        protected transient ByteArrayBuilder _byteBuilder;
        protected JsonLocation _location;

        @Deprecated
        public Parser(Segment firstSeg, ObjectCodec codec, boolean hasNativeTypeIds, boolean hasNativeObjectIds) {
            this(firstSeg, codec, hasNativeTypeIds, hasNativeObjectIds, null);
        }

        @Deprecated
        public Parser(Segment firstSeg, ObjectCodec codec, boolean hasNativeTypeIds, boolean hasNativeObjectIds, JsonStreamContext parentContext) {
            this(firstSeg, codec, hasNativeTypeIds, hasNativeObjectIds, parentContext, StreamReadConstraints.defaults());
        }

        public Parser(Segment firstSeg, ObjectCodec codec, boolean hasNativeTypeIds, boolean hasNativeObjectIds, JsonStreamContext parentContext, StreamReadConstraints streamReadConstraints) {
            this._location = null;
            this._segment = firstSeg;
            this._segmentPtr = -1;
            this._codec = codec;
            this._streamReadConstraints = streamReadConstraints;
            this._parsingContext = TokenBufferReadContext.createRootContext(parentContext);
            this._hasNativeTypeIds = hasNativeTypeIds;
            this._hasNativeObjectIds = hasNativeObjectIds;
            this._hasNativeIds = hasNativeTypeIds || hasNativeObjectIds;
        }

        public void setLocation(JsonLocation l) {
            this._location = l;
        }

        @Override // com.fasterxml.jackson.core.JsonParser
        public ObjectCodec getCodec() {
            return this._codec;
        }

        @Override // com.fasterxml.jackson.core.JsonParser
        public void setCodec(ObjectCodec c) {
            this._codec = c;
        }

        @Override // com.fasterxml.jackson.core.JsonParser, com.fasterxml.jackson.core.Versioned
        public Version version() {
            return PackageVersion.VERSION;
        }

        @Override // com.fasterxml.jackson.core.JsonParser
        public JacksonFeatureSet<StreamReadCapability> getReadCapabilities() {
            return DEFAULT_READ_CAPABILITIES;
        }

        @Override // com.fasterxml.jackson.core.JsonParser
        public StreamReadConstraints streamReadConstraints() {
            return this._streamReadConstraints;
        }

        public JsonToken peekNextToken() throws IOException {
            if (this._closed) {
                return null;
            }
            Segment seg = this._segment;
            int ptr = this._segmentPtr + 1;
            if (ptr >= 16) {
                ptr = 0;
                seg = seg == null ? null : seg.next();
            }
            if (seg == null) {
                return null;
            }
            return seg.type(ptr);
        }

        @Override // com.fasterxml.jackson.core.base.ParserMinimalBase, com.fasterxml.jackson.core.JsonParser, java.io.Closeable, java.lang.AutoCloseable
        public void close() throws IOException {
            if (!this._closed) {
                this._closed = true;
            }
        }

        @Override // com.fasterxml.jackson.core.base.ParserMinimalBase, com.fasterxml.jackson.core.JsonParser
        public JsonToken nextToken() throws IOException {
            if (this._closed || this._segment == null) {
                return null;
            }
            int i = this._segmentPtr + 1;
            this._segmentPtr = i;
            if (i >= 16) {
                this._segmentPtr = 0;
                this._segment = this._segment.next();
                if (this._segment == null) {
                    return null;
                }
            }
            this._currToken = this._segment.type(this._segmentPtr);
            if (this._currToken == JsonToken.FIELD_NAME) {
                Object ob = _currentObject();
                String name = ob instanceof String ? (String) ob : ob.toString();
                this._parsingContext.setCurrentName(name);
            } else if (this._currToken == JsonToken.START_OBJECT) {
                this._parsingContext = this._parsingContext.createChildObjectContext();
            } else if (this._currToken == JsonToken.START_ARRAY) {
                this._parsingContext = this._parsingContext.createChildArrayContext();
            } else if (this._currToken == JsonToken.END_OBJECT || this._currToken == JsonToken.END_ARRAY) {
                this._parsingContext = this._parsingContext.parentOrCopy();
            } else {
                this._parsingContext.updateForValue();
            }
            return this._currToken;
        }

        @Override // com.fasterxml.jackson.core.JsonParser
        public String nextFieldName() throws IOException {
            if (this._closed || this._segment == null) {
                return null;
            }
            int ptr = this._segmentPtr + 1;
            if (ptr < 16 && this._segment.type(ptr) == JsonToken.FIELD_NAME) {
                this._segmentPtr = ptr;
                this._currToken = JsonToken.FIELD_NAME;
                Object ob = this._segment.get(ptr);
                String name = ob instanceof String ? (String) ob : ob.toString();
                this._parsingContext.setCurrentName(name);
                return name;
            }
            if (nextToken() == JsonToken.FIELD_NAME) {
                return currentName();
            }
            return null;
        }

        @Override // com.fasterxml.jackson.core.base.ParserMinimalBase, com.fasterxml.jackson.core.JsonParser
        public boolean isClosed() {
            return this._closed;
        }

        @Override // com.fasterxml.jackson.core.base.ParserMinimalBase, com.fasterxml.jackson.core.JsonParser
        public JsonStreamContext getParsingContext() {
            return this._parsingContext;
        }

        @Override // com.fasterxml.jackson.core.JsonParser
        public JsonLocation getTokenLocation() {
            return getCurrentLocation();
        }

        @Override // com.fasterxml.jackson.core.JsonParser
        public JsonLocation getCurrentLocation() {
            return this._location == null ? JsonLocation.NA : this._location;
        }

        @Override // com.fasterxml.jackson.core.JsonParser
        public String currentName() {
            if (this._currToken == JsonToken.START_OBJECT || this._currToken == JsonToken.START_ARRAY) {
                JsonStreamContext parent = this._parsingContext.getParent();
                return parent.getCurrentName();
            }
            return this._parsingContext.getCurrentName();
        }

        @Override // com.fasterxml.jackson.core.base.ParserMinimalBase, com.fasterxml.jackson.core.JsonParser
        public String getCurrentName() {
            return currentName();
        }

        @Override // com.fasterxml.jackson.core.base.ParserMinimalBase, com.fasterxml.jackson.core.JsonParser
        public void overrideCurrentName(String name) {
            JsonStreamContext ctxt = this._parsingContext;
            if (this._currToken == JsonToken.START_OBJECT || this._currToken == JsonToken.START_ARRAY) {
                ctxt = ctxt.getParent();
            }
            if (ctxt instanceof TokenBufferReadContext) {
                try {
                    ((TokenBufferReadContext) ctxt).setCurrentName(name);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        }

        @Override // com.fasterxml.jackson.core.base.ParserMinimalBase, com.fasterxml.jackson.core.JsonParser
        public String getText() {
            if (this._currToken == JsonToken.VALUE_STRING || this._currToken == JsonToken.FIELD_NAME) {
                Object ob = _currentObject();
                if (ob instanceof String) {
                    return (String) ob;
                }
                return ClassUtil.nullOrToString(ob);
            }
            if (this._currToken == null) {
                return null;
            }
            switch (this._currToken) {
                case VALUE_NUMBER_INT:
                case VALUE_NUMBER_FLOAT:
                    return ClassUtil.nullOrToString(_currentObject());
                default:
                    return this._currToken.asString();
            }
        }

        @Override // com.fasterxml.jackson.core.base.ParserMinimalBase, com.fasterxml.jackson.core.JsonParser
        public char[] getTextCharacters() {
            String str = getText();
            if (str == null) {
                return null;
            }
            return str.toCharArray();
        }

        @Override // com.fasterxml.jackson.core.base.ParserMinimalBase, com.fasterxml.jackson.core.JsonParser
        public int getTextLength() {
            String str = getText();
            if (str == null) {
                return 0;
            }
            return str.length();
        }

        @Override // com.fasterxml.jackson.core.base.ParserMinimalBase, com.fasterxml.jackson.core.JsonParser
        public int getTextOffset() {
            return 0;
        }

        @Override // com.fasterxml.jackson.core.base.ParserMinimalBase, com.fasterxml.jackson.core.JsonParser
        public boolean hasTextCharacters() {
            return false;
        }

        @Override // com.fasterxml.jackson.core.JsonParser
        public boolean isNaN() {
            if (this._currToken == JsonToken.VALUE_NUMBER_FLOAT) {
                Object value = _currentObject();
                if (value instanceof Double) {
                    Double v = (Double) value;
                    return v.isNaN() || v.isInfinite();
                }
                if (value instanceof Float) {
                    Float v2 = (Float) value;
                    return v2.isNaN() || v2.isInfinite();
                }
                return false;
            }
            return false;
        }

        @Override // com.fasterxml.jackson.core.JsonParser
        public BigInteger getBigIntegerValue() throws IOException {
            Number n = getNumberValue(true);
            if (n instanceof BigInteger) {
                return (BigInteger) n;
            }
            if (n instanceof BigDecimal) {
                BigDecimal bd = (BigDecimal) n;
                streamReadConstraints().validateBigIntegerScale(bd.scale());
                return bd.toBigInteger();
            }
            return BigInteger.valueOf(n.longValue());
        }

        @Override // com.fasterxml.jackson.core.JsonParser
        public BigDecimal getDecimalValue() throws IOException {
            Number n = getNumberValue(true);
            if (n instanceof BigDecimal) {
                return (BigDecimal) n;
            }
            if (n instanceof Integer) {
                return BigDecimal.valueOf(n.intValue());
            }
            if (n instanceof Long) {
                return BigDecimal.valueOf(n.longValue());
            }
            if (n instanceof BigInteger) {
                return new BigDecimal((BigInteger) n);
            }
            return BigDecimal.valueOf(n.doubleValue());
        }

        @Override // com.fasterxml.jackson.core.JsonParser
        public double getDoubleValue() throws IOException {
            return getNumberValue().doubleValue();
        }

        @Override // com.fasterxml.jackson.core.JsonParser
        public float getFloatValue() throws IOException {
            return getNumberValue().floatValue();
        }

        @Override // com.fasterxml.jackson.core.JsonParser
        public int getIntValue() throws IOException {
            Number n = this._currToken == JsonToken.VALUE_NUMBER_INT ? (Number) _currentObject() : getNumberValue();
            if ((n instanceof Integer) || _smallerThanInt(n)) {
                return n.intValue();
            }
            return _convertNumberToInt(n);
        }

        @Override // com.fasterxml.jackson.core.JsonParser
        public long getLongValue() throws IOException {
            Number n = this._currToken == JsonToken.VALUE_NUMBER_INT ? (Number) _currentObject() : getNumberValue();
            if ((n instanceof Long) || _smallerThanLong(n)) {
                return n.longValue();
            }
            return _convertNumberToLong(n);
        }

        @Override // com.fasterxml.jackson.core.JsonParser
        public JsonParser.NumberType getNumberType() throws IOException {
            Object n = getNumberValueDeferred();
            if (n instanceof Integer) {
                return JsonParser.NumberType.INT;
            }
            if (n instanceof Long) {
                return JsonParser.NumberType.LONG;
            }
            if (n instanceof Double) {
                return JsonParser.NumberType.DOUBLE;
            }
            if (n instanceof BigDecimal) {
                return JsonParser.NumberType.BIG_DECIMAL;
            }
            if (n instanceof BigInteger) {
                return JsonParser.NumberType.BIG_INTEGER;
            }
            if (n instanceof Float) {
                return JsonParser.NumberType.FLOAT;
            }
            if (n instanceof Short) {
                return JsonParser.NumberType.INT;
            }
            if (n instanceof String) {
                return this._currToken == JsonToken.VALUE_NUMBER_FLOAT ? JsonParser.NumberType.BIG_DECIMAL : JsonParser.NumberType.BIG_INTEGER;
            }
            return null;
        }

        @Override // com.fasterxml.jackson.core.JsonParser
        public final Number getNumberValue() throws IOException {
            return getNumberValue(false);
        }

        @Override // com.fasterxml.jackson.core.JsonParser
        public Object getNumberValueDeferred() throws IOException {
            _checkIsNumber();
            return _currentObject();
        }

        private Number getNumberValue(boolean preferBigNumbers) throws IOException {
            _checkIsNumber();
            Object value = _currentObject();
            if (value instanceof Number) {
                return (Number) value;
            }
            if (value instanceof String) {
                String str = (String) value;
                int len = str.length();
                if (this._currToken == JsonToken.VALUE_NUMBER_INT) {
                    if (preferBigNumbers || len >= 19) {
                        return NumberInput.parseBigInteger(str, isEnabled(StreamReadFeature.USE_FAST_BIG_NUMBER_PARSER));
                    }
                    if (len >= 10) {
                        return Long.valueOf(NumberInput.parseLong(str));
                    }
                    return Integer.valueOf(NumberInput.parseInt(str));
                }
                if (preferBigNumbers) {
                    BigDecimal dec = NumberInput.parseBigDecimal(str, isEnabled(StreamReadFeature.USE_FAST_BIG_NUMBER_PARSER));
                    if (dec == null) {
                        throw new IllegalStateException("Internal error: failed to parse number '" + str + "'");
                    }
                    return dec;
                }
                return Double.valueOf(NumberInput.parseDouble(str, isEnabled(StreamReadFeature.USE_FAST_DOUBLE_PARSER)));
            }
            throw new IllegalStateException("Internal error: entry should be a Number, but is of type " + ClassUtil.classNameOf(value));
        }

        private final boolean _smallerThanInt(Number n) {
            return (n instanceof Short) || (n instanceof Byte);
        }

        private final boolean _smallerThanLong(Number n) {
            return (n instanceof Integer) || (n instanceof Short) || (n instanceof Byte);
        }

        protected int _convertNumberToInt(Number n) throws IOException {
            if (n instanceof Long) {
                long l = n.longValue();
                int result = (int) l;
                if (result != l) {
                    reportOverflowInt();
                }
                return result;
            }
            if (n instanceof BigInteger) {
                BigInteger big = (BigInteger) n;
                if (BI_MIN_INT.compareTo(big) > 0 || BI_MAX_INT.compareTo(big) < 0) {
                    reportOverflowInt();
                }
            } else {
                if ((n instanceof Double) || (n instanceof Float)) {
                    double d = n.doubleValue();
                    if (d < -2.147483648E9d || d > 2.147483647E9d) {
                        reportOverflowInt();
                    }
                    return (int) d;
                }
                if (n instanceof BigDecimal) {
                    BigDecimal big2 = (BigDecimal) n;
                    if (BD_MIN_INT.compareTo(big2) > 0 || BD_MAX_INT.compareTo(big2) < 0) {
                        reportOverflowInt();
                    }
                } else {
                    _throwInternal();
                }
            }
            return n.intValue();
        }

        protected long _convertNumberToLong(Number n) throws IOException {
            if (n instanceof BigInteger) {
                BigInteger big = (BigInteger) n;
                if (BI_MIN_LONG.compareTo(big) > 0 || BI_MAX_LONG.compareTo(big) < 0) {
                    reportOverflowLong();
                }
            } else {
                if ((n instanceof Double) || (n instanceof Float)) {
                    double d = n.doubleValue();
                    if (d < -9.223372036854776E18d || d > 9.223372036854776E18d) {
                        reportOverflowLong();
                    }
                    return (long) d;
                }
                if (n instanceof BigDecimal) {
                    BigDecimal big2 = (BigDecimal) n;
                    if (BD_MIN_LONG.compareTo(big2) > 0 || BD_MAX_LONG.compareTo(big2) < 0) {
                        reportOverflowLong();
                    }
                } else {
                    _throwInternal();
                }
            }
            return n.longValue();
        }

        @Override // com.fasterxml.jackson.core.JsonParser
        public Object getEmbeddedObject() {
            if (this._currToken == JsonToken.VALUE_EMBEDDED_OBJECT) {
                return _currentObject();
            }
            return null;
        }

        @Override // com.fasterxml.jackson.core.base.ParserMinimalBase, com.fasterxml.jackson.core.JsonParser
        public byte[] getBinaryValue(Base64Variant b64variant) throws IOException {
            if (this._currToken == JsonToken.VALUE_EMBEDDED_OBJECT) {
                Object ob = _currentObject();
                if (ob instanceof byte[]) {
                    return (byte[]) ob;
                }
            }
            if (this._currToken != JsonToken.VALUE_STRING) {
                throw _constructError("Current token (" + this._currToken + ") not VALUE_STRING (or VALUE_EMBEDDED_OBJECT with byte[]), cannot access as binary");
            }
            String str = getText();
            if (str == null) {
                return null;
            }
            ByteArrayBuilder builder = this._byteBuilder;
            if (builder == null) {
                ByteArrayBuilder byteArrayBuilder = new ByteArrayBuilder(100);
                builder = byteArrayBuilder;
                this._byteBuilder = byteArrayBuilder;
            } else {
                this._byteBuilder.reset();
            }
            _decodeBase64(str, builder, b64variant);
            return builder.toByteArray();
        }

        @Override // com.fasterxml.jackson.core.JsonParser
        public int readBinaryValue(Base64Variant b64variant, OutputStream out) throws IOException {
            byte[] data = getBinaryValue(b64variant);
            if (data != null) {
                out.write(data, 0, data.length);
                return data.length;
            }
            return 0;
        }

        @Override // com.fasterxml.jackson.core.JsonParser
        public boolean canReadObjectId() {
            return this._hasNativeObjectIds;
        }

        @Override // com.fasterxml.jackson.core.JsonParser
        public boolean canReadTypeId() {
            return this._hasNativeTypeIds;
        }

        @Override // com.fasterxml.jackson.core.JsonParser
        public Object getTypeId() {
            return this._segment.findTypeId(this._segmentPtr);
        }

        @Override // com.fasterxml.jackson.core.JsonParser
        public Object getObjectId() {
            return this._segment.findObjectId(this._segmentPtr);
        }

        protected final Object _currentObject() {
            return this._segment.get(this._segmentPtr);
        }

        protected final void _checkIsNumber() throws JacksonException {
            if (this._currToken == null || !this._currToken.isNumeric()) {
                throw _constructError("Current token (" + this._currToken + ") not numeric, cannot use numeric value accessors");
            }
        }

        @Override // com.fasterxml.jackson.core.base.ParserMinimalBase
        protected void _handleEOF() {
            _throwInternal();
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    /* loaded from: server.jar:BOOT-INF/lib/jackson-databind-2.15.3.jar:com/fasterxml/jackson/databind/util/TokenBuffer$Segment.class */
    public static final class Segment {
        public static final int TOKENS_PER_SEGMENT = 16;
        private static final JsonToken[] TOKEN_TYPES_BY_INDEX = new JsonToken[16];
        protected Segment _next;
        protected long _tokenTypes;
        protected final Object[] _tokens = new Object[16];
        protected TreeMap<Integer, Object> _nativeIds;

        static {
            JsonToken[] t = JsonToken.values();
            System.arraycopy(t, 1, TOKEN_TYPES_BY_INDEX, 1, Math.min(15, t.length - 1));
        }

        public JsonToken type(int index) {
            long l = this._tokenTypes;
            if (index > 0) {
                l >>= index << 2;
            }
            int ix = ((int) l) & 15;
            return TOKEN_TYPES_BY_INDEX[ix];
        }

        public int rawType(int index) {
            long l = this._tokenTypes;
            if (index > 0) {
                l >>= index << 2;
            }
            int ix = ((int) l) & 15;
            return ix;
        }

        public Object get(int index) {
            return this._tokens[index];
        }

        public Segment next() {
            return this._next;
        }

        public boolean hasIds() {
            return this._nativeIds != null;
        }

        public Segment append(int index, JsonToken tokenType) {
            if (index < 16) {
                set(index, tokenType);
                return null;
            }
            this._next = new Segment();
            this._next.set(0, tokenType);
            return this._next;
        }

        public Segment append(int index, JsonToken tokenType, Object objectId, Object typeId) {
            if (index < 16) {
                set(index, tokenType, objectId, typeId);
                return null;
            }
            this._next = new Segment();
            this._next.set(0, tokenType, objectId, typeId);
            return this._next;
        }

        public Segment append(int index, JsonToken tokenType, Object value) {
            if (index < 16) {
                set(index, tokenType, value);
                return null;
            }
            this._next = new Segment();
            this._next.set(0, tokenType, value);
            return this._next;
        }

        public Segment append(int index, JsonToken tokenType, Object value, Object objectId, Object typeId) {
            if (index < 16) {
                set(index, tokenType, value, objectId, typeId);
                return null;
            }
            this._next = new Segment();
            this._next.set(0, tokenType, value, objectId, typeId);
            return this._next;
        }

        private void set(int index, JsonToken tokenType) {
            long typeCode = tokenType.ordinal();
            if (index > 0) {
                typeCode <<= index << 2;
            }
            this._tokenTypes |= typeCode;
        }

        private void set(int index, JsonToken tokenType, Object objectId, Object typeId) {
            long typeCode = tokenType.ordinal();
            if (index > 0) {
                typeCode <<= index << 2;
            }
            this._tokenTypes |= typeCode;
            assignNativeIds(index, objectId, typeId);
        }

        private void set(int index, JsonToken tokenType, Object value) {
            this._tokens[index] = value;
            long typeCode = tokenType.ordinal();
            if (index > 0) {
                typeCode <<= index << 2;
            }
            this._tokenTypes |= typeCode;
        }

        private void set(int index, JsonToken tokenType, Object value, Object objectId, Object typeId) {
            this._tokens[index] = value;
            long typeCode = tokenType.ordinal();
            if (index > 0) {
                typeCode <<= index << 2;
            }
            this._tokenTypes |= typeCode;
            assignNativeIds(index, objectId, typeId);
        }

        private final void assignNativeIds(int index, Object objectId, Object typeId) {
            if (this._nativeIds == null) {
                this._nativeIds = new TreeMap<>();
            }
            if (objectId != null) {
                this._nativeIds.put(Integer.valueOf(_objectIdIndex(index)), objectId);
            }
            if (typeId != null) {
                this._nativeIds.put(Integer.valueOf(_typeIdIndex(index)), typeId);
            }
        }

        Object findObjectId(int index) {
            if (this._nativeIds == null) {
                return null;
            }
            return this._nativeIds.get(Integer.valueOf(_objectIdIndex(index)));
        }

        Object findTypeId(int index) {
            if (this._nativeIds == null) {
                return null;
            }
            return this._nativeIds.get(Integer.valueOf(_typeIdIndex(index)));
        }

        private final int _typeIdIndex(int i) {
            return i + i;
        }

        private final int _objectIdIndex(int i) {
            return i + i + 1;
        }
    }
}
