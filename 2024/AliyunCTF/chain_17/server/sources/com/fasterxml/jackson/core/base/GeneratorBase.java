package com.fasterxml.jackson.core.base;

import com.fasterxml.jackson.core.Base64Variant;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonStreamContext;
import com.fasterxml.jackson.core.ObjectCodec;
import com.fasterxml.jackson.core.PrettyPrinter;
import com.fasterxml.jackson.core.SerializableString;
import com.fasterxml.jackson.core.TreeNode;
import com.fasterxml.jackson.core.Version;
import com.fasterxml.jackson.core.json.DupDetector;
import com.fasterxml.jackson.core.json.JsonWriteContext;
import com.fasterxml.jackson.core.json.PackageVersion;
import com.fasterxml.jackson.core.util.DefaultPrettyPrinter;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import org.springframework.asm.Opcodes;

/* loaded from: server.jar:BOOT-INF/lib/jackson-core-2.15.3.jar:com/fasterxml/jackson/core/base/GeneratorBase.class */
public abstract class GeneratorBase extends JsonGenerator {
    public static final int SURR1_FIRST = 55296;
    public static final int SURR1_LAST = 56319;
    public static final int SURR2_FIRST = 56320;
    public static final int SURR2_LAST = 57343;
    protected static final int DERIVED_FEATURES_MASK = (JsonGenerator.Feature.WRITE_NUMBERS_AS_STRINGS.getMask() | JsonGenerator.Feature.ESCAPE_NON_ASCII.getMask()) | JsonGenerator.Feature.STRICT_DUPLICATE_DETECTION.getMask();
    protected static final String WRITE_BINARY = "write a binary value";
    protected static final String WRITE_BOOLEAN = "write a boolean value";
    protected static final String WRITE_NULL = "write a null";
    protected static final String WRITE_NUMBER = "write a number";
    protected static final String WRITE_RAW = "write a raw (unencoded) value";
    protected static final String WRITE_STRING = "write a string";
    protected static final int MAX_BIG_DECIMAL_SCALE = 9999;
    protected ObjectCodec _objectCodec;
    protected int _features;
    protected boolean _cfgNumbersAsStrings;
    protected JsonWriteContext _writeContext;
    protected boolean _closed;

    @Override // com.fasterxml.jackson.core.JsonGenerator, java.io.Flushable
    public abstract void flush() throws IOException;

    protected abstract void _releaseBuffers();

    protected abstract void _verifyValueWrite(String str) throws IOException;

    /* JADX INFO: Access modifiers changed from: protected */
    public GeneratorBase(int features, ObjectCodec codec) {
        this._features = features;
        this._objectCodec = codec;
        DupDetector dups = JsonGenerator.Feature.STRICT_DUPLICATE_DETECTION.enabledIn(features) ? DupDetector.rootDetector(this) : null;
        this._writeContext = JsonWriteContext.createRootContext(dups);
        this._cfgNumbersAsStrings = JsonGenerator.Feature.WRITE_NUMBERS_AS_STRINGS.enabledIn(features);
    }

    protected GeneratorBase(int features, ObjectCodec codec, JsonWriteContext ctxt) {
        this._features = features;
        this._objectCodec = codec;
        this._writeContext = ctxt;
        this._cfgNumbersAsStrings = JsonGenerator.Feature.WRITE_NUMBERS_AS_STRINGS.enabledIn(features);
    }

    @Override // com.fasterxml.jackson.core.JsonGenerator, com.fasterxml.jackson.core.Versioned
    public Version version() {
        return PackageVersion.VERSION;
    }

    @Override // com.fasterxml.jackson.core.JsonGenerator
    public Object getCurrentValue() {
        return this._writeContext.getCurrentValue();
    }

    @Override // com.fasterxml.jackson.core.JsonGenerator
    public void setCurrentValue(Object v) {
        if (this._writeContext != null) {
            this._writeContext.setCurrentValue(v);
        }
    }

    @Override // com.fasterxml.jackson.core.JsonGenerator
    public final boolean isEnabled(JsonGenerator.Feature f) {
        return (this._features & f.getMask()) != 0;
    }

    @Override // com.fasterxml.jackson.core.JsonGenerator
    public int getFeatureMask() {
        return this._features;
    }

