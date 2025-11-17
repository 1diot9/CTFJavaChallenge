package com.fasterxml.jackson.databind.cfg;

/* loaded from: server.jar:BOOT-INF/lib/jackson-databind-2.15.3.jar:com/fasterxml/jackson/databind/cfg/EnumFeature.class */
public enum EnumFeature implements DatatypeFeature {
    READ_ENUM_KEYS_USING_INDEX(false),
    WRITE_ENUMS_TO_LOWERCASE(false);

    private static final int FEATURE_INDEX = 0;
    private final boolean _enabledByDefault;
    private final int _mask = 1 << ordinal();

    EnumFeature(boolean enabledByDefault) {
        this._enabledByDefault = enabledByDefault;
    }

    @Override // com.fasterxml.jackson.core.util.JacksonFeature
    public boolean enabledByDefault() {
        return this._enabledByDefault;
    }

    @Override // com.fasterxml.jackson.core.util.JacksonFeature
    public boolean enabledIn(int flags) {
        return (flags & this._mask) != 0;
    }

    @Override // com.fasterxml.jackson.core.util.JacksonFeature
    public int getMask() {
        return this._mask;
    }

    @Override // com.fasterxml.jackson.databind.cfg.DatatypeFeature
    public int featureIndex() {
        return 0;
    }
}
