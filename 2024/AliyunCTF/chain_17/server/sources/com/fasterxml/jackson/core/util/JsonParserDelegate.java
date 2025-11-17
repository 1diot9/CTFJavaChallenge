package com.fasterxml.jackson.core.util;

import com.fasterxml.jackson.core.Base64Variant;
import com.fasterxml.jackson.core.FormatSchema;
import com.fasterxml.jackson.core.JsonLocation;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonStreamContext;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.core.ObjectCodec;
import com.fasterxml.jackson.core.StreamReadCapability;
import com.fasterxml.jackson.core.StreamReadConstraints;
import com.fasterxml.jackson.core.Version;
import java.io.IOException;
import java.io.OutputStream;
import java.io.Writer;
import java.math.BigDecimal;
import java.math.BigInteger;

/* loaded from: server.jar:BOOT-INF/lib/jackson-core-2.15.3.jar:com/fasterxml/jackson/core/util/JsonParserDelegate.class */
public class JsonParserDelegate extends JsonParser {
    protected JsonParser delegate;

    public JsonParserDelegate(JsonParser d) {
        this.delegate = d;
    }

    @Override // com.fasterxml.jackson.core.JsonParser
    public void setCodec(ObjectCodec c) {
        this.delegate.setCodec(c);
    }

    @Override // com.fasterxml.jackson.core.JsonParser
    public ObjectCodec getCodec() {
        return this.delegate.getCodec();
    }

    @Override // com.fasterxml.jackson.core.JsonParser
    public JsonParser enable(JsonParser.Feature f) {
        this.delegate.enable(f);
        return this;
    }

    @Override // com.fasterxml.jackson.core.JsonParser
    public JsonParser disable(JsonParser.Feature f) {
        this.delegate.disable(f);
        return this;
    }

    @Override // com.fasterxml.jackson.core.JsonParser
    public boolean isEnabled(JsonParser.Feature f) {
        return this.delegate.isEnabled(f);
    }

    @Override // com.fasterxml.jackson.core.JsonParser
    public int getFeatureMask() {
        return this.delegate.getFeatureMask();
    }

    @Override // com.fasterxml.jackson.core.JsonParser
    @Deprecated
    public JsonParser setFeatureMask(int mask) {
        this.delegate.setFeatureMask(mask);
        return this;
    }

    @Override // com.fasterxml.jackson.core.JsonParser
    public JsonParser overrideStdFeatures(int values, int mask) {
        this.delegate.overrideStdFeatures(values, mask);
        return this;
    }

    @Override // com.fasterxml.jackson.core.JsonParser
    public JsonParser overrideFormatFeatures(int values, int mask) {
        this.delegate.overrideFormatFeatures(values, mask);
        return this;
    }

    @Override // com.fasterxml.jackson.core.JsonParser
    public FormatSchema getSchema() {
        return this.delegate.getSchema();
    }

    @Override // com.fasterxml.jackson.core.JsonParser
    public void setSchema(FormatSchema schema) {
        this.delegate.setSchema(schema);
    }

    @Override // com.fasterxml.jackson.core.JsonParser
    public boolean canUseSchema(FormatSchema schema) {
        return this.delegate.canUseSchema(schema);
    }

    @Override // com.fasterxml.jackson.core.JsonParser, com.fasterxml.jackson.core.Versioned
    public Version version() {
        return this.delegate.version();
    }

    @Override // com.fasterxml.jackson.core.JsonParser
    public Object getInputSource() {
        return this.delegate.getInputSource();
    }

    @Override // com.fasterxml.jackson.core.JsonParser
    public StreamReadConstraints streamReadConstraints() {
        return this.delegate.streamReadConstraints();
    }

    @Override // com.fasterxml.jackson.core.JsonParser
    public boolean requiresCustomCodec() {
        return this.delegate.requiresCustomCodec();
    }

    @Override // com.fasterxml.jackson.core.JsonParser
    public JacksonFeatureSet<StreamReadCapability> getReadCapabilities() {
        return this.delegate.getReadCapabilities();
    }

    @Override // com.fasterxml.jackson.core.JsonParser, java.io.Closeable, java.lang.AutoCloseable
    public void close() throws IOException {
        this.delegate.close();
    }

    @Override // com.fasterxml.jackson.core.JsonParser
    public boolean isClosed() {
        return this.delegate.isClosed();
    }

    @Override // com.fasterxml.jackson.core.JsonParser
    public void clearCurrentToken() {
        this.delegate.clearCurrentToken();
    }

    @Override // com.fasterxml.jackson.core.JsonParser
    public JsonToken getLastClearedToken() {
        return this.delegate.getLastClearedToken();
    }

    @Override // com.fasterxml.jackson.core.JsonParser
    public void overrideCurrentName(String name) {
        this.delegate.overrideCurrentName(name);
    }

    @Override // com.fasterxml.jackson.core.JsonParser
    public void assignCurrentValue(Object v) {
        this.delegate.assignCurrentValue(v);
    }

