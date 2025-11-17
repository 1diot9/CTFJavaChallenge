package com.fasterxml.jackson.core;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.util.JacksonFeature;

/* loaded from: server.jar:BOOT-INF/lib/jackson-core-2.15.3.jar:com/fasterxml/jackson/core/StreamReadFeature.class */
public enum StreamReadFeature implements JacksonFeature {
    AUTO_CLOSE_SOURCE(JsonParser.Feature.AUTO_CLOSE_SOURCE),
    STRICT_DUPLICATE_DETECTION(JsonParser.Feature.STRICT_DUPLICATE_DETECTION),
    IGNORE_UNDEFINED(JsonParser.Feature.IGNORE_UNDEFINED),
    INCLUDE_SOURCE_IN_LOCATION(JsonParser.Feature.INCLUDE_SOURCE_IN_LOCATION),
    USE_FAST_DOUBLE_PARSER(JsonParser.Feature.USE_FAST_DOUBLE_PARSER),
    USE_FAST_BIG_NUMBER_PARSER(JsonParser.Feature.USE_FAST_BIG_NUMBER_PARSER);

    private final boolean _defaultState;
    private final int _mask;
    private final JsonParser.Feature _mappedFeature;

    StreamReadFeature(JsonParser.Feature mapTo) {
        this._mappedFeature = mapTo;
        this._mask = mapTo.getMask();
        this._defaultState = mapTo.enabledByDefault();
    }

    public static int collectDefaults() {
        int flags = 0;
        for (StreamReadFeature f : values()) {
            if (f.enabledByDefault()) {
                flags |= f.getMask();
            }
        }
        return flags;
    }

    @Override // com.fasterxml.jackson.core.util.JacksonFeature
    public boolean enabledByDefault() {
        return this._defaultState;
    }

    @Override // com.fasterxml.jackson.core.util.JacksonFeature
    public boolean enabledIn(int flags) {
        return (flags & this._mask) != 0;
    }

    @Override // com.fasterxml.jackson.core.util.JacksonFeature
    public int getMask() {
        return this._mask;
    }

    public JsonParser.Feature mappedFeature() {
        return this._mappedFeature;
    }
}
