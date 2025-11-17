package com.fasterxml.jackson.databind.cfg;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonSetter;
import java.io.Serializable;

/* loaded from: server.jar:BOOT-INF/lib/jackson-databind-2.15.3.jar:com/fasterxml/jackson/databind/cfg/MutableConfigOverride.class */
public class MutableConfigOverride extends ConfigOverride implements Serializable {
    private static final long serialVersionUID = 1;

    public MutableConfigOverride() {
    }

    protected MutableConfigOverride(MutableConfigOverride src) {
        super(src);
    }

    public MutableConfigOverride copy() {
        return new MutableConfigOverride(this);
    }

    public MutableConfigOverride setFormat(JsonFormat.Value v) {
        this._format = v;
        return this;
    }

    public MutableConfigOverride setInclude(JsonInclude.Value v) {
        this._include = v;
        return this;
    }

    public MutableConfigOverride setIncludeAsProperty(JsonInclude.Value v) {
        this._includeAsProperty = v;
        return this;
    }

    public MutableConfigOverride setIgnorals(JsonIgnoreProperties.Value v) {
        this._ignorals = v;
        return this;
    }

    public MutableConfigOverride setIsIgnoredType(Boolean v) {
        this._isIgnoredType = v;
        return this;
    }

    public MutableConfigOverride setSetterInfo(JsonSetter.Value v) {
        this._setterInfo = v;
        return this;
    }

    public MutableConfigOverride setVisibility(JsonAutoDetect.Value v) {
        this._visibility = v;
        return this;
    }

    public MutableConfigOverride setMergeable(Boolean v) {
        this._mergeable = v;
        return this;
    }
}
