package com.fasterxml.jackson.databind.cfg;

import com.fasterxml.jackson.core.FormatFeature;
import com.fasterxml.jackson.core.util.VersionUtil;
import java.io.Serializable;

/* loaded from: server.jar:BOOT-INF/lib/jackson-databind-2.15.3.jar:com/fasterxml/jackson/databind/cfg/DatatypeFeatures.class */
public class DatatypeFeatures implements Serializable {
    private static final long serialVersionUID = 1;
    protected static final int FEATURE_INDEX_ENUM = 0;
    protected static final int FEATURE_INDEX_JSON_NODE = 1;
    private final int _enabledFor1;
    private final int _enabledFor2;
    private final int _explicitFor1;
    private final int _explicitFor2;

    protected DatatypeFeatures(int enabledFor1, int explicitFor1, int enabledFor2, int explicitFor2) {
        this._enabledFor1 = enabledFor1;
        this._explicitFor1 = explicitFor1;
        this._enabledFor2 = enabledFor2;
        this._explicitFor2 = explicitFor2;
    }

    public static DatatypeFeatures defaultFeatures() {
        return DefaultHolder.getDefault();
    }

    private DatatypeFeatures _with(int enabledFor1, int explicitFor1, int enabledFor2, int explicitFor2) {
        if (this._enabledFor1 == enabledFor1 && this._explicitFor1 == explicitFor1 && this._enabledFor2 == enabledFor2 && this._explicitFor2 == explicitFor2) {
            return this;
        }
        return new DatatypeFeatures(enabledFor1, explicitFor1, enabledFor2, explicitFor2);
    }

    public DatatypeFeatures with(DatatypeFeature f) {
        int mask = f.getMask();
        switch (f.featureIndex()) {
            case 0:
                return _with(this._enabledFor1 | mask, this._explicitFor1 | mask, this._enabledFor2, this._explicitFor2);
            case 1:
                return _with(this._enabledFor1, this._explicitFor1, this._enabledFor2 | mask, this._explicitFor2 | mask);
            default:
                VersionUtil.throwInternal();
                return this;
        }
    }

    public DatatypeFeatures withFeatures(DatatypeFeature... features) {
        int mask = _calcMask(features);
        if (mask == 0) {
            return this;
        }
        switch (features[0].featureIndex()) {
            case 0:
                return _with(this._enabledFor1 | mask, this._explicitFor1 | mask, this._enabledFor2, this._explicitFor2);
            case 1:
                return _with(this._enabledFor1, this._explicitFor1, this._enabledFor2 | mask, this._explicitFor2 | mask);
            default:
                VersionUtil.throwInternal();
                return this;
        }
    }

    public DatatypeFeatures without(DatatypeFeature f) {
        int mask = f.getMask();
        switch (f.featureIndex()) {
            case 0:
                return _with(this._enabledFor1 & (mask ^ (-1)), this._explicitFor1 | mask, this._enabledFor2, this._explicitFor2);
            case 1:
                return _with(this._enabledFor1, this._explicitFor1, this._enabledFor2 & (mask ^ (-1)), this._explicitFor2 | mask);
            default:
                VersionUtil.throwInternal();
                return this;
        }
    }

    public DatatypeFeatures withoutFeatures(DatatypeFeature... features) {
        int mask = _calcMask(features);
        if (mask == 0) {
            return this;
        }
        switch (features[0].featureIndex()) {
            case 0:
                return _with(this._enabledFor1 & (mask ^ (-1)), this._explicitFor1 | mask, this._enabledFor2, this._explicitFor2);
            case 1:
                return _with(this._enabledFor1, this._explicitFor1, this._enabledFor2 & (mask ^ (-1)), this._explicitFor2 | mask);
            default:
                VersionUtil.throwInternal();
                return this;
        }
    }

    private static final int _calcMask(DatatypeFeature... features) {
        int mask = 0;
        for (DatatypeFeature f : features) {
            mask |= f.getMask();
        }
        return mask;
    }

    public boolean isEnabled(DatatypeFeature f) {
        switch (f.featureIndex()) {
            case 0:
                return f.enabledIn(this._enabledFor1);
            case 1:
                return f.enabledIn(this._enabledFor2);
            default:
                VersionUtil.throwInternal();
                return false;
        }
    }

    public boolean isExplicitlySet(DatatypeFeature f) {
        switch (f.featureIndex()) {
            case 0:
                return f.enabledIn(this._explicitFor1);
            case 1:
                return f.enabledIn(this._explicitFor2);
            default:
                VersionUtil.throwInternal();
                return false;
        }
    }

    public boolean isExplicitlyEnabled(DatatypeFeature f) {
        switch (f.featureIndex()) {
            case 0:
                return f.enabledIn(this._explicitFor1 & this._enabledFor1);
            case 1:
                return f.enabledIn(this._explicitFor2 & this._enabledFor2);
            default:
                VersionUtil.throwInternal();
                return false;
        }
    }

    public boolean isExplicitlyDisabled(DatatypeFeature f) {
        switch (f.featureIndex()) {
            case 0:
                return f.enabledIn(this._explicitFor1 & (this._enabledFor1 ^ (-1)));
            case 1:
                return f.enabledIn(this._explicitFor2 & (this._enabledFor2 ^ (-1)));
            default:
                VersionUtil.throwInternal();
                return false;
        }
    }

    public Boolean getExplicitState(DatatypeFeature f) {
        switch (f.featureIndex()) {
            case 0:
                if (f.enabledIn(this._explicitFor1)) {
                    return Boolean.valueOf(f.enabledIn(this._enabledFor1));
                }
                return null;
            case 1:
                if (f.enabledIn(this._explicitFor2)) {
                    return Boolean.valueOf(f.enabledIn(this._enabledFor2));
                }
                return null;
            default:
                VersionUtil.throwInternal();
                return null;
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: server.jar:BOOT-INF/lib/jackson-databind-2.15.3.jar:com/fasterxml/jackson/databind/cfg/DatatypeFeatures$DefaultHolder.class */
    public static class DefaultHolder {
        private static final DatatypeFeatures DEFAULT_FEATURES = new DatatypeFeatures(collectDefaults(EnumFeature.values()), 0, collectDefaults(JsonNodeFeature.values()), 0);

        private DefaultHolder() {
        }

        /* JADX WARN: Incorrect types in method signature: <F:Ljava/lang/Enum<TF;>;:Lcom/fasterxml/jackson/core/util/JacksonFeature;>([TF;)I */
        /* JADX WARN: Multi-variable type inference failed */
        private static int collectDefaults(Enum[] enumArr) {
            int flags = 0;
            for (FormatFeature formatFeature : enumArr) {
                if (formatFeature.enabledByDefault()) {
                    flags |= formatFeature.getMask();
                }
            }
            return flags;
        }

        public static DatatypeFeatures getDefault() {
            return DEFAULT_FEATURES;
        }
    }
}