    @Override // com.fasterxml.jackson.core.JsonParser
    public void setCurrentValue(Object v) {
        this.delegate.setCurrentValue(v);
    }

    @Override // com.fasterxml.jackson.core.JsonParser
    public JsonStreamContext getParsingContext() {
        return this.delegate.getParsingContext();
    }

    @Override // com.fasterxml.jackson.core.JsonParser
    public JsonToken currentToken() {
        return this.delegate.currentToken();
    }

    @Override // com.fasterxml.jackson.core.JsonParser
    public int currentTokenId() {
        return this.delegate.currentTokenId();
    }

    @Override // com.fasterxml.jackson.core.JsonParser
    public String currentName() throws IOException {
        return this.delegate.currentName();
    }

    @Override // com.fasterxml.jackson.core.JsonParser
    public Object currentValue() {
        return this.delegate.currentValue();
    }

    @Override // com.fasterxml.jackson.core.JsonParser
    public JsonLocation currentLocation() {
        return this.delegate.getCurrentLocation();
    }

    @Override // com.fasterxml.jackson.core.JsonParser
    public JsonLocation currentTokenLocation() {
        return this.delegate.getTokenLocation();
    }

    @Override // com.fasterxml.jackson.core.JsonParser
    public JsonToken getCurrentToken() {
        return this.delegate.getCurrentToken();
    }

    @Override // com.fasterxml.jackson.core.JsonParser
    @Deprecated
    public int getCurrentTokenId() {
        return this.delegate.getCurrentTokenId();
    }

    @Override // com.fasterxml.jackson.core.JsonParser
    public String getCurrentName() throws IOException {
        return this.delegate.getCurrentName();
    }

    @Override // com.fasterxml.jackson.core.JsonParser
    public Object getCurrentValue() {
        return this.delegate.getCurrentValue();
    }

    @Override // com.fasterxml.jackson.core.JsonParser
    public JsonLocation getCurrentLocation() {
        return this.delegate.getCurrentLocation();
    }

    @Override // com.fasterxml.jackson.core.JsonParser
    public JsonLocation getTokenLocation() {
        return this.delegate.getTokenLocation();
    }

    @Override // com.fasterxml.jackson.core.JsonParser
    public boolean hasCurrentToken() {
        return this.delegate.hasCurrentToken();
    }

    @Override // com.fasterxml.jackson.core.JsonParser
    public boolean hasTokenId(int id) {
        return this.delegate.hasTokenId(id);
    }

    @Override // com.fasterxml.jackson.core.JsonParser
    public boolean hasToken(JsonToken t) {
        return this.delegate.hasToken(t);
    }

    @Override // com.fasterxml.jackson.core.JsonParser
    public boolean isExpectedStartArrayToken() {
        return this.delegate.isExpectedStartArrayToken();
    }

    @Override // com.fasterxml.jackson.core.JsonParser
    public boolean isExpectedStartObjectToken() {
        return this.delegate.isExpectedStartObjectToken();
    }

    @Override // com.fasterxml.jackson.core.JsonParser
    public boolean isExpectedNumberIntToken() {
        return this.delegate.isExpectedNumberIntToken();
    }

    @Override // com.fasterxml.jackson.core.JsonParser
    public boolean isNaN() throws IOException {
        return this.delegate.isNaN();
    }

    @Override // com.fasterxml.jackson.core.JsonParser
    public String getText() throws IOException {
        return this.delegate.getText();
    }

    @Override // com.fasterxml.jackson.core.JsonParser
    public boolean hasTextCharacters() {
        return this.delegate.hasTextCharacters();
    }

    @Override // com.fasterxml.jackson.core.JsonParser
    public char[] getTextCharacters() throws IOException {
        return this.delegate.getTextCharacters();
    }

    @Override // com.fasterxml.jackson.core.JsonParser
    public int getTextLength() throws IOException {
        return this.delegate.getTextLength();
    }

    @Override // com.fasterxml.jackson.core.JsonParser
    public int getTextOffset() throws IOException {
        return this.delegate.getTextOffset();
    }

    @Override // com.fasterxml.jackson.core.JsonParser
    public int getText(Writer writer) throws IOException, UnsupportedOperationException {
        return this.delegate.getText(writer);
    }

    @Override // com.fasterxml.jackson.core.JsonParser
    public BigInteger getBigIntegerValue() throws IOException {
        return this.delegate.getBigIntegerValue();
    }

    @Override // com.fasterxml.jackson.core.JsonParser
    public boolean getBooleanValue() throws IOException {
        return this.delegate.getBooleanValue();
    }

    @Override // com.fasterxml.jackson.core.JsonParser
    public byte getByteValue() throws IOException {
        return this.delegate.getByteValue();
    }

    @Override // com.fasterxml.jackson.core.JsonParser
    public short getShortValue() throws IOException {
        return this.delegate.getShortValue();
    }

    @Override // com.fasterxml.jackson.core.JsonParser
    public BigDecimal getDecimalValue() throws IOException {
        return this.delegate.getDecimalValue();
    }

