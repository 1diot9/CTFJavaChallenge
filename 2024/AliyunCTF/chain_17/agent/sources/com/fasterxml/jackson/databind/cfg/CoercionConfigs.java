package com.fasterxml.jackson.databind.cfg;

import com.fasterxml.jackson.databind.DeserializationConfig;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.type.LogicalType;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

/* loaded from: agent.jar:BOOT-INF/lib/jackson-databind-2.15.3.jar:com/fasterxml/jackson/databind/cfg/CoercionConfigs.class */
public class CoercionConfigs implements Serializable {
    private static final long serialVersionUID = 1;
    private static final int TARGET_TYPE_COUNT = LogicalType.values().length;
    protected CoercionAction _defaultAction;
    protected final MutableCoercionConfig _defaultCoercions;
    protected MutableCoercionConfig[] _perTypeCoercions;
    protected Map<Class<?>, MutableCoercionConfig> _perClassCoercions;

    public CoercionConfigs() {
        this(CoercionAction.TryConvert, new MutableCoercionConfig(), null, null);
    }

    protected CoercionConfigs(CoercionAction defaultAction, MutableCoercionConfig defaultCoercions, MutableCoercionConfig[] perTypeCoercions, Map<Class<?>, MutableCoercionConfig> perClassCoercions) {
        this._defaultCoercions = defaultCoercions;
        this._defaultAction = defaultAction;
        this._perTypeCoercions = perTypeCoercions;
        this._perClassCoercions = perClassCoercions;
    }

    public CoercionConfigs copy() {
        MutableCoercionConfig[] newPerType;
        Map<Class<?>, MutableCoercionConfig> newPerClass;
        if (this._perTypeCoercions == null) {
            newPerType = null;
        } else {
            int size = this._perTypeCoercions.length;
            newPerType = new MutableCoercionConfig[size];
            for (int i = 0; i < size; i++) {
                newPerType[i] = _copy(this._perTypeCoercions[i]);
            }
        }
        if (this._perClassCoercions == null) {
            newPerClass = null;
        } else {
            newPerClass = new HashMap<>();
            for (Map.Entry<Class<?>, MutableCoercionConfig> entry : this._perClassCoercions.entrySet()) {
                newPerClass.put(entry.getKey(), entry.getValue().copy());
            }
        }
        return new CoercionConfigs(this._defaultAction, this._defaultCoercions.copy(), newPerType, newPerClass);
    }

    private static MutableCoercionConfig _copy(MutableCoercionConfig src) {
        if (src == null) {
            return null;
        }
        return src.copy();
    }

    public MutableCoercionConfig defaultCoercions() {
        return this._defaultCoercions;
    }

    public MutableCoercionConfig findOrCreateCoercion(LogicalType type) {
        if (this._perTypeCoercions == null) {
            this._perTypeCoercions = new MutableCoercionConfig[TARGET_TYPE_COUNT];
        }
        MutableCoercionConfig config = this._perTypeCoercions[type.ordinal()];
        if (config == null) {
            MutableCoercionConfig[] mutableCoercionConfigArr = this._perTypeCoercions;
            int ordinal = type.ordinal();
            MutableCoercionConfig mutableCoercionConfig = new MutableCoercionConfig();
            config = mutableCoercionConfig;
            mutableCoercionConfigArr[ordinal] = mutableCoercionConfig;
        }
        return config;
    }

    public MutableCoercionConfig findOrCreateCoercion(Class<?> type) {
        if (this._perClassCoercions == null) {
            this._perClassCoercions = new HashMap();
        }
        MutableCoercionConfig config = this._perClassCoercions.get(type);
        if (config == null) {
            config = new MutableCoercionConfig();
            this._perClassCoercions.put(type, config);
        }
        return config;
    }

