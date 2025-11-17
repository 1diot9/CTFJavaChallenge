package com.fasterxml.jackson.core.json;

import com.fasterxml.jackson.core.FormatFeature;
import com.fasterxml.jackson.core.JsonGenerator;

/* loaded from: agent.jar:BOOT-INF/lib/jackson-core-2.15.3.jar:com/fasterxml/jackson/core/json/JsonWriteFeature.class */
public enum JsonWriteFeature implements FormatFeature {
    QUOTE_FIELD_NAMES(true, JsonGenerator.Feature.QUOTE_FIELD_NAMES),
    WRITE_NAN_AS_STRINGS(true, JsonGenerator.Feature.QUOTE_NON_NUMERIC_NUMBERS),
    WRITE_NUMBERS_AS_STRINGS(false, JsonGenerator.Feature.WRITE_NUMBERS_AS_STRINGS),
    ESCAPE_NON_ASCII(false, JsonGenerator.Feature.ESCAPE_NON_ASCII),
    WRITE_HEX_UPPER_CASE(true, JsonGenerator.Feature.WRITE_HEX_UPPER_CASE);

    private final boolean _defaultState;
    private final int _mask = 1 << ordinal();
    private final JsonGenerator.Feature _mappedFeature;

    public static int collectDefaults() {
        int flags = 0;
        for (JsonWriteFeature f : values()) {
            if (f.enabledByDefault()) {
                flags |= f.getMask();
            }
        }
        return flags;
    }

    JsonWriteFeature(boolean defaultState, JsonGenerator.Feature mapTo) {
        this._defaultState = defaultState;
        this._mappedFeature = mapTo;
    }

    @Override // com.fasterxml.jackson.core.FormatFeature, com.fasterxml.jackson.core.util.JacksonFeature
    public boolean enabledByDefault() {
        return this._defaultState;
    }

    @Override // com.fasterxml.jackson.core.FormatFeature, com.fasterxml.jackson.core.util.JacksonFeature
    public int getMask() {
        return this._mask;
    }

    @Override // com.fasterxml.jackson.core.FormatFeature, com.fasterxml.jackson.core.util.JacksonFeature
    public boolean enabledIn(int flags) {
        return (flags & this._mask) != 0;
    }

    public JsonGenerator.Feature mappedFeature() {
        return this._mappedFeature;
    }
}
