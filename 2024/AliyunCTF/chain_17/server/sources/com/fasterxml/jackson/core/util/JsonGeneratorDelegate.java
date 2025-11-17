package com.fasterxml.jackson.core.util;

import com.fasterxml.jackson.core.Base64Variant;
import com.fasterxml.jackson.core.FormatSchema;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonStreamContext;
import com.fasterxml.jackson.core.ObjectCodec;
import com.fasterxml.jackson.core.PrettyPrinter;
import com.fasterxml.jackson.core.SerializableString;
import com.fasterxml.jackson.core.StreamWriteCapability;
import com.fasterxml.jackson.core.TreeNode;
import com.fasterxml.jackson.core.Version;
import com.fasterxml.jackson.core.io.CharacterEscapes;
import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.math.BigDecimal;
import java.math.BigInteger;

/* loaded from: server.jar:BOOT-INF/lib/jackson-core-2.15.3.jar:com/fasterxml/jackson/core/util/JsonGeneratorDelegate.class */
public class JsonGeneratorDelegate extends JsonGenerator {
    protected JsonGenerator delegate;
    protected boolean delegateCopyMethods;

    public JsonGeneratorDelegate(JsonGenerator d) {
        this(d, true);
    }

    public JsonGeneratorDelegate(JsonGenerator d, boolean delegateCopyMethods) {
        this.delegate = d;
        this.delegateCopyMethods = delegateCopyMethods;
    }

    @Override // com.fasterxml.jackson.core.JsonGenerator
    public ObjectCodec getCodec() {
        return this.delegate.getCodec();
    }

    @Override // com.fasterxml.jackson.core.JsonGenerator
    public JsonGenerator setCodec(ObjectCodec oc) {
        this.delegate.setCodec(oc);
        return this;
    }

    @Override // com.fasterxml.jackson.core.JsonGenerator
    public void setSchema(FormatSchema schema) {
        this.delegate.setSchema(schema);
    }

    @Override // com.fasterxml.jackson.core.JsonGenerator
    public FormatSchema getSchema() {
        return this.delegate.getSchema();
    }

    @Override // com.fasterxml.jackson.core.JsonGenerator, com.fasterxml.jackson.core.Versioned
    public Version version() {
        return this.delegate.version();
    }

    @Override // com.fasterxml.jackson.core.JsonGenerator
    public Object getOutputTarget() {
        return this.delegate.getOutputTarget();
    }

    @Override // com.fasterxml.jackson.core.JsonGenerator
    public int getOutputBuffered() {
        return this.delegate.getOutputBuffered();
    }

    @Override // com.fasterxml.jackson.core.JsonGenerator
    public void assignCurrentValue(Object v) {
        this.delegate.assignCurrentValue(v);
    }

    @Override // com.fasterxml.jackson.core.JsonGenerator
    public Object currentValue() {
        return this.delegate.currentValue();
    }

    @Override // com.fasterxml.jackson.core.JsonGenerator
    public void setCurrentValue(Object v) {
        this.delegate.setCurrentValue(v);
    }

    @Override // com.fasterxml.jackson.core.JsonGenerator
    public Object getCurrentValue() {
        return this.delegate.getCurrentValue();
    }

    @Override // com.fasterxml.jackson.core.JsonGenerator
    public boolean canUseSchema(FormatSchema schema) {
        return this.delegate.canUseSchema(schema);
    }

    @Override // com.fasterxml.jackson.core.JsonGenerator
    public boolean canWriteTypeId() {
        return this.delegate.canWriteTypeId();
    }

    @Override // com.fasterxml.jackson.core.JsonGenerator
    public boolean canWriteObjectId() {
        return this.delegate.canWriteObjectId();
    }

    @Override // com.fasterxml.jackson.core.JsonGenerator
    public boolean canWriteBinaryNatively() {
        return this.delegate.canWriteBinaryNatively();
    }

    @Override // com.fasterxml.jackson.core.JsonGenerator
    public boolean canOmitFields() {
        return this.delegate.canOmitFields();
    }

    @Override // com.fasterxml.jackson.core.JsonGenerator
    public boolean canWriteFormattedNumbers() {
        return this.delegate.canWriteFormattedNumbers();
    }

    @Override // com.fasterxml.jackson.core.JsonGenerator
    public JacksonFeatureSet<StreamWriteCapability> getWriteCapabilities() {
        return this.delegate.getWriteCapabilities();
    }

    @Override // com.fasterxml.jackson.core.JsonGenerator
    public JsonGenerator enable(JsonGenerator.Feature f) {
        this.delegate.enable(f);
        return this;
    }

