package com.fasterxml.jackson.databind.introspect;

import com.fasterxml.jackson.annotation.ObjectIdGenerator;
import com.fasterxml.jackson.annotation.ObjectIdResolver;
import com.fasterxml.jackson.annotation.SimpleObjectIdResolver;
import com.fasterxml.jackson.databind.PropertyName;
import com.fasterxml.jackson.databind.util.ClassUtil;

/* loaded from: server.jar:BOOT-INF/lib/jackson-databind-2.15.3.jar:com/fasterxml/jackson/databind/introspect/ObjectIdInfo.class */
public class ObjectIdInfo {
    protected final PropertyName _propertyName;
    protected final Class<? extends ObjectIdGenerator<?>> _generator;
    protected final Class<? extends ObjectIdResolver> _resolver;
    protected final Class<?> _scope;
    protected final boolean _alwaysAsId;
    private static final ObjectIdInfo EMPTY = new ObjectIdInfo(PropertyName.NO_NAME, Object.class, null, false, null);

    public ObjectIdInfo(PropertyName name, Class<?> scope, Class<? extends ObjectIdGenerator<?>> gen, Class<? extends ObjectIdResolver> resolver) {
        this(name, scope, gen, false, resolver);
    }

    protected ObjectIdInfo(PropertyName prop, Class<?> scope, Class<? extends ObjectIdGenerator<?>> gen, boolean alwaysAsId) {
        this(prop, scope, gen, alwaysAsId, SimpleObjectIdResolver.class);
    }

    /* JADX WARN: Incorrect type for immutable var: ssa=java.lang.Class<? extends com.fasterxml.jackson.annotation.ObjectIdResolver>, code=java.lang.Class, for r8v0, types: [java.lang.Class<? extends com.fasterxml.jackson.annotation.ObjectIdResolver>] */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct code enable 'Show inconsistent code' option in preferences
    */
    protected ObjectIdInfo(com.fasterxml.jackson.databind.PropertyName r4, java.lang.Class<?> r5, java.lang.Class<? extends com.fasterxml.jackson.annotation.ObjectIdGenerator<?>> r6, boolean r7, java.lang.Class r8) {
        /*
            r3 = this;
            r0 = r3
            r0.<init>()
            r0 = r3
            r1 = r4
            r0._propertyName = r1
            r0 = r3
            r1 = r5
            r0._scope = r1
            r0 = r3
            r1 = r6
            r0._generator = r1
            r0 = r3
            r1 = r7
            r0._alwaysAsId = r1
            r0 = r8
            if (r0 != 0) goto L22
            java.lang.Class<com.fasterxml.jackson.annotation.SimpleObjectIdResolver> r0 = com.fasterxml.jackson.annotation.SimpleObjectIdResolver.class
            r8 = r0
        L22:
            r0 = r3
            r1 = r8
            r0._resolver = r1
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: com.fasterxml.jackson.databind.introspect.ObjectIdInfo.<init>(com.fasterxml.jackson.databind.PropertyName, java.lang.Class, java.lang.Class, boolean, java.lang.Class):void");
    }

    public static ObjectIdInfo empty() {
        return EMPTY;
    }

    public ObjectIdInfo withAlwaysAsId(boolean state) {
        if (this._alwaysAsId == state) {
            return this;
        }
        return new ObjectIdInfo(this._propertyName, this._scope, this._generator, state, this._resolver);
    }

    public PropertyName getPropertyName() {
        return this._propertyName;
    }

    public Class<?> getScope() {
        return this._scope;
    }

    public Class<? extends ObjectIdGenerator<?>> getGeneratorType() {
        return this._generator;
    }

    public Class<? extends ObjectIdResolver> getResolverType() {
        return this._resolver;
    }

    public boolean getAlwaysAsId() {
        return this._alwaysAsId;
    }

    public String toString() {
        return "ObjectIdInfo: propName=" + this._propertyName + ", scope=" + ClassUtil.nameOf(this._scope) + ", generatorType=" + ClassUtil.nameOf(this._generator) + ", alwaysAsId=" + this._alwaysAsId;
    }
}
