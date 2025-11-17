package com.fasterxml.jackson.databind.cfg;

import java.io.Serializable;

/* loaded from: server.jar:BOOT-INF/lib/jackson-databind-2.15.3.jar:com/fasterxml/jackson/databind/cfg/MutableCoercionConfig.class */
public class MutableCoercionConfig extends CoercionConfig implements Serializable {
    private static final long serialVersionUID = 1;

    public MutableCoercionConfig() {
    }

    protected MutableCoercionConfig(MutableCoercionConfig src) {
        super(src);
    }

    public MutableCoercionConfig copy() {
        return new MutableCoercionConfig(this);
    }

    public MutableCoercionConfig setCoercion(CoercionInputShape shape, CoercionAction action) {
        this._coercionsByShape[shape.ordinal()] = action;
        return this;
    }

    public MutableCoercionConfig setAcceptBlankAsEmpty(Boolean state) {
        this._acceptBlankAsEmpty = state;
        return this;
    }
}