    @Override // com.fasterxml.jackson.core.JsonGenerator
    public JsonGenerator disable(JsonGenerator.Feature f) {
        this.delegate.disable(f);
        return this;
    }

    @Override // com.fasterxml.jackson.core.JsonGenerator
    public boolean isEnabled(JsonGenerator.Feature f) {
        return this.delegate.isEnabled(f);
    }

    @Override // com.fasterxml.jackson.core.JsonGenerator
    public int getFeatureMask() {
        return this.delegate.getFeatureMask();
    }

    @Override // com.fasterxml.jackson.core.JsonGenerator
    @Deprecated
    public JsonGenerator setFeatureMask(int mask) {
        this.delegate.setFeatureMask(mask);
        return this;
    }

    @Override // com.fasterxml.jackson.core.JsonGenerator
    public JsonGenerator overrideStdFeatures(int values, int mask) {
        this.delegate.overrideStdFeatures(values, mask);
        return this;
    }

    @Override // com.fasterxml.jackson.core.JsonGenerator
    public JsonGenerator overrideFormatFeatures(int values, int mask) {
        this.delegate.overrideFormatFeatures(values, mask);
        return this;
    }

    @Override // com.fasterxml.jackson.core.JsonGenerator
    public JsonGenerator setPrettyPrinter(PrettyPrinter pp) {
        this.delegate.setPrettyPrinter(pp);
        return this;
    }

    @Override // com.fasterxml.jackson.core.JsonGenerator
    public PrettyPrinter getPrettyPrinter() {
        return this.delegate.getPrettyPrinter();
    }

    @Override // com.fasterxml.jackson.core.JsonGenerator
    public JsonGenerator useDefaultPrettyPrinter() {
        this.delegate.useDefaultPrettyPrinter();
        return this;
    }

    @Override // com.fasterxml.jackson.core.JsonGenerator
    public JsonGenerator setHighestNonEscapedChar(int charCode) {
        this.delegate.setHighestNonEscapedChar(charCode);
        return this;
    }

    @Override // com.fasterxml.jackson.core.JsonGenerator
    public int getHighestEscapedChar() {
        return this.delegate.getHighestEscapedChar();
    }

    @Override // com.fasterxml.jackson.core.JsonGenerator
    public CharacterEscapes getCharacterEscapes() {
        return this.delegate.getCharacterEscapes();
    }

    @Override // com.fasterxml.jackson.core.JsonGenerator
    public JsonGenerator setCharacterEscapes(CharacterEscapes esc) {
        this.delegate.setCharacterEscapes(esc);
        return this;
    }

    @Override // com.fasterxml.jackson.core.JsonGenerator
    public JsonGenerator setRootValueSeparator(SerializableString sep) {
        this.delegate.setRootValueSeparator(sep);
        return this;
    }

    @Override // com.fasterxml.jackson.core.JsonGenerator
    public void writeStartArray() throws IOException {
        this.delegate.writeStartArray();
    }

    @Override // com.fasterxml.jackson.core.JsonGenerator
    public void writeStartArray(int size) throws IOException {
        this.delegate.writeStartArray(size);
    }

    @Override // com.fasterxml.jackson.core.JsonGenerator
    public void writeStartArray(Object forValue) throws IOException {
        this.delegate.writeStartArray(forValue);
    }

    @Override // com.fasterxml.jackson.core.JsonGenerator
    public void writeStartArray(Object forValue, int size) throws IOException {
        this.delegate.writeStartArray(forValue, size);
    }

    @Override // com.fasterxml.jackson.core.JsonGenerator
    public void writeEndArray() throws IOException {
        this.delegate.writeEndArray();
    }

    @Override // com.fasterxml.jackson.core.JsonGenerator
    public void writeStartObject() throws IOException {
        this.delegate.writeStartObject();
    }

    @Override // com.fasterxml.jackson.core.JsonGenerator
    public void writeStartObject(Object forValue) throws IOException {
        this.delegate.writeStartObject(forValue);
    }

    @Override // com.fasterxml.jackson.core.JsonGenerator
    public void writeStartObject(Object forValue, int size) throws IOException {
        this.delegate.writeStartObject(forValue, size);
    }

    @Override // com.fasterxml.jackson.core.JsonGenerator
    public void writeEndObject() throws IOException {
        this.delegate.writeEndObject();
    }

    @Override // com.fasterxml.jackson.core.JsonGenerator
    public void writeFieldName(String name) throws IOException {
        this.delegate.writeFieldName(name);
    }

    @Override // com.fasterxml.jackson.core.JsonGenerator
    public void writeFieldName(SerializableString name) throws IOException {
        this.delegate.writeFieldName(name);
    }