    @Override // com.fasterxml.jackson.core.JsonGenerator
    public JsonGenerator enable(JsonGenerator.Feature f) {
        int mask = f.getMask();
        this._features |= mask;
        if ((mask & DERIVED_FEATURES_MASK) != 0) {
            if (f == JsonGenerator.Feature.WRITE_NUMBERS_AS_STRINGS) {
                this._cfgNumbersAsStrings = true;
            } else if (f == JsonGenerator.Feature.ESCAPE_NON_ASCII) {
                setHighestNonEscapedChar(Opcodes.LAND);
            } else if (f == JsonGenerator.Feature.STRICT_DUPLICATE_DETECTION && this._writeContext.getDupDetector() == null) {
                this._writeContext = this._writeContext.withDupDetector(DupDetector.rootDetector(this));
            }
        }
        return this;
    }

    @Override // com.fasterxml.jackson.core.JsonGenerator
    public JsonGenerator disable(JsonGenerator.Feature f) {
        int mask = f.getMask();
        this._features &= mask ^ (-1);
        if ((mask & DERIVED_FEATURES_MASK) != 0) {
            if (f == JsonGenerator.Feature.WRITE_NUMBERS_AS_STRINGS) {
                this._cfgNumbersAsStrings = false;
            } else if (f == JsonGenerator.Feature.ESCAPE_NON_ASCII) {
                setHighestNonEscapedChar(0);
            } else if (f == JsonGenerator.Feature.STRICT_DUPLICATE_DETECTION) {
                this._writeContext = this._writeContext.withDupDetector(null);
            }
        }
        return this;
    }

    @Override // com.fasterxml.jackson.core.JsonGenerator
    @Deprecated
    public JsonGenerator setFeatureMask(int newMask) {
        int changed = newMask ^ this._features;
        this._features = newMask;
        if (changed != 0) {
            _checkStdFeatureChanges(newMask, changed);
        }
        return this;
    }

