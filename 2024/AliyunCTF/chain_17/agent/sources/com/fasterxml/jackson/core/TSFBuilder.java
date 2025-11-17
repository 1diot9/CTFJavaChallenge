package com.fasterxml.jackson.core;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.TSFBuilder;
import com.fasterxml.jackson.core.io.InputDecorator;
import com.fasterxml.jackson.core.io.OutputDecorator;
import com.fasterxml.jackson.core.json.JsonReadFeature;
import com.fasterxml.jackson.core.json.JsonWriteFeature;

/* loaded from: agent.jar:BOOT-INF/lib/jackson-core-2.15.3.jar:com/fasterxml/jackson/core/TSFBuilder.class */
public abstract class TSFBuilder<F extends JsonFactory, B extends TSFBuilder<F, B>> {
    protected static final int DEFAULT_FACTORY_FEATURE_FLAGS = JsonFactory.Feature.collectDefaults();
    protected static final int DEFAULT_PARSER_FEATURE_FLAGS = JsonParser.Feature.collectDefaults();
    protected static final int DEFAULT_GENERATOR_FEATURE_FLAGS = JsonGenerator.Feature.collectDefaults();
    protected int _factoryFeatures;
    protected int _streamReadFeatures;
    protected int _streamWriteFeatures;
    protected InputDecorator _inputDecorator;
    protected OutputDecorator _outputDecorator;
    protected StreamReadConstraints _streamReadConstraints;

    public abstract F build();

    /* JADX INFO: Access modifiers changed from: protected */
    public TSFBuilder() {
        this._factoryFeatures = DEFAULT_FACTORY_FEATURE_FLAGS;
        this._streamReadFeatures = DEFAULT_PARSER_FEATURE_FLAGS;
        this._streamWriteFeatures = DEFAULT_GENERATOR_FEATURE_FLAGS;
        this._inputDecorator = null;
        this._outputDecorator = null;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public TSFBuilder(JsonFactory base) {
        this(base._factoryFeatures, base._parserFeatures, base._generatorFeatures);
        this._streamReadConstraints = base._streamReadConstraints;
    }

    protected TSFBuilder(int factoryFeatures, int parserFeatures, int generatorFeatures) {
        this._factoryFeatures = factoryFeatures;
        this._streamReadFeatures = parserFeatures;
        this._streamWriteFeatures = generatorFeatures;
    }

    public int factoryFeaturesMask() {
        return this._factoryFeatures;
    }

    public int streamReadFeatures() {
        return this._streamReadFeatures;
    }

    public int streamWriteFeatures() {
        return this._streamWriteFeatures;
    }

    public InputDecorator inputDecorator() {
        return this._inputDecorator;
    }

    public OutputDecorator outputDecorator() {
        return this._outputDecorator;
    }

    public B enable(JsonFactory.Feature f) {
        this._factoryFeatures |= f.getMask();
        return _this();
    }

    public B disable(JsonFactory.Feature f) {
        this._factoryFeatures &= f.getMask() ^ (-1);
        return _this();
    }

    public B configure(JsonFactory.Feature f, boolean state) {
        return state ? enable(f) : disable(f);
    }

    public B enable(StreamReadFeature f) {
        this._streamReadFeatures |= f.mappedFeature().getMask();
        return _this();
    }

    public B enable(StreamReadFeature first, StreamReadFeature... other) {
        this._streamReadFeatures |= first.mappedFeature().getMask();
        for (StreamReadFeature f : other) {
            this._streamReadFeatures |= f.mappedFeature().getMask();
        }
        return _this();
    }

    public B disable(StreamReadFeature f) {
        this._streamReadFeatures &= f.mappedFeature().getMask() ^ (-1);
        return _this();
    }

    public B disable(StreamReadFeature first, StreamReadFeature... other) {
        this._streamReadFeatures &= first.mappedFeature().getMask() ^ (-1);
        for (StreamReadFeature f : other) {
            this._streamReadFeatures &= f.mappedFeature().getMask() ^ (-1);
        }
        return _this();
    }

    public B configure(StreamReadFeature f, boolean state) {
        return state ? enable(f) : disable(f);
    }

    public B enable(StreamWriteFeature f) {
        this._streamWriteFeatures |= f.mappedFeature().getMask();
        return _this();
    }

    public B enable(StreamWriteFeature first, StreamWriteFeature... other) {
        this._streamWriteFeatures |= first.mappedFeature().getMask();
        for (StreamWriteFeature f : other) {
            this._streamWriteFeatures |= f.mappedFeature().getMask();
        }
        return _this();
    }

    public B disable(StreamWriteFeature f) {
        this._streamWriteFeatures &= f.mappedFeature().getMask() ^ (-1);
        return _this();
    }

    public B disable(StreamWriteFeature first, StreamWriteFeature... other) {
        this._streamWriteFeatures &= first.mappedFeature().getMask() ^ (-1);
        for (StreamWriteFeature f : other) {
            this._streamWriteFeatures &= f.mappedFeature().getMask() ^ (-1);
        }
        return _this();
    }

    public B configure(StreamWriteFeature f, boolean state) {
        return state ? enable(f) : disable(f);
    }

    public B enable(JsonReadFeature f) {
        return _failNonJSON(f);
    }

    public B enable(JsonReadFeature first, JsonReadFeature... other) {
        return _failNonJSON(first);
    }

    public B disable(JsonReadFeature f) {
        return _failNonJSON(f);
    }

    public B disable(JsonReadFeature first, JsonReadFeature... other) {
        return _failNonJSON(first);
    }

    public B configure(JsonReadFeature f, boolean state) {
        return _failNonJSON(f);
    }

    private B _failNonJSON(Object feature) {
        throw new IllegalArgumentException("Feature " + feature.getClass().getName() + "#" + feature.toString() + " not supported for non-JSON backend");
    }

    public B enable(JsonWriteFeature f) {
        return _failNonJSON(f);
    }

    public B enable(JsonWriteFeature first, JsonWriteFeature... other) {
        return _failNonJSON(first);
    }

    public B disable(JsonWriteFeature f) {
        return _failNonJSON(f);
    }

    public B disable(JsonWriteFeature first, JsonWriteFeature... other) {
        return _failNonJSON(first);
    }

    public B configure(JsonWriteFeature f, boolean state) {
        return _failNonJSON(f);
    }

    public B inputDecorator(InputDecorator dec) {
        this._inputDecorator = dec;
        return _this();
    }

    public B outputDecorator(OutputDecorator dec) {
        this._outputDecorator = dec;
        return _this();
    }

    public B streamReadConstraints(StreamReadConstraints streamReadConstraints) {
        this._streamReadConstraints = streamReadConstraints;
        return _this();
    }

    protected final B _this() {
        return this;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public void _legacyEnable(JsonParser.Feature f) {
        if (f != null) {
            this._streamReadFeatures |= f.getMask();
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public void _legacyDisable(JsonParser.Feature f) {
        if (f != null) {
            this._streamReadFeatures &= f.getMask() ^ (-1);
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public void _legacyEnable(JsonGenerator.Feature f) {
        if (f != null) {
            this._streamWriteFeatures |= f.getMask();
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public void _legacyDisable(JsonGenerator.Feature f) {
        if (f != null) {
            this._streamWriteFeatures &= f.getMask() ^ (-1);
        }
    }
}