    @Override // com.fasterxml.jackson.core.JsonGenerator
    public void writeFieldId(long id) throws IOException {
        this.delegate.writeFieldId(id);
    }

    @Override // com.fasterxml.jackson.core.JsonGenerator
    public void writeArray(int[] array, int offset, int length) throws IOException {
        this.delegate.writeArray(array, offset, length);
    }

    @Override // com.fasterxml.jackson.core.JsonGenerator
    public void writeArray(long[] array, int offset, int length) throws IOException {
        this.delegate.writeArray(array, offset, length);
    }

    @Override // com.fasterxml.jackson.core.JsonGenerator
    public void writeArray(double[] array, int offset, int length) throws IOException {
        this.delegate.writeArray(array, offset, length);
    }

    @Override // com.fasterxml.jackson.core.JsonGenerator
    public void writeArray(String[] array, int offset, int length) throws IOException {
        this.delegate.writeArray(array, offset, length);
    }

    @Override // com.fasterxml.jackson.core.JsonGenerator
    public void writeString(String text) throws IOException {
        this.delegate.writeString(text);
    }

    @Override // com.fasterxml.jackson.core.JsonGenerator
    public void writeString(Reader reader, int len) throws IOException {
        this.delegate.writeString(reader, len);
    }

    @Override // com.fasterxml.jackson.core.JsonGenerator
    public void writeString(char[] text, int offset, int len) throws IOException {
        this.delegate.writeString(text, offset, len);
    }

    @Override // com.fasterxml.jackson.core.JsonGenerator
    public void writeString(SerializableString text) throws IOException {
        this.delegate.writeString(text);
    }

    @Override // com.fasterxml.jackson.core.JsonGenerator
    public void writeRawUTF8String(byte[] text, int offset, int length) throws IOException {
        this.delegate.writeRawUTF8String(text, offset, length);
    }

    @Override // com.fasterxml.jackson.core.JsonGenerator
    public void writeUTF8String(byte[] text, int offset, int length) throws IOException {
        this.delegate.writeUTF8String(text, offset, length);
    }

    @Override // com.fasterxml.jackson.core.JsonGenerator
    public void writeRaw(String text) throws IOException {
        this.delegate.writeRaw(text);
    }

    @Override // com.fasterxml.jackson.core.JsonGenerator
    public void writeRaw(String text, int offset, int len) throws IOException {
        this.delegate.writeRaw(text, offset, len);
    }

    @Override // com.fasterxml.jackson.core.JsonGenerator
    public void writeRaw(SerializableString raw) throws IOException {
        this.delegate.writeRaw(raw);
    }

    @Override // com.fasterxml.jackson.core.JsonGenerator
    public void writeRaw(char[] text, int offset, int len) throws IOException {
        this.delegate.writeRaw(text, offset, len);
    }

    @Override // com.fasterxml.jackson.core.JsonGenerator
    public void writeRaw(char c) throws IOException {
        this.delegate.writeRaw(c);
    }

    @Override // com.fasterxml.jackson.core.JsonGenerator
    public void writeRawValue(String text) throws IOException {
        this.delegate.writeRawValue(text);
    }

    @Override // com.fasterxml.jackson.core.JsonGenerator
    public void writeRawValue(String text, int offset, int len) throws IOException {
        this.delegate.writeRawValue(text, offset, len);
    }

    @Override // com.fasterxml.jackson.core.JsonGenerator
    public void writeRawValue(char[] text, int offset, int len) throws IOException {
        this.delegate.writeRawValue(text, offset, len);
    }

    @Override // com.fasterxml.jackson.core.JsonGenerator
    public void writeBinary(Base64Variant b64variant, byte[] data, int offset, int len) throws IOException {
        this.delegate.writeBinary(b64variant, data, offset, len);
    }

    @Override // com.fasterxml.jackson.core.JsonGenerator
    public int writeBinary(Base64Variant b64variant, InputStream data, int dataLength) throws IOException {
        return this.delegate.writeBinary(b64variant, data, dataLength);
    }

    @Override // com.fasterxml.jackson.core.JsonGenerator
    public void writeNumber(short v) throws IOException {
        this.delegate.writeNumber(v);
    }

    @Override // com.fasterxml.jackson.core.JsonGenerator
    public void writeNumber(int v) throws IOException {
        this.delegate.writeNumber(v);
    }

    @Override // com.fasterxml.jackson.core.JsonGenerator
    public void writeNumber(long v) throws IOException {
        this.delegate.writeNumber(v);
    }

    @Override // com.fasterxml.jackson.core.JsonGenerator
    public void writeNumber(BigInteger v) throws IOException {
        this.delegate.writeNumber(v);
    }