    public CoercionAction findCoercion(DeserializationConfig config, LogicalType targetType, Class<?> targetClass, CoercionInputShape inputShape) {
        MutableCoercionConfig cc;
        CoercionAction act;
        MutableCoercionConfig cc2;
        CoercionAction act2;
        if (this._perClassCoercions != null && targetClass != null && (cc2 = this._perClassCoercions.get(targetClass)) != null && (act2 = cc2.findAction(inputShape)) != null) {
            return act2;
        }
        if (this._perTypeCoercions != null && targetType != null && (cc = this._perTypeCoercions[targetType.ordinal()]) != null && (act = cc.findAction(inputShape)) != null) {
            return act;
        }
        CoercionAction act3 = this._defaultCoercions.findAction(inputShape);
        if (act3 != null) {
            return act3;
        }
        switch (inputShape) {
            case EmptyArray:
                return config.isEnabled(DeserializationFeature.ACCEPT_EMPTY_ARRAY_AS_NULL_OBJECT) ? CoercionAction.AsNull : CoercionAction.Fail;
            case Float:
                if (targetType == LogicalType.Integer) {
                    return config.isEnabled(DeserializationFeature.ACCEPT_FLOAT_AS_INT) ? CoercionAction.TryConvert : CoercionAction.Fail;
                }
                break;
            case Integer:
                if (targetType == LogicalType.Enum && config.isEnabled(DeserializationFeature.FAIL_ON_NUMBERS_FOR_ENUMS)) {
                    return CoercionAction.Fail;
                }
                break;
        }
        boolean baseScalar = _isScalarType(targetType);
        if (baseScalar && !config.isEnabled(MapperFeature.ALLOW_COERCION_OF_SCALARS) && (targetType != LogicalType.Float || inputShape != CoercionInputShape.Integer)) {
            return CoercionAction.Fail;
        }
        if (inputShape == CoercionInputShape.EmptyString) {
            if (baseScalar || config.isEnabled(DeserializationFeature.ACCEPT_EMPTY_STRING_AS_NULL_OBJECT)) {
                return CoercionAction.AsNull;
            }
            if (targetType == LogicalType.OtherScalar) {
                return CoercionAction.TryConvert;
            }
            return CoercionAction.Fail;
        }
        return this._defaultAction;
    }

    public CoercionAction findCoercionFromBlankString(DeserializationConfig config, LogicalType targetType, Class<?> targetClass, CoercionAction actionIfBlankNotAllowed) {
        MutableCoercionConfig cc;
        MutableCoercionConfig cc2;
        Boolean acceptBlankAsEmpty = null;
        CoercionAction action = null;
        if (this._perClassCoercions != null && targetClass != null && (cc2 = this._perClassCoercions.get(targetClass)) != null) {
            acceptBlankAsEmpty = cc2.getAcceptBlankAsEmpty();
            action = cc2.findAction(CoercionInputShape.EmptyString);
        }
        if (this._perTypeCoercions != null && targetType != null && (cc = this._perTypeCoercions[targetType.ordinal()]) != null) {
            if (acceptBlankAsEmpty == null) {
                acceptBlankAsEmpty = cc.getAcceptBlankAsEmpty();
            }
            if (action == null) {
                action = cc.findAction(CoercionInputShape.EmptyString);
            }
        }
        if (acceptBlankAsEmpty == null) {
            acceptBlankAsEmpty = this._defaultCoercions.getAcceptBlankAsEmpty();
        }
        if (action == null) {
            action = this._defaultCoercions.findAction(CoercionInputShape.EmptyString);
        }
        if (Boolean.FALSE.equals(acceptBlankAsEmpty)) {
            return actionIfBlankNotAllowed;
        }
        if (action != null) {
            return action;
        }
        if (_isScalarType(targetType)) {
            return CoercionAction.AsNull;
        }
        if (config.isEnabled(DeserializationFeature.ACCEPT_EMPTY_STRING_AS_NULL_OBJECT)) {
            return CoercionAction.AsNull;
        }
        return actionIfBlankNotAllowed;
    }

    protected boolean _isScalarType(LogicalType targetType) {
        return targetType == LogicalType.Float || targetType == LogicalType.Integer || targetType == LogicalType.Boolean || targetType == LogicalType.DateTime;
    }
}
