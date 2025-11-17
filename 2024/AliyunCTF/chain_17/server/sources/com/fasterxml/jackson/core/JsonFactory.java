package com.fasterxml.jackson.core;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.format.InputAccessor;
import com.fasterxml.jackson.core.format.MatchStrength;
import com.fasterxml.jackson.core.io.CharacterEscapes;
import com.fasterxml.jackson.core.io.ContentReference;
import com.fasterxml.jackson.core.io.IOContext;
import com.fasterxml.jackson.core.io.InputDecorator;
import com.fasterxml.jackson.core.io.OutputDecorator;
import com.fasterxml.jackson.core.io.SerializedString;
import com.fasterxml.jackson.core.io.UTF8Writer;
import com.fasterxml.jackson.core.json.ByteSourceJsonBootstrapper;
import com.fasterxml.jackson.core.json.PackageVersion;
import com.fasterxml.jackson.core.json.ReaderBasedJsonParser;
import com.fasterxml.jackson.core.json.UTF8DataInputJsonParser;
import com.fasterxml.jackson.core.json.UTF8JsonGenerator;
import com.fasterxml.jackson.core.json.WriterBasedJsonGenerator;
import com.fasterxml.jackson.core.json.async.NonBlockingByteBufferJsonParser;
import com.fasterxml.jackson.core.json.async.NonBlockingJsonParser;
import com.fasterxml.jackson.core.sym.ByteQuadsCanonicalizer;
import com.fasterxml.jackson.core.sym.CharsToNameCanonicalizer;
import com.fasterxml.jackson.core.util.BufferRecycler;
import com.fasterxml.jackson.core.util.BufferRecyclers;
import com.fasterxml.jackson.core.util.DefaultPrettyPrinter;
import com.fasterxml.jackson.core.util.JacksonFeature;
import java.io.CharArrayReader;
import java.io.DataInput;
import java.io.DataOutput;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.io.Serializable;
import java.io.StringReader;
import java.io.Writer;
import java.net.URL;
import java.util.Objects;

/* loaded from: server.jar:BOOT-INF/lib/jackson-core-2.15.3.jar:com/fasterxml/jackson/core/JsonFactory.class */
public class JsonFactory extends TokenStreamFactory implements Versioned, Serializable {
    private static final long serialVersionUID = 2;
    public static final String FORMAT_NAME_JSON = "JSON";
    protected static final int DEFAULT_FACTORY_FEATURE_FLAGS = Feature.collectDefaults();
    protected static final int DEFAULT_PARSER_FEATURE_FLAGS = JsonParser.Feature.collectDefaults();
    protected static final int DEFAULT_GENERATOR_FEATURE_FLAGS = JsonGenerator.Feature.collectDefaults();
    public static final SerializableString DEFAULT_ROOT_VALUE_SEPARATOR = DefaultPrettyPrinter.DEFAULT_ROOT_VALUE_SEPARATOR;
    public static final char DEFAULT_QUOTE_CHAR = '\"';
    protected final transient CharsToNameCanonicalizer _rootCharSymbols;
    protected final transient ByteQuadsCanonicalizer _byteSymbolCanonicalizer;
    protected int _factoryFeatures;
    protected int _parserFeatures;
    protected int _generatorFeatures;
    protected ObjectCodec _objectCodec;
    protected CharacterEscapes _characterEscapes;
    protected StreamReadConstraints _streamReadConstraints;
    protected InputDecorator _inputDecorator;
    protected OutputDecorator _outputDecorator;
    protected SerializableString _rootValueSeparator;
    protected int _maximumNonEscapedChar;
    protected final char _quoteChar;

    /* loaded from: server.jar:BOOT-INF/lib/jackson-core-2.15.3.jar:com/fasterxml/jackson/core/JsonFactory$Feature.class */
    public enum Feature implements JacksonFeature {
        INTERN_FIELD_NAMES(true),
        CANONICALIZE_FIELD_NAMES(true),
        FAIL_ON_SYMBOL_HASH_OVERFLOW(true),
        USE_THREAD_LOCAL_FOR_BUFFER_RECYCLING(true),
        CHARSET_DETECTION(true);