    @Override // com.fasterxml.jackson.core.JsonGenerator
    public JsonGenerator overrideStdFeatures(int values, int mask) {
        int oldState = this._features;
        int newState = (oldState & (mask ^ (-1))) | (values & mask);
        int changed = oldState ^ newState;
        if (changed != 0) {
            this._features = newState;
            _checkStdFeatureChanges(newState, changed);
        }
        return this;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public void _checkStdFeatureChanges(int newFeatureFlags, int changedFeatures) {
        if ((changedFeatures & DERIVED_FEATURES_MASK) == 0) {
            return;
        }
        this._cfgNumbersAsStrings = JsonGenerator.Feature.WRITE_NUMBERS_AS_STRINGS.enabledIn(newFeatureFlags);
        if (JsonGenerator.Feature.ESCAPE_NON_ASCII.enabledIn(changedFeatures)) {
            if (JsonGenerator.Feature.ESCAPE_NON_ASCII.enabledIn(newFeatureFlags)) {
                setHighestNonEscapedChar(Opcodes.LAND);
            } else {
                setHighestNonEscapedChar(0);
            }
        }
        if (JsonGenerator.Feature.STRICT_DUPLICATE_DETECTION.enabledIn(changedFeatures)) {
            if (JsonGenerator.Feature.STRICT_DUPLICATE_DETECTION.enabledIn(newFeatureFlags)) {
                if (this._writeContext.getDupDetector() == null) {
                    this._writeContext = this._writeContext.withDupDetector(DupDetector.rootDetector(this));
                    return;
                }
                return;
            }
            this._writeContext = this._writeContext.withDupDetector(null);
        }
    }

    @Override // com.fasterxml.jackson.core.JsonGenerator
    public JsonGenerator useDefaultPrettyPrinter() {
        if (getPrettyPrinter() != null) {
            return this;
        }
        return setPrettyPrinter(_constructDefaultPrettyPrinter());
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
    public JsonStreamContext getOutputContext() {
        return this._writeContext;
    }

    @Override // com.fasterxml.jackson.core.JsonGenerator
    public void writeStartObject(Object forValue) throws IOException {
        writeStartObject();
        if (forValue != null) {
            setCurrentValue(forValue);
        }
    }

    @Override // com.fasterxml.jackson.core.JsonGenerator
    public void writeFieldName(SerializableString name) throws IOException {
        writeFieldName(name.getValue());
    }

    @Override // com.fasterxml.jackson.core.JsonGenerator
    public void writeString(SerializableString text) throws IOException {
        writeString(text.getValue());
    }

    @Override // com.fasterxml.jackson.core.JsonGenerator
    public void writeRawValue(String text) throws IOException {
        _verifyValueWrite("write raw value");
        writeRaw(text);
    }

    @Override // com.fasterxml.jackson.core.JsonGenerator
    public void writeRawValue(String text, int offset, int len) throws IOException {
        _verifyValueWrite("write raw value");
        writeRaw(text, offset, len);
    }

    @Override // com.fasterxml.jackson.core.JsonGenerator
    public void writeRawValue(char[] text, int offset, int len) throws IOException {
        _verifyValueWrite("write raw value");
        writeRaw(text, offset, len);
    }

    @Override // com.fasterxml.jackson.core.JsonGenerator
    public void writeRawValue(SerializableString text) throws IOException {
        _verifyValueWrite("write raw value");
        writeRaw(text);
    }

    @Override // com.fasterxml.jackson.core.JsonGenerator
    public int writeBinary(Base64Variant b64variant, InputStream data, int dataLength) throws IOException {
        _reportUnsupportedOperation();
        return 0;
    }

    @Override // com.fasterxml.jackson.core.JsonGenerator
    public void writeObject(Object value) throws IOException {
        if (value == null) {
            writeNull();
        } else if (this._objectCodec != null) {
            this._objectCodec.writeValue(this, value);
        } else {
            _writeSimpleObject(value);
        }
    }

    @Override // com.fasterxml.jackson.core.JsonGenerator
    public void writeTree(TreeNode rootNode) throws IOException {
        if (rootNode == null) {
            writeNull();
        } else {
            if (this._objectCodec == null) {
                throw new IllegalStateException("No ObjectCodec defined");
            }
            this._objectCodec.writeValue(this, rootNode);
        }
    }

    @Override // com.fasterxml.jackson.core.JsonGenerator, java.io.Closeable, java.lang.AutoCloseable
    public void close() throws IOException {
        this._closed = true;
    }

    @Override // com.fasterxml.jackson.core.JsonGenerator
    public boolean isClosed() {
        return this._closed;
    }

    protected PrettyPrinter _constructDefaultPrettyPrinter() {
        return new DefaultPrettyPrinter();
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public String _asString(BigDecimal value) throws IOException {
        if (JsonGenerator.Feature.WRITE_BIGDECIMAL_AS_PLAIN.enabledIn(this._features)) {
            int scale = value.scale();
            if (scale < -9999 || scale > MAX_BIG_DECIMAL_SCALE) {
                _reportError(String.format("Attempt to write plain `java.math.BigDecimal` (see JsonGenerator.Feature.WRITE_BIGDECIMAL_AS_PLAIN) with illegal scale (%d): needs to be between [-%d, %d]", Integer.valueOf(scale), Integer.valueOf(MAX_BIG_DECIMAL_SCALE), Integer.valueOf(MAX_BIG_DECIMAL_SCALE)));
            }
            return value.toPlainString();
        }
        return value.toString();
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public final int _decodeSurrogate(int surr1, int surr2) throws IOException {
        if (surr2 < 56320 || surr2 > 57343) {
            String msg = String.format("Incomplete surrogate pair: first char 0x%04X, second 0x%04X", Integer.valueOf(surr1), Integer.valueOf(surr2));
            _reportError(msg);
        }
        int c = 65536 + ((surr1 - SURR1_FIRST) << 10) + (surr2 - SURR2_FIRST);
        return c;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public void _checkRangeBoundsForByteArray(byte[] data, int offset, int len) throws IOException {
        if (data == null) {
            _reportError("Invalid `byte[]` argument: `null`");
        }
        int dataLen = data.length;
        int end = offset + len;
        int anyNegs = offset | len | end | (dataLen - end);
        if (anyNegs < 0) {
            _reportError(String.format("Invalid 'offset' (%d) and/or 'len' (%d) arguments for `byte[]` of length %d", Integer.valueOf(offset), Integer.valueOf(len), Integer.valueOf(dataLen)));
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public void _checkRangeBoundsForCharArray(char[] data, int offset, int len) throws IOException {
        if (data == null) {
            _reportError("Invalid `char[]` argument: `null`");
        }
        int dataLen = data.length;
        int end = offset + len;
        int anyNegs = offset | len | end | (dataLen - end);
        if (anyNegs < 0) {
            _reportError(String.format("Invalid 'offset' (%d) and/or 'len' (%d) arguments for `char[]` of length %d", Integer.valueOf(offset), Integer.valueOf(len), Integer.valueOf(dataLen)));
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public void _checkRangeBoundsForString(String data, int offset, int len) throws IOException {
        if (data == null) {
            _reportError("Invalid `String` argument: `null`");
        }
        int dataLen = data.length();
        int end = offset + len;
        int anyNegs = offset | len | end | (dataLen - end);
        if (anyNegs < 0) {
            _reportError(String.format("Invalid 'offset' (%d) and/or 'len' (%d) arguments for `String` of length %d", Integer.valueOf(offset), Integer.valueOf(len), Integer.valueOf(dataLen)));
        }
    }
}
