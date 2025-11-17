package com.fasterxml.jackson.databind.cfg;

import com.fasterxml.jackson.databind.util.ClassUtil;
import java.io.Serializable;

/* loaded from: server.jar:BOOT-INF/lib/jackson-databind-2.15.3.jar:com/fasterxml/jackson/databind/cfg/ConstructorDetector.class */
public final class ConstructorDetector implements Serializable {
    private static final long serialVersionUID = 1;
    public static final ConstructorDetector DEFAULT = new ConstructorDetector(SingleArgConstructor.HEURISTIC);
    public static final ConstructorDetector USE_PROPERTIES_BASED = new ConstructorDetector(SingleArgConstructor.PROPERTIES);
    public static final ConstructorDetector USE_DELEGATING = new ConstructorDetector(SingleArgConstructor.DELEGATING);
    public static final ConstructorDetector EXPLICIT_ONLY = new ConstructorDetector(SingleArgConstructor.REQUIRE_MODE);
    protected final SingleArgConstructor _singleArgMode;
    protected final boolean _requireCtorAnnotation;
    protected final boolean _allowJDKTypeCtors;

    /* loaded from: server.jar:BOOT-INF/lib/jackson-databind-2.15.3.jar:com/fasterxml/jackson/databind/cfg/ConstructorDetector$SingleArgConstructor.class */
    public enum SingleArgConstructor {
        DELEGATING,
        PROPERTIES,
        HEURISTIC,
        REQUIRE_MODE
    }

    protected ConstructorDetector(SingleArgConstructor singleArgMode, boolean requireCtorAnnotation, boolean allowJDKTypeCtors) {
        this._singleArgMode = singleArgMode;
        this._requireCtorAnnotation = requireCtorAnnotation;
        this._allowJDKTypeCtors = allowJDKTypeCtors;
    }

    protected ConstructorDetector(SingleArgConstructor singleArgMode) {
        this(singleArgMode, false, false);
    }

    public ConstructorDetector withSingleArgMode(SingleArgConstructor singleArgMode) {
        return new ConstructorDetector(singleArgMode, this._requireCtorAnnotation, this._allowJDKTypeCtors);
    }

    public ConstructorDetector withRequireAnnotation(boolean state) {
        return new ConstructorDetector(this._singleArgMode, state, this._allowJDKTypeCtors);
    }

    public ConstructorDetector withAllowJDKTypeConstructors(boolean state) {
        return new ConstructorDetector(this._singleArgMode, this._requireCtorAnnotation, state);
    }

    public SingleArgConstructor singleArgMode() {
        return this._singleArgMode;
    }

    public boolean requireCtorAnnotation() {
        return this._requireCtorAnnotation;
    }

    public boolean allowJDKTypeConstructors() {
        return this._allowJDKTypeCtors;
    }

    public boolean singleArgCreatorDefaultsToDelegating() {
        return this._singleArgMode == SingleArgConstructor.DELEGATING;
    }

    public boolean singleArgCreatorDefaultsToProperties() {
        return this._singleArgMode == SingleArgConstructor.PROPERTIES;
    }

    public boolean shouldIntrospectorImplicitConstructors(Class<?> rawType) {
        if (this._requireCtorAnnotation) {
            return false;
        }
        if (!this._allowJDKTypeCtors && ClassUtil.isJDKClass(rawType) && !Throwable.class.isAssignableFrom(rawType)) {
            return false;
        }
        return true;
    }
}
