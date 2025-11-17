package com.fasterxml.jackson.databind.cfg;

import java.io.Serializable;
import java.util.Arrays;

/* loaded from: server.jar:BOOT-INF/lib/jackson-databind-2.15.3.jar:com/fasterxml/jackson/databind/cfg/CoercionConfig.class */
public class CoercionConfig implements Serializable {
    private static final long serialVersionUID = 1;
    private static final int INPUT_SHAPE_COUNT = CoercionInputShape.values().length;
    protected Boolean _acceptBlankAsEmpty;
    protected final CoercionAction[] _coercionsByShape;

    public CoercionConfig() {
        this._coercionsByShape = new CoercionAction[INPUT_SHAPE_COUNT];
        this._acceptBlankAsEmpty = null;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public CoercionConfig(CoercionConfig src) {
        this._acceptBlankAsEmpty = src._acceptBlankAsEmpty;
        this._coercionsByShape = (CoercionAction[]) Arrays.copyOf(src._coercionsByShape, src._coercionsByShape.length);
    }

    public CoercionAction findAction(CoercionInputShape shape) {
        return this._coercionsByShape[shape.ordinal()];
    }

    public Boolean getAcceptBlankAsEmpty() {
        return this._acceptBlankAsEmpty;
    }
}
