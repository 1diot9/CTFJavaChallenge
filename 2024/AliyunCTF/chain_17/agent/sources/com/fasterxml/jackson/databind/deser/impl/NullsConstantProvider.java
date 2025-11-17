package com.fasterxml.jackson.databind.deser.impl;

import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.deser.NullValueProvider;
import com.fasterxml.jackson.databind.util.AccessPattern;
import java.io.Serializable;

/* loaded from: agent.jar:BOOT-INF/lib/jackson-databind-2.15.3.jar:com/fasterxml/jackson/databind/deser/impl/NullsConstantProvider.class */
public class NullsConstantProvider implements NullValueProvider, Serializable {
    private static final long serialVersionUID = 1;
    private static final NullsConstantProvider SKIPPER = new NullsConstantProvider(null);
    private static final NullsConstantProvider NULLER = new NullsConstantProvider(null);
    protected final Object _nullValue;
    protected final AccessPattern _access;

    protected NullsConstantProvider(Object nvl) {
        this._nullValue = nvl;
        this._access = this._nullValue == null ? AccessPattern.ALWAYS_NULL : AccessPattern.CONSTANT;
    }

    public static NullsConstantProvider skipper() {
        return SKIPPER;
    }

    public static NullsConstantProvider nuller() {
        return NULLER;
    }

    public static NullsConstantProvider forValue(Object nvl) {
        if (nvl == null) {
            return NULLER;
        }
        return new NullsConstantProvider(nvl);
    }

    public static boolean isSkipper(NullValueProvider p) {
        return p == SKIPPER;
    }

    public static boolean isNuller(NullValueProvider p) {
        return p == NULLER;
    }

    @Override // com.fasterxml.jackson.databind.deser.NullValueProvider
    public AccessPattern getNullAccessPattern() {
        return this._access;
    }

    @Override // com.fasterxml.jackson.databind.deser.NullValueProvider
    public Object getNullValue(DeserializationContext ctxt) {
        return this._nullValue;
    }
}