        private final boolean _defaultState;

        public static int collectDefaults() {
            int flags = 0;
            for (Feature f : values()) {
                if (f.enabledByDefault()) {
                    flags |= f.getMask();
                }
            }
            return flags;
        }

        Feature(boolean defaultState) {
            this._defaultState = defaultState;
        }

        @Override // com.fasterxml.jackson.core.util.JacksonFeature
        public boolean enabledByDefault() {
            return this._defaultState;
        }

        @Override // com.fasterxml.jackson.core.util.JacksonFeature
        public boolean enabledIn(int flags) {
            return (flags & getMask()) != 0;
        }

        @Override // com.fasterxml.jackson.core.util.JacksonFeature
        public int getMask() {
            return 1 << ordinal();
        }
    }

    public JsonFactory() {
        this((ObjectCodec) null);
    }

    public JsonFactory(ObjectCodec oc) {
        this._rootCharSymbols = CharsToNameCanonicalizer.createRoot();
        this._byteSymbolCanonicalizer = ByteQuadsCanonicalizer.createRoot();
        this._factoryFeatures = DEFAULT_FACTORY_FEATURE_FLAGS;
        this._parserFeatures = DEFAULT_PARSER_FEATURE_FLAGS;
        this._generatorFeatures = DEFAULT_GENERATOR_FEATURE_FLAGS;
        this._rootValueSeparator = DEFAULT_ROOT_VALUE_SEPARATOR;
        this._objectCodec = oc;
        this._quoteChar = '\"';
        this._streamReadConstraints = StreamReadConstraints.defaults();
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public JsonFactory(JsonFactory src, ObjectCodec codec) {
        this._rootCharSymbols = CharsToNameCanonicalizer.createRoot();
        this._byteSymbolCanonicalizer = ByteQuadsCanonicalizer.createRoot();
        this._factoryFeatures = DEFAULT_FACTORY_FEATURE_FLAGS;
        this._parserFeatures = DEFAULT_PARSER_FEATURE_FLAGS;
        this._generatorFeatures = DEFAULT_GENERATOR_FEATURE_FLAGS;
        this._rootValueSeparator = DEFAULT_ROOT_VALUE_SEPARATOR;
        this._objectCodec = codec;
        this._factoryFeatures = src._factoryFeatures;
        this._parserFeatures = src._parserFeatures;
        this._generatorFeatures = src._generatorFeatures;
        this._inputDecorator = src._inputDecorator;
        this._outputDecorator = src._outputDecorator;
        this._streamReadConstraints = src._streamReadConstraints == null ? StreamReadConstraints.defaults() : src._streamReadConstraints;
        this._characterEscapes = src._characterEscapes;
        this._rootValueSeparator = src._rootValueSeparator;
        this._maximumNonEscapedChar = src._maximumNonEscapedChar;
        this._quoteChar = src._quoteChar;
    }

    public JsonFactory(JsonFactoryBuilder b) {
        this._rootCharSymbols = CharsToNameCanonicalizer.createRoot();
        this._byteSymbolCanonicalizer = ByteQuadsCanonicalizer.createRoot();
        this._factoryFeatures = DEFAULT_FACTORY_FEATURE_FLAGS;
        this._parserFeatures = DEFAULT_PARSER_FEATURE_FLAGS;
        this._generatorFeatures = DEFAULT_GENERATOR_FEATURE_FLAGS;
        this._rootValueSeparator = DEFAULT_ROOT_VALUE_SEPARATOR;
        this._objectCodec = null;
        this._factoryFeatures = b._factoryFeatures;
        this._parserFeatures = b._streamReadFeatures;
        this._generatorFeatures = b._streamWriteFeatures;
        this._inputDecorator = b._inputDecorator;
        this._outputDecorator = b._outputDecorator;
        this._streamReadConstraints = b._streamReadConstraints == null ? StreamReadConstraints.defaults() : b._streamReadConstraints;
        this._characterEscapes = b._characterEscapes;
        this._rootValueSeparator = b._rootValueSeparator;
        this._maximumNonEscapedChar = b._maximumNonEscapedChar;
        this._quoteChar = b._quoteChar;
    }

    protected JsonFactory(TSFBuilder<?, ?> b, boolean bogus) {
        this._rootCharSymbols = CharsToNameCanonicalizer.createRoot();
        this._byteSymbolCanonicalizer = ByteQuadsCanonicalizer.createRoot();
        this._factoryFeatures = DEFAULT_FACTORY_FEATURE_FLAGS;
        this._parserFeatures = DEFAULT_PARSER_FEATURE_FLAGS;
        this._generatorFeatures = DEFAULT_GENERATOR_FEATURE_FLAGS;
        this._rootValueSeparator = DEFAULT_ROOT_VALUE_SEPARATOR;
        this._objectCodec = null;
        this._factoryFeatures = b._factoryFeatures;
        this._parserFeatures = b._streamReadFeatures;
        this._generatorFeatures = b._streamWriteFeatures;
        this._inputDecorator = b._inputDecorator;
        this._outputDecorator = b._outputDecorator;
        this._streamReadConstraints = b._streamReadConstraints == null ? StreamReadConstraints.defaults() : b._streamReadConstraints;
        this._characterEscapes = null;
        this._rootValueSeparator = null;
        this._maximumNonEscapedChar = 0;
        this._quoteChar = '\"';
    }

    public TSFBuilder<?, ?> rebuild() {
        _requireJSONFactory("Factory implementation for format (%s) MUST override `rebuild()` method");
        return new JsonFactoryBuilder(this);
    }

    public static TSFBuilder<?, ?> builder() {
        return new JsonFactoryBuilder();
    }

    public JsonFactory copy() {
        _checkInvalidCopy(JsonFactory.class);
        return new JsonFactory(this, (ObjectCodec) null);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public void _checkInvalidCopy(Class<?> exp) {
        if (getClass() != exp) {
            throw new IllegalStateException("Failed copy(): " + getClass().getName() + " (version: " + version() + ") does not override copy(); it has to");
        }
    }

    protected Object readResolve() {
        return new JsonFactory(this, this._objectCodec);
    }

    @Override // com.fasterxml.jackson.core.TokenStreamFactory
    public boolean requiresPropertyOrdering() {
        return false;
    }

    @Override // com.fasterxml.jackson.core.TokenStreamFactory
    public boolean canHandleBinaryNatively() {
        return false;
    }

    public boolean canUseCharArrays() {
        return true;
    }

    @Override // com.fasterxml.jackson.core.TokenStreamFactory
    public boolean canParseAsync() {
        return _isJSONFactory();
    }

    @Override // com.fasterxml.jackson.core.TokenStreamFactory
    public Class<? extends FormatFeature> getFormatReadFeatureType() {
        return null;
    }

    @Override // com.fasterxml.jackson.core.TokenStreamFactory
    public Class<? extends FormatFeature> getFormatWriteFeatureType() {
        return null;
    }

    @Override // com.fasterxml.jackson.core.TokenStreamFactory
    public boolean canUseSchema(FormatSchema schema) {
        String ourFormat;
        return (schema == null || (ourFormat = getFormatName()) == null || !ourFormat.equals(schema.getSchemaType())) ? false : true;
    }

    @Override // com.fasterxml.jackson.core.TokenStreamFactory
    public String getFormatName() {
        if (getClass() == JsonFactory.class) {
            return FORMAT_NAME_JSON;
        }
        return null;
    }

    public MatchStrength hasFormat(InputAccessor acc) throws IOException {
        if (getClass() == JsonFactory.class) {
            return hasJSONFormat(acc);
        }
        return null;
    }

    public boolean requiresCustomCodec() {
        return false;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public MatchStrength hasJSONFormat(InputAccessor acc) throws IOException {
        return ByteSourceJsonBootstrapper.hasJSONFormat(acc);
    }

    @Override // com.fasterxml.jackson.core.Versioned
    public Version version() {
        return PackageVersion.VERSION;
    }

    @Deprecated
    public final JsonFactory configure(Feature f, boolean state) {
        return state ? enable(f) : disable(f);
    }

    @Deprecated
    public JsonFactory enable(Feature f) {
        this._factoryFeatures |= f.getMask();
        return this;
    }

    @Deprecated
    public JsonFactory disable(Feature f) {
        this._factoryFeatures &= f.getMask() ^ (-1);
        return this;
    }

    public final boolean isEnabled(Feature f) {
        return (this._factoryFeatures & f.getMask()) != 0;
    }

    @Override // com.fasterxml.jackson.core.TokenStreamFactory
    public final int getParserFeatures() {
        return this._parserFeatures;
    }

    @Override // com.fasterxml.jackson.core.TokenStreamFactory
    public final int getGeneratorFeatures() {
        return this._generatorFeatures;
    }

    @Override // com.fasterxml.jackson.core.TokenStreamFactory
    public int getFormatParserFeatures() {
        return 0;
    }

    @Override // com.fasterxml.jackson.core.TokenStreamFactory
    public int getFormatGeneratorFeatures() {
        return 0;
    }

    @Override // com.fasterxml.jackson.core.TokenStreamFactory
    public StreamReadConstraints streamReadConstraints() {
        return this._streamReadConstraints;
    }

    public JsonFactory setStreamReadConstraints(StreamReadConstraints src) {
        this._streamReadConstraints = (StreamReadConstraints) Objects.requireNonNull(src);
        return this;
    }

    public final JsonFactory configure(JsonParser.Feature f, boolean state) {
        return state ? enable(f) : disable(f);
    }

    public JsonFactory enable(JsonParser.Feature f) {
        this._parserFeatures |= f.getMask();
        return this;
    }

    public JsonFactory disable(JsonParser.Feature f) {
        this._parserFeatures &= f.getMask() ^ (-1);
        return this;
    }

    @Override // com.fasterxml.jackson.core.TokenStreamFactory
    public final boolean isEnabled(JsonParser.Feature f) {
        return (this._parserFeatures & f.getMask()) != 0;
    }

    public final boolean isEnabled(StreamReadFeature f) {
        return (this._parserFeatures & f.mappedFeature().getMask()) != 0;
    }

    public InputDecorator getInputDecorator() {
        return this._inputDecorator;
    }

    @Deprecated
    public JsonFactory setInputDecorator(InputDecorator d) {
        this._inputDecorator = d;
        return this;
    }

    public final JsonFactory configure(JsonGenerator.Feature f, boolean state) {
        return state ? enable(f) : disable(f);
    }

    public JsonFactory enable(JsonGenerator.Feature f) {
        this._generatorFeatures |= f.getMask();
        return this;
    }

    public JsonFactory disable(JsonGenerator.Feature f) {
        this._generatorFeatures &= f.getMask() ^ (-1);
        return this;
    }

    @Override // com.fasterxml.jackson.core.TokenStreamFactory
    public final boolean isEnabled(JsonGenerator.Feature f) {
        return (this._generatorFeatures & f.getMask()) != 0;
    }

    public final boolean isEnabled(StreamWriteFeature f) {
        return (this._generatorFeatures & f.mappedFeature().getMask()) != 0;
    }

    public CharacterEscapes getCharacterEscapes() {
        return this._characterEscapes;
    }

    public JsonFactory setCharacterEscapes(CharacterEscapes esc) {
        this._characterEscapes = esc;
        return this;
    }

    public OutputDecorator getOutputDecorator() {
        return this._outputDecorator;
    }

    @Deprecated
    public JsonFactory setOutputDecorator(OutputDecorator d) {
        this._outputDecorator = d;
        return this;
    }

    public JsonFactory setRootValueSeparator(String sep) {
        this._rootValueSeparator = sep == null ? null : new SerializedString(sep);
        return this;
    }

    public String getRootValueSeparator() {
        if (this._rootValueSeparator == null) {
            return null;
        }
        return this._rootValueSeparator.getValue();
    }

    public JsonFactory setCodec(ObjectCodec oc) {
        this._objectCodec = oc;
        return this;
    }

    public ObjectCodec getCodec() {
        return this._objectCodec;
    }

    @Override // com.fasterxml.jackson.core.TokenStreamFactory
    public JsonParser createParser(File f) throws IOException, JsonParseException {
        IOContext ctxt = _createContext(_createContentReference(f), true);
        InputStream in = _fileInputStream(f);
        return _createParser(_decorate(in, ctxt), ctxt);
    }

    @Override // com.fasterxml.jackson.core.TokenStreamFactory
    public JsonParser createParser(URL url) throws IOException, JsonParseException {
        IOContext ctxt = _createContext(_createContentReference(url), true);
        InputStream in = _optimizedStreamFromURL(url);
        return _createParser(_decorate(in, ctxt), ctxt);
    }

    @Override // com.fasterxml.jackson.core.TokenStreamFactory
    public JsonParser createParser(InputStream in) throws IOException, JsonParseException {
        IOContext ctxt = _createContext(_createContentReference(in), false);
        return _createParser(_decorate(in, ctxt), ctxt);
    }

    @Override // com.fasterxml.jackson.core.TokenStreamFactory
    public JsonParser createParser(Reader r) throws IOException, JsonParseException {
        IOContext ctxt = _createContext(_createContentReference(r), false);
        return _createParser(_decorate(r, ctxt), ctxt);
    }

    @Override // com.fasterxml.jackson.core.TokenStreamFactory
    public JsonParser createParser(byte[] data) throws IOException, JsonParseException {
        InputStream in;
        IOContext ctxt = _createContext(_createContentReference(data), true);
        if (this._inputDecorator != null && (in = this._inputDecorator.decorate(ctxt, data, 0, data.length)) != null) {
            return _createParser(in, ctxt);
        }
        return _createParser(data, 0, data.length, ctxt);
    }

    @Override // com.fasterxml.jackson.core.TokenStreamFactory
    public JsonParser createParser(byte[] data, int offset, int len) throws IOException, JsonParseException {
        InputStream in;
        _checkRangeBoundsForByteArray(data, offset, len);
        IOContext ctxt = _createContext(_createContentReference(data, offset, len), true);
        if (this._inputDecorator != null && (in = this._inputDecorator.decorate(ctxt, data, offset, len)) != null) {
            return _createParser(in, ctxt);
        }
        return _createParser(data, offset, len, ctxt);
    }

    @Override // com.fasterxml.jackson.core.TokenStreamFactory
    public JsonParser createParser(String content) throws IOException, JsonParseException {
        int strLen = content.length();
        if (this._inputDecorator != null || strLen > 32768 || !canUseCharArrays()) {
            return createParser(new StringReader(content));
        }
        IOContext ctxt = _createContext(_createContentReference(content), true);
        char[] buf = ctxt.allocTokenBuffer(strLen);
        content.getChars(0, strLen, buf, 0);
        return _createParser(buf, 0, strLen, ctxt, true);
    }

    @Override // com.fasterxml.jackson.core.TokenStreamFactory
    public JsonParser createParser(char[] content) throws IOException {
        return createParser(content, 0, content.length);
    }

    @Override // com.fasterxml.jackson.core.TokenStreamFactory
    public JsonParser createParser(char[] content, int offset, int len) throws IOException {
        _checkRangeBoundsForCharArray(content, offset, len);
        if (this._inputDecorator != null) {
            return createParser(new CharArrayReader(content, offset, len));
        }
        return _createParser(content, offset, len, _createContext(_createContentReference(content, offset, len), true), false);
    }

    @Override // com.fasterxml.jackson.core.TokenStreamFactory
    public JsonParser createParser(DataInput in) throws IOException {
        IOContext ctxt = _createContext(_createContentReference(in), false);
        return _createParser(_decorate(in, ctxt), ctxt);
    }

    @Override // com.fasterxml.jackson.core.TokenStreamFactory
    public JsonParser createNonBlockingByteArrayParser() throws IOException {
        _requireJSONFactory("Non-blocking source not (yet?) supported for this format (%s)");
        IOContext ctxt = _createNonBlockingContext(null);
        ByteQuadsCanonicalizer can = this._byteSymbolCanonicalizer.makeChild(this._factoryFeatures);
        return new NonBlockingJsonParser(ctxt, this._parserFeatures, can);
    }

    @Override // com.fasterxml.jackson.core.TokenStreamFactory
    public JsonParser createNonBlockingByteBufferParser() throws IOException {
        _requireJSONFactory("Non-blocking source not (yet?) supported for this format (%s)");
        IOContext ctxt = _createNonBlockingContext(null);
        ByteQuadsCanonicalizer can = this._byteSymbolCanonicalizer.makeChild(this._factoryFeatures);
        return new NonBlockingByteBufferJsonParser(ctxt, this._parserFeatures, can);
    }

    @Override // com.fasterxml.jackson.core.TokenStreamFactory
    public JsonGenerator createGenerator(OutputStream out, JsonEncoding enc) throws IOException {
        IOContext ctxt = _createContext(_createContentReference(out), false);
        ctxt.setEncoding(enc);
        if (enc == JsonEncoding.UTF8) {
            return _createUTF8Generator(_decorate(out, ctxt), ctxt);
        }
        Writer w = _createWriter(out, enc, ctxt);
        return _createGenerator(_decorate(w, ctxt), ctxt);
    }

    @Override // com.fasterxml.jackson.core.TokenStreamFactory
    public JsonGenerator createGenerator(OutputStream out) throws IOException {
        return createGenerator(out, JsonEncoding.UTF8);
    }

    @Override // com.fasterxml.jackson.core.TokenStreamFactory
    public JsonGenerator createGenerator(Writer w) throws IOException {
        IOContext ctxt = _createContext(_createContentReference(w), false);
        return _createGenerator(_decorate(w, ctxt), ctxt);
    }

    @Override // com.fasterxml.jackson.core.TokenStreamFactory
    public JsonGenerator createGenerator(File f, JsonEncoding enc) throws IOException {
        OutputStream out = _fileOutputStream(f);
        IOContext ctxt = _createContext(_createContentReference(out), true);
        ctxt.setEncoding(enc);
        if (enc == JsonEncoding.UTF8) {
            return _createUTF8Generator(_decorate(out, ctxt), ctxt);
        }
        Writer w = _createWriter(out, enc, ctxt);
        return _createGenerator(_decorate(w, ctxt), ctxt);
    }

    @Override // com.fasterxml.jackson.core.TokenStreamFactory
    public JsonGenerator createGenerator(DataOutput out, JsonEncoding enc) throws IOException {
        return createGenerator(_createDataOutputWrapper(out), enc);
    }

    @Override // com.fasterxml.jackson.core.TokenStreamFactory
    public JsonGenerator createGenerator(DataOutput out) throws IOException {
        return createGenerator(_createDataOutputWrapper(out), JsonEncoding.UTF8);
    }

    @Deprecated
    public JsonParser createJsonParser(File f) throws IOException, JsonParseException {
        return createParser(f);
    }

    @Deprecated
    public JsonParser createJsonParser(URL url) throws IOException, JsonParseException {
        return createParser(url);
    }

    @Deprecated
    public JsonParser createJsonParser(InputStream in) throws IOException, JsonParseException {
        return createParser(in);
    }

    @Deprecated
    public JsonParser createJsonParser(Reader r) throws IOException, JsonParseException {
        return createParser(r);
    }

    @Deprecated
    public JsonParser createJsonParser(byte[] data) throws IOException, JsonParseException {
        return createParser(data);
    }

    @Deprecated
    public JsonParser createJsonParser(byte[] data, int offset, int len) throws IOException, JsonParseException {
        return createParser(data, offset, len);
    }

    @Deprecated
    public JsonParser createJsonParser(String content) throws IOException, JsonParseException {
        return createParser(content);
    }

    @Deprecated
    public JsonGenerator createJsonGenerator(OutputStream out, JsonEncoding enc) throws IOException {
        return createGenerator(out, enc);
    }

    @Deprecated
    public JsonGenerator createJsonGenerator(Writer out) throws IOException {
        return createGenerator(out);
    }

    @Deprecated
    public JsonGenerator createJsonGenerator(OutputStream out) throws IOException {
        return createGenerator(out, JsonEncoding.UTF8);
    }

    protected JsonParser _createParser(InputStream in, IOContext ctxt) throws IOException {
        try {
            return new ByteSourceJsonBootstrapper(ctxt, in).constructParser(this._parserFeatures, this._objectCodec, this._byteSymbolCanonicalizer, this._rootCharSymbols, this._factoryFeatures);
        } catch (IOException | RuntimeException e) {
            if (ctxt.isResourceManaged()) {
                try {
                    in.close();
                } catch (Exception e2) {
                    e.addSuppressed(e2);
                }
            }
            throw e;
        }
    }

    protected JsonParser _createParser(Reader r, IOContext ctxt) throws IOException {
        return new ReaderBasedJsonParser(ctxt, this._parserFeatures, r, this._objectCodec, this._rootCharSymbols.makeChild(this._factoryFeatures));
    }

    protected JsonParser _createParser(char[] data, int offset, int len, IOContext ctxt, boolean recyclable) throws IOException {
        return new ReaderBasedJsonParser(ctxt, this._parserFeatures, null, this._objectCodec, this._rootCharSymbols.makeChild(this._factoryFeatures), data, offset, offset + len, recyclable);
    }

    protected JsonParser _createParser(byte[] data, int offset, int len, IOContext ctxt) throws IOException {
        return new ByteSourceJsonBootstrapper(ctxt, data, offset, len).constructParser(this._parserFeatures, this._objectCodec, this._byteSymbolCanonicalizer, this._rootCharSymbols, this._factoryFeatures);
    }

    protected JsonParser _createParser(DataInput input, IOContext ctxt) throws IOException {
        _requireJSONFactory("InputData source not (yet?) supported for this format (%s)");
        int firstByte = ByteSourceJsonBootstrapper.skipUTF8BOM(input);
        ByteQuadsCanonicalizer can = this._byteSymbolCanonicalizer.makeChild(this._factoryFeatures);
        return new UTF8DataInputJsonParser(ctxt, this._parserFeatures, input, this._objectCodec, can, firstByte);
    }

    protected JsonGenerator _createGenerator(Writer out, IOContext ctxt) throws IOException {
        WriterBasedJsonGenerator gen = new WriterBasedJsonGenerator(ctxt, this._generatorFeatures, this._objectCodec, out, this._quoteChar);
        if (this._maximumNonEscapedChar > 0) {
            gen.setHighestNonEscapedChar(this._maximumNonEscapedChar);
        }
        if (this._characterEscapes != null) {
            gen.setCharacterEscapes(this._characterEscapes);
        }
        SerializableString rootSep = this._rootValueSeparator;
        if (rootSep != DEFAULT_ROOT_VALUE_SEPARATOR) {
            gen.setRootValueSeparator(rootSep);
        }
        return gen;
    }

    protected JsonGenerator _createUTF8Generator(OutputStream out, IOContext ctxt) throws IOException {
        UTF8JsonGenerator gen = new UTF8JsonGenerator(ctxt, this._generatorFeatures, this._objectCodec, out, this._quoteChar);
        if (this._maximumNonEscapedChar > 0) {
            gen.setHighestNonEscapedChar(this._maximumNonEscapedChar);
        }
        if (this._characterEscapes != null) {
            gen.setCharacterEscapes(this._characterEscapes);
        }
        SerializableString rootSep = this._rootValueSeparator;
        if (rootSep != DEFAULT_ROOT_VALUE_SEPARATOR) {
            gen.setRootValueSeparator(rootSep);
        }
        return gen;
    }

    protected Writer _createWriter(OutputStream out, JsonEncoding enc, IOContext ctxt) throws IOException {
        if (enc == JsonEncoding.UTF8) {
            return new UTF8Writer(ctxt, out);
        }
        return new OutputStreamWriter(out, enc.getJavaName());
    }

    protected final InputStream _decorate(InputStream in, IOContext ctxt) throws IOException {
        InputStream in2;
        if (this._inputDecorator != null && (in2 = this._inputDecorator.decorate(ctxt, in)) != null) {
            return in2;
        }
        return in;
    }

    protected final Reader _decorate(Reader in, IOContext ctxt) throws IOException {
        Reader in2;
        if (this._inputDecorator != null && (in2 = this._inputDecorator.decorate(ctxt, in)) != null) {
            return in2;
        }
        return in;
    }

    protected final DataInput _decorate(DataInput in, IOContext ctxt) throws IOException {
        DataInput in2;
        if (this._inputDecorator != null && (in2 = this._inputDecorator.decorate(ctxt, in)) != null) {
            return in2;
        }
        return in;
    }

    protected final OutputStream _decorate(OutputStream out, IOContext ctxt) throws IOException {
        OutputStream out2;
        if (this._outputDecorator != null && (out2 = this._outputDecorator.decorate(ctxt, out)) != null) {
            return out2;
        }
        return out;
    }

    protected final Writer _decorate(Writer out, IOContext ctxt) throws IOException {
        Writer out2;
        if (this._outputDecorator != null && (out2 = this._outputDecorator.decorate(ctxt, out)) != null) {
            return out2;
        }
        return out;
    }

    public BufferRecycler _getBufferRecycler() {
        if (Feature.USE_THREAD_LOCAL_FOR_BUFFER_RECYCLING.enabledIn(this._factoryFeatures)) {
            return BufferRecyclers.getBufferRecycler();
        }
        return new BufferRecycler();
    }

    protected IOContext _createContext(ContentReference contentRef, boolean resourceManaged) {
        if (contentRef == null) {
            contentRef = ContentReference.unknown();
        }
        return new IOContext(this._streamReadConstraints, _getBufferRecycler(), contentRef, resourceManaged);
    }

    @Deprecated
    protected IOContext _createContext(Object rawContentRef, boolean resourceManaged) {
        return new IOContext(this._streamReadConstraints, _getBufferRecycler(), _createContentReference(rawContentRef), resourceManaged);
    }

    protected IOContext _createNonBlockingContext(Object srcRef) {
        return new IOContext(this._streamReadConstraints, _getBufferRecycler(), _createContentReference(srcRef), false);
    }

    protected ContentReference _createContentReference(Object contentAccessor) {
        return ContentReference.construct(!canHandleBinaryNatively(), contentAccessor);
    }

    protected ContentReference _createContentReference(Object contentAccessor, int offset, int length) {
        return ContentReference.construct(!canHandleBinaryNatively(), contentAccessor, offset, length);
    }

    private final void _requireJSONFactory(String msg) {
        if (!_isJSONFactory()) {
            throw new UnsupportedOperationException(String.format(msg, getFormatName()));
        }
    }

    private final boolean _isJSONFactory() {
        return getFormatName() == FORMAT_NAME_JSON;
    }
}
