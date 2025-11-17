package com.fasterxml.jackson.core;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.io.CharacterEscapes;
import com.fasterxml.jackson.core.io.SerializedString;
import com.fasterxml.jackson.core.json.JsonReadFeature;
import com.fasterxml.jackson.core.json.JsonWriteFeature;
import org.springframework.asm.Opcodes;

/* loaded from: server.jar:BOOT-INF/lib/jackson-core-2.15.3.jar:com/fasterxml/jackson/core/JsonFactoryBuilder.class */
public class JsonFactoryBuilder extends TSFBuilder<JsonFactory, JsonFactoryBuilder> {
    protected CharacterEscapes _characterEscapes;
    protected SerializableString _rootValueSeparator;
    protected int _maximumNonEscapedChar;
    protected char _quoteChar;

    public JsonFactoryBuilder() {
        this._quoteChar = '\"';
        this._rootValueSeparator = JsonFactory.DEFAULT_ROOT_VALUE_SEPARATOR;
        this._maximumNonEscapedChar = 0;
    }

    public JsonFactoryBuilder(JsonFactory base) {
        super(base);
        this._quoteChar = '\"';
        this._characterEscapes = base.getCharacterEscapes();
        this._rootValueSeparator = base._rootValueSeparator;
        this._maximumNonEscapedChar = base._maximumNonEscapedChar;
    }

    /* JADX WARN: Can't rename method to resolve collision */
    @Override // com.fasterxml.jackson.core.TSFBuilder
    public JsonFactoryBuilder enable(JsonReadFeature f) {
        _legacyEnable(f.mappedFeature());
        return this;
    }

    /* JADX WARN: Can't rename method to resolve collision */
    @Override // com.fasterxml.jackson.core.TSFBuilder
    public JsonFactoryBuilder enable(JsonReadFeature first, JsonReadFeature... other) {
        _legacyEnable(first.mappedFeature());
        enable(first);
        for (JsonReadFeature f : other) {
            _legacyEnable(f.mappedFeature());
        }
        return this;
    }

    /* JADX WARN: Can't rename method to resolve collision */
    @Override // com.fasterxml.jackson.core.TSFBuilder
    public JsonFactoryBuilder disable(JsonReadFeature f) {
        _legacyDisable(f.mappedFeature());
        return this;
    }

    /* JADX WARN: Can't rename method to resolve collision */
    @Override // com.fasterxml.jackson.core.TSFBuilder
    public JsonFactoryBuilder disable(JsonReadFeature first, JsonReadFeature... other) {
        _legacyDisable(first.mappedFeature());
        for (JsonReadFeature f : other) {
            _legacyEnable(f.mappedFeature());
        }
        return this;
    }

    /* JADX WARN: Can't rename method to resolve collision */
    @Override // com.fasterxml.jackson.core.TSFBuilder
    public JsonFactoryBuilder configure(JsonReadFeature f, boolean state) {
        return state ? enable(f) : disable(f);
    }

    /* JADX WARN: Can't rename method to resolve collision */
    @Override // com.fasterxml.jackson.core.TSFBuilder
    public JsonFactoryBuilder enable(JsonWriteFeature f) {
        JsonGenerator.Feature old = f.mappedFeature();
        if (old != null) {
            _legacyEnable(old);
        }
        return this;
    }

    /* JADX WARN: Can't rename method to resolve collision */
    @Override // com.fasterxml.jackson.core.TSFBuilder
    public JsonFactoryBuilder enable(JsonWriteFeature first, JsonWriteFeature... other) {
        _legacyEnable(first.mappedFeature());
        for (JsonWriteFeature f : other) {
            _legacyEnable(f.mappedFeature());
        }
        return this;
    }

    /* JADX WARN: Can't rename method to resolve collision */
    @Override // com.fasterxml.jackson.core.TSFBuilder
    public JsonFactoryBuilder disable(JsonWriteFeature f) {
        _legacyDisable(f.mappedFeature());
        return this;
    }

    /* JADX WARN: Can't rename method to resolve collision */
    @Override // com.fasterxml.jackson.core.TSFBuilder
    public JsonFactoryBuilder disable(JsonWriteFeature first, JsonWriteFeature... other) {
        _legacyDisable(first.mappedFeature());
        for (JsonWriteFeature f : other) {
            _legacyDisable(f.mappedFeature());
        }
        return this;
    }

    /* JADX WARN: Can't rename method to resolve collision */
    @Override // com.fasterxml.jackson.core.TSFBuilder
    public JsonFactoryBuilder configure(JsonWriteFeature f, boolean state) {
        return state ? enable(f) : disable(f);
    }

    public JsonFactoryBuilder characterEscapes(CharacterEscapes esc) {
        this._characterEscapes = esc;
        return this;
    }

    public JsonFactoryBuilder rootValueSeparator(String sep) {
        this._rootValueSeparator = sep == null ? null : new SerializedString(sep);
        return this;
    }

    public JsonFactoryBuilder rootValueSeparator(SerializableString sep) {
        this._rootValueSeparator = sep;
        return this;
    }

    public JsonFactoryBuilder highestNonEscapedChar(int maxNonEscaped) {
        this._maximumNonEscapedChar = maxNonEscaped <= 0 ? 0 : Math.max(Opcodes.LAND, maxNonEscaped);
        return this;
    }

    public JsonFactoryBuilder quoteChar(char ch2) {
        if (ch2 > 127) {
            throw new IllegalArgumentException("Can only use Unicode characters up to 0x7F as quote characters");
        }
        this._quoteChar = ch2;
        return this;
    }

    public CharacterEscapes characterEscapes() {
        return this._characterEscapes;
    }

    public SerializableString rootValueSeparator() {
        return this._rootValueSeparator;
    }

    public int highestNonEscapedChar() {
        return this._maximumNonEscapedChar;
    }

    public char quoteChar() {
        return this._quoteChar;
    }

    @Override // com.fasterxml.jackson.core.TSFBuilder
    public JsonFactory build() {
        return new JsonFactory(this);
    }
}