    @Override // com.fasterxml.jackson.core.JsonParser
    public double getDoubleValue() throws IOException {
        return this.delegate.getDoubleValue();
    }

    @Override // com.fasterxml.jackson.core.JsonParser
    public float getFloatValue() throws IOException {
        return this.delegate.getFloatValue();
    }

    @Override // com.fasterxml.jackson.core.JsonParser
    public int getIntValue() throws IOException {
        return this.delegate.getIntValue();
    }

    @Override // com.fasterxml.jackson.core.JsonParser
    public long getLongValue() throws IOException {
        return this.delegate.getLongValue();
    }

    @Override // com.fasterxml.jackson.core.JsonParser
    public JsonParser.NumberType getNumberType() throws IOException {
        return this.delegate.getNumberType();
    }

    @Override // com.fasterxml.jackson.core.JsonParser
    public Number getNumberValue() throws IOException {
        return this.delegate.getNumberValue();
    }

    @Override // com.fasterxml.jackson.core.JsonParser
    public Number getNumberValueExact() throws IOException {
        return this.delegate.getNumberValueExact();
    }

    @Override // com.fasterxml.jackson.core.JsonParser
    public Object getNumberValueDeferred() throws IOException {
        return this.delegate.getNumberValueDeferred();
    }

    @Override // com.fasterxml.jackson.core.JsonParser
    public int getValueAsInt() throws IOException {
        return this.delegate.getValueAsInt();
    }

    @Override // com.fasterxml.jackson.core.JsonParser
    public int getValueAsInt(int defaultValue) throws IOException {
        return this.delegate.getValueAsInt(defaultValue);
    }

    @Override // com.fasterxml.jackson.core.JsonParser
    public long getValueAsLong() throws IOException {
        return this.delegate.getValueAsLong();
    }

    @Override // com.fasterxml.jackson.core.JsonParser
    public long getValueAsLong(long defaultValue) throws IOException {
        return this.delegate.getValueAsLong(defaultValue);
    }

    @Override // com.fasterxml.jackson.core.JsonParser
    public double getValueAsDouble() throws IOException {
        return this.delegate.getValueAsDouble();
    }

    @Override // com.fasterxml.jackson.core.JsonParser
    public double getValueAsDouble(double defaultValue) throws IOException {
        return this.delegate.getValueAsDouble(defaultValue);
    }

    @Override // com.fasterxml.jackson.core.JsonParser
    public boolean getValueAsBoolean() throws IOException {
        return this.delegate.getValueAsBoolean();
    }

    @Override // com.fasterxml.jackson.core.JsonParser
    public boolean getValueAsBoolean(boolean defaultValue) throws IOException {
        return this.delegate.getValueAsBoolean(defaultValue);
    }

    @Override // com.fasterxml.jackson.core.JsonParser
    public String getValueAsString() throws IOException {
        return this.delegate.getValueAsString();
    }

    @Override // com.fasterxml.jackson.core.JsonParser
    public String getValueAsString(String defaultValue) throws IOException {
        return this.delegate.getValueAsString(defaultValue);
    }

    @Override // com.fasterxml.jackson.core.JsonParser
    public Object getEmbeddedObject() throws IOException {
        return this.delegate.getEmbeddedObject();
    }

    @Override // com.fasterxml.jackson.core.JsonParser
    public byte[] getBinaryValue(Base64Variant b64variant) throws IOException {
        return this.delegate.getBinaryValue(b64variant);
    }

    @Override // com.fasterxml.jackson.core.JsonParser
    public int readBinaryValue(Base64Variant b64variant, OutputStream out) throws IOException {
        return this.delegate.readBinaryValue(b64variant, out);
    }

    @Override // com.fasterxml.jackson.core.JsonParser
    public JsonToken nextToken() throws IOException {
        return this.delegate.nextToken();
    }

    @Override // com.fasterxml.jackson.core.JsonParser
    public JsonToken nextValue() throws IOException {
        return this.delegate.nextValue();
    }

    @Override // com.fasterxml.jackson.core.JsonParser
    public void finishToken() throws IOException {
        this.delegate.finishToken();
    }

    @Override // com.fasterxml.jackson.core.JsonParser
    public JsonParser skipChildren() throws IOException {
        this.delegate.skipChildren();
        return this;
    }

    @Override // com.fasterxml.jackson.core.JsonParser
    public boolean canReadObjectId() {
        return this.delegate.canReadObjectId();
    }

    @Override // com.fasterxml.jackson.core.JsonParser
    public boolean canReadTypeId() {
        return this.delegate.canReadTypeId();
    }

    @Override // com.fasterxml.jackson.core.JsonParser
    public Object getObjectId() throws IOException {
        return this.delegate.getObjectId();
    }

    @Override // com.fasterxml.jackson.core.JsonParser
    public Object getTypeId() throws IOException {
        return this.delegate.getTypeId();
    }

    public JsonParser delegate() {
        return this.delegate;
    }
}
