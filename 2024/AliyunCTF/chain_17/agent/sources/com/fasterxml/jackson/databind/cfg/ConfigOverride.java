package com.fasterxml.jackson.databind.cfg;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonSetter;

/* loaded from: agent.jar:BOOT-INF/lib/jackson-databind-2.15.3.jar:com/fasterxml/jackson/databind/cfg/ConfigOverride.class */
public abstract class ConfigOverride {
    protected JsonFormat.Value _format;
    protected JsonInclude.Value _include;
    protected JsonInclude.Value _includeAsProperty;
    protected JsonIgnoreProperties.Value _ignorals;
    protected JsonSetter.Value _setterInfo;
    protected JsonAutoDetect.Value _visibility;
    protected Boolean _isIgnoredType;
    protected Boolean _mergeable;

    /* JADX INFO: Access modifiers changed from: protected */
    public ConfigOverride() {
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public ConfigOverride(ConfigOverride src) {
        this._format = src._format;
        this._include = src._include;
        this._includeAsProperty = src._includeAsProperty;
        this._ignorals = src._ignorals;
        this._setterInfo = src._setterInfo;
        this._visibility = src._visibility;
        this._isIgnoredType = src._isIgnoredType;
        this._mergeable = src._mergeable;
    }

    public static ConfigOverride empty() {
        return Empty.INSTANCE;
    }

    public JsonFormat.Value getFormat() {
        return this._format;
    }

    public JsonInclude.Value getInclude() {
        return this._include;
    }

    public JsonInclude.Value getIncludeAsProperty() {
        return this._includeAsProperty;
    }

    public JsonIgnoreProperties.Value getIgnorals() {
        return this._ignorals;
    }

    public Boolean getIsIgnoredType() {
        return this._isIgnoredType;
    }

    public JsonSetter.Value getSetterInfo() {
        return this._setterInfo;
    }

    public JsonAutoDetect.Value getVisibility() {
        return this._visibility;
    }

    public Boolean getMergeable() {
        return this._mergeable;
    }

    /* loaded from: agent.jar:BOOT-INF/lib/jackson-databind-2.15.3.jar:com/fasterxml/jackson/databind/cfg/ConfigOverride$Empty.class */
    static final class Empty extends ConfigOverride {
        static final Empty INSTANCE = new Empty();

        private Empty() {
        }
    }
}