    @Override // com.fasterxml.jackson.core.JsonGenerator
    public void writeNumber(double v) throws IOException {
        this.delegate.writeNumber(v);
    }

    @Override // com.fasterxml.jackson.core.JsonGenerator
    public void writeNumber(float v) throws IOException {
        this.delegate.writeNumber(v);
    }

    @Override // com.fasterxml.jackson.core.JsonGenerator
    public void writeNumber(BigDecimal v) throws IOException {
        this.delegate.writeNumber(v);
    }

    @Override // com.fasterxml.jackson.core.JsonGenerator
    public void writeNumber(String encodedValue) throws IOException, UnsupportedOperationException {
        this.delegate.writeNumber(encodedValue);
    }

    @Override // com.fasterxml.jackson.core.JsonGenerator
    public void writeNumber(char[] encodedValueBuffer, int offset, int length) throws IOException, UnsupportedOperationException {
        this.delegate.writeNumber(encodedValueBuffer, offset, length);
    }

    @Override // com.fasterxml.jackson.core.JsonGenerator
    public void writeBoolean(boolean state) throws IOException {
        this.delegate.writeBoolean(state);
    }

    @Override // com.fasterxml.jackson.core.JsonGenerator
    public void writeNull() throws IOException {
        this.delegate.writeNull();
    }

    @Override // com.fasterxml.jackson.core.JsonGenerator
    public void writeOmittedField(String fieldName) throws IOException {
        this.delegate.writeOmittedField(fieldName);
    }

    @Override // com.fasterxml.jackson.core.JsonGenerator
    public void writeObjectId(Object id) throws IOException {
        this.delegate.writeObjectId(id);
    }

    @Override // com.fasterxml.jackson.core.JsonGenerator
    public void writeObjectRef(Object id) throws IOException {
        this.delegate.writeObjectRef(id);
    }

    @Override // com.fasterxml.jackson.core.JsonGenerator
    public void writeTypeId(Object id) throws IOException {
        this.delegate.writeTypeId(id);
    }

    @Override // com.fasterxml.jackson.core.JsonGenerator
    public void writeEmbeddedObject(Object object) throws IOException {
        this.delegate.writeEmbeddedObject(object);
    }

    @Override // com.fasterxml.jackson.core.JsonGenerator
    public void writePOJO(Object pojo) throws IOException {
        writeObject(pojo);
    }

    @Override // com.fasterxml.jackson.core.JsonGenerator
    public void writeObject(Object pojo) throws IOException {
        if (this.delegateCopyMethods) {
            this.delegate.writeObject(pojo);
            return;
        }
        if (pojo == null) {
            writeNull();
            return;
        }
        ObjectCodec c = getCodec();
        if (c != null) {
            c.writeValue(this, pojo);
        } else {
            _writeSimpleObject(pojo);
        }
    }

    @Override // com.fasterxml.jackson.core.JsonGenerator
    public void writeTree(TreeNode tree) throws IOException {
        if (this.delegateCopyMethods) {
            this.delegate.writeTree(tree);
            return;
        }
        if (tree == null) {
            writeNull();
            return;
        }
        ObjectCodec c = getCodec();
        if (c == null) {
            throw new IllegalStateException("No ObjectCodec defined");
        }
        c.writeTree(this, tree);
    }

    @Override // com.fasterxml.jackson.core.JsonGenerator
    public void copyCurrentEvent(JsonParser p) throws IOException {
        if (!this.delegateCopyMethods) {
            super.copyCurrentEvent(p);
        } else {
            this.delegate.copyCurrentEvent(p);
        }
    }

    @Override // com.fasterxml.jackson.core.JsonGenerator
    public void copyCurrentStructure(JsonParser p) throws IOException {
        if (!this.delegateCopyMethods) {
            super.copyCurrentStructure(p);
        } else {
            this.delegate.copyCurrentStructure(p);
        }
    }

    @Override // com.fasterxml.jackson.core.JsonGenerator
    public JsonStreamContext getOutputContext() {
        return this.delegate.getOutputContext();
    }

    @Override // com.fasterxml.jackson.core.JsonGenerator, java.io.Flushable
    public void flush() throws IOException {
        this.delegate.flush();
    }

    @Override // com.fasterxml.jackson.core.JsonGenerator, java.io.Closeable, java.lang.AutoCloseable
    public void close() throws IOException {
        this.delegate.close();
    }

    @Override // com.fasterxml.jackson.core.JsonGenerator
    public boolean isClosed() {
        return this.delegate.isClosed();
    }

    @Deprecated
    public JsonGenerator getDelegate() {
        return this.delegate;
    }

    public JsonGenerator delegate() {
        return this.delegate;
    }
}
