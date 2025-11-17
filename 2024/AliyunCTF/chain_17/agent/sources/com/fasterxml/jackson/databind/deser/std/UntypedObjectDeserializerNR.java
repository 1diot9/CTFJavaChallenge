package com.fasterxml.jackson.databind.deser.std;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.core.StreamReadCapability;
import com.fasterxml.jackson.databind.DeserializationConfig;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.annotation.JacksonStdImpl;
import com.fasterxml.jackson.databind.jsontype.TypeDeserializer;
import com.fasterxml.jackson.databind.type.LogicalType;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/* JADX INFO: Access modifiers changed from: package-private */
@JacksonStdImpl
/* loaded from: agent.jar:BOOT-INF/lib/jackson-databind-2.15.3.jar:com/fasterxml/jackson/databind/deser/std/UntypedObjectDeserializerNR.class */
public final class UntypedObjectDeserializerNR extends StdDeserializer<Object> {
    private static final long serialVersionUID = 1;
    protected static final Object[] NO_OBJECTS = new Object[0];
    public static final UntypedObjectDeserializerNR std = new UntypedObjectDeserializerNR();
    protected final boolean _nonMerging;

    public UntypedObjectDeserializerNR() {
        this(false);
    }

    protected UntypedObjectDeserializerNR(boolean nonMerging) {
        super((Class<?>) Object.class);
        this._nonMerging = nonMerging;
    }

    public static UntypedObjectDeserializerNR instance(boolean nonMerging) {
        if (nonMerging) {
            return new UntypedObjectDeserializerNR(true);
        }
        return std;
    }

    @Override // com.fasterxml.jackson.databind.JsonDeserializer
    public LogicalType logicalType() {
        return LogicalType.Untyped;
    }

    @Override // com.fasterxml.jackson.databind.JsonDeserializer
    public Boolean supportsUpdate(DeserializationConfig config) {
        if (this._nonMerging) {
            return Boolean.FALSE;
        }
        return null;
    }

    @Override // com.fasterxml.jackson.databind.JsonDeserializer
    public Object deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
        switch (p.currentTokenId()) {
            case 1:
                return _deserializeNR(p, ctxt, Scope.rootObjectScope(ctxt.isEnabled(StreamReadCapability.DUPLICATE_PROPERTIES)));
            case 2:
                return Scope.emptyMap();
            case 3:
                return _deserializeNR(p, ctxt, Scope.rootArrayScope());
            case 4:
            default:
                return ctxt.handleUnexpectedToken(getValueType(ctxt), p);
            case 5:
                return _deserializeObjectAtName(p, ctxt);
            case 6:
                return p.getText();
            case 7:
                if (ctxt.hasSomeOfFeatures(F_MASK_INT_COERCIONS)) {
                    return _coerceIntegral(p, ctxt);
                }
                return p.getNumberValue();
            case 8:
                if (ctxt.isEnabled(DeserializationFeature.USE_BIG_DECIMAL_FOR_FLOATS)) {
                    return p.getDecimalValue();
                }
                return p.getNumberValue();
            case 9:
                return Boolean.TRUE;
            case 10:
                return Boolean.FALSE;
            case 11:
                return null;
            case 12:
                return p.getEmbeddedObject();
        }
    }

    @Override // com.fasterxml.jackson.databind.deser.std.StdDeserializer, com.fasterxml.jackson.databind.JsonDeserializer
    public Object deserializeWithType(JsonParser p, DeserializationContext ctxt, TypeDeserializer typeDeserializer) throws IOException {
        switch (p.currentTokenId()) {
            case 1:
            case 3:
            case 5:
                return typeDeserializer.deserializeTypedFromAny(p, ctxt);
            case 2:
            case 4:
            default:
                return _deserializeAnyScalar(p, ctxt, p.currentTokenId());
        }
    }

    /* JADX WARN: Can't fix incorrect switch cases order, some code will duplicate */
    /* JADX WARN: Failed to find 'out' block for switch in B:7:0x0012. Please report as an issue. */
    /* JADX WARN: Removed duplicated region for block: B:16:0x004d  */
    @Override // com.fasterxml.jackson.databind.JsonDeserializer
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct code enable 'Show inconsistent code' option in preferences
    */
    public java.lang.Object deserialize(com.fasterxml.jackson.core.JsonParser r6, com.fasterxml.jackson.databind.DeserializationContext r7, java.lang.Object r8) throws java.io.IOException {
        /*
            r5 = this;
            r0 = r5
            boolean r0 = r0._nonMerging
            if (r0 == 0) goto Le
            r0 = r5
            r1 = r6
            r2 = r7
            java.lang.Object r0 = r0.deserialize(r1, r2)
            return r0
        Le:
            r0 = r6
            int r0 = r0.currentTokenId()
            switch(r0) {
                case 1: goto L36;
                case 2: goto L34;
                case 3: goto La2;
                case 4: goto L34;
                case 5: goto L46;
                default: goto Ld9;
            }
        L34:
            r0 = r8
            return r0
        L36:
            r0 = r6
            com.fasterxml.jackson.core.JsonToken r0 = r0.nextToken()
            r9 = r0
            r0 = r9
            com.fasterxml.jackson.core.JsonToken r1 = com.fasterxml.jackson.core.JsonToken.END_OBJECT
            if (r0 != r1) goto L46
            r0 = r8
            return r0
        L46:
            r0 = r8
            boolean r0 = r0 instanceof java.util.Map
            if (r0 == 0) goto Ld9
            r0 = r8
            java.util.Map r0 = (java.util.Map) r0
            r9 = r0
            r0 = r6
            java.lang.String r0 = r0.currentName()
            r10 = r0
        L59:
            r0 = r6
            com.fasterxml.jackson.core.JsonToken r0 = r0.nextToken()
            r0 = r9
            r1 = r10
            java.lang.Object r0 = r0.get(r1)
            r11 = r0
            r0 = r11
            if (r0 == 0) goto L7b
            r0 = r5
            r1 = r6
            r2 = r7
            r3 = r11
            java.lang.Object r0 = r0.deserialize(r1, r2, r3)
            r12 = r0
            goto L83
        L7b:
            r0 = r5
            r1 = r6
            r2 = r7
            java.lang.Object r0 = r0.deserialize(r1, r2)
            r12 = r0
        L83:
            r0 = r12
            r1 = r11
            if (r0 == r1) goto L96
            r0 = r9
            r1 = r10
            r2 = r12
            java.lang.Object r0 = r0.put(r1, r2)
        L96:
            r0 = r6
            java.lang.String r0 = r0.nextFieldName()
            r1 = r0
            r10 = r1
            if (r0 != 0) goto L59
            r0 = r8
            return r0
        La2:
            r0 = r6
            com.fasterxml.jackson.core.JsonToken r0 = r0.nextToken()
            r9 = r0
            r0 = r9
            com.fasterxml.jackson.core.JsonToken r1 = com.fasterxml.jackson.core.JsonToken.END_ARRAY
            if (r0 != r1) goto Lb2
            r0 = r8
            return r0
        Lb2:
            r0 = r8
            boolean r0 = r0 instanceof java.util.Collection
            if (r0 == 0) goto Ld9
            r0 = r8
            java.util.Collection r0 = (java.util.Collection) r0
            r9 = r0
        Lbf:
            r0 = r9
            r1 = r5
            r2 = r6
            r3 = r7
            java.lang.Object r1 = r1.deserialize(r2, r3)
            boolean r0 = r0.add(r1)
            r0 = r6
            com.fasterxml.jackson.core.JsonToken r0 = r0.nextToken()
            com.fasterxml.jackson.core.JsonToken r1 = com.fasterxml.jackson.core.JsonToken.END_ARRAY
            if (r0 != r1) goto Lbf
            r0 = r8
            return r0
        Ld9:
            r0 = r5
            r1 = r6
            r2 = r7
            java.lang.Object r0 = r0.deserialize(r1, r2)
            return r0
        */
        throw new UnsupportedOperationException("Method not decompiled: com.fasterxml.jackson.databind.deser.std.UntypedObjectDeserializerNR.deserialize(com.fasterxml.jackson.core.JsonParser, com.fasterxml.jackson.databind.DeserializationContext, java.lang.Object):java.lang.Object");
    }

    private Object _deserializeObjectAtName(JsonParser p, DeserializationContext ctxt) throws IOException {
        Object _deserializeAnyScalar;
        Scope rootObject = Scope.rootObjectScope(ctxt.isEnabled(StreamReadCapability.DUPLICATE_PROPERTIES));
        String currentName = p.currentName();
        while (true) {
            String key = currentName;
            if (key != null) {
                JsonToken t = p.nextToken();
                if (t == null) {
                    t = JsonToken.NOT_AVAILABLE;
                }
                switch (t.id()) {
                    case 1:
                        _deserializeAnyScalar = _deserializeNR(p, ctxt, rootObject.childObject());
                        break;
                    case 2:
                        return rootObject.finishRootObject();
                    case 3:
                        _deserializeAnyScalar = _deserializeNR(p, ctxt, rootObject.childArray());
                        break;
                    default:
                        _deserializeAnyScalar = _deserializeAnyScalar(p, ctxt, t.id());
                        break;
                }
                Object value = _deserializeAnyScalar;
                rootObject.putValue(key, value);
                currentName = p.nextFieldName();
            } else {
                return rootObject.finishRootObject();
            }
        }
    }

    /* JADX WARN: Failed to find 'out' block for switch in B:60:0x003d. Please report as an issue. */
    /* JADX WARN: Failed to find 'out' block for switch in B:9:0x0135. Please report as an issue. */
    private Object _deserializeNR(JsonParser p, DeserializationContext ctxt, Scope rootScope) throws IOException {
        Object value;
        Object embeddedObject;
        boolean intCoercions = ctxt.hasSomeOfFeatures(F_MASK_INT_COERCIONS);
        boolean useJavaArray = ctxt.isEnabled(DeserializationFeature.USE_JAVA_ARRAY_FOR_JSON_ARRAY);
        Scope scope = rootScope;
        while (true) {
            Scope currScope = scope;
            if (currScope.isObject()) {
                String nextFieldName = p.nextFieldName();
                while (true) {
                    String propName = nextFieldName;
                    if (propName != null) {
                        JsonToken t = p.nextToken();
                        if (t == null) {
                            t = JsonToken.NOT_AVAILABLE;
                        }
                        switch (t.id()) {
                            case 1:
                                currScope = currScope.childObject(propName);
                                nextFieldName = p.nextFieldName();
                            case 2:
                            case 4:
                            case 5:
                            default:
                                return ctxt.handleUnexpectedToken(getValueType(ctxt), p);
                            case 3:
                                scope = currScope.childArray(propName);
                                break;
                            case 6:
                                value = p.getText();
                                currScope.putValue(propName, value);
                                nextFieldName = p.nextFieldName();
                            case 7:
                                value = intCoercions ? _coerceIntegral(p, ctxt) : p.getNumberValue();
                                currScope.putValue(propName, value);
                                nextFieldName = p.nextFieldName();
                            case 8:
                                value = ctxt.isEnabled(DeserializationFeature.USE_BIG_DECIMAL_FOR_FLOATS) ? p.getDecimalValue() : p.getNumberValue();
                                currScope.putValue(propName, value);
                                nextFieldName = p.nextFieldName();
                            case 9:
                                value = Boolean.TRUE;
                                currScope.putValue(propName, value);
                                nextFieldName = p.nextFieldName();
                            case 10:
                                value = Boolean.FALSE;
                                currScope.putValue(propName, value);
                                nextFieldName = p.nextFieldName();
                            case 11:
                                value = null;
                                currScope.putValue(propName, value);
                                nextFieldName = p.nextFieldName();
                            case 12:
                                value = p.getEmbeddedObject();
                                currScope.putValue(propName, value);
                                nextFieldName = p.nextFieldName();
                        }
                    } else {
                        if (currScope == rootScope) {
                            return currScope.finishRootObject();
                        }
                        scope = currScope.finishBranchObject();
                    }
                }
            } else {
                while (true) {
                    JsonToken t2 = p.nextToken();
                    if (t2 == null) {
                        t2 = JsonToken.NOT_AVAILABLE;
                    }
                    switch (t2.id()) {
                        case 1:
                            scope = currScope.childObject();
                            break;
                        case 2:
                        case 5:
                        default:
                            return ctxt.handleUnexpectedToken(getValueType(ctxt), p);
                        case 3:
                            scope = currScope.childArray();
                            break;
                        case 4:
                            if (currScope == rootScope) {
                                return currScope.finishRootArray(useJavaArray);
                            }
                            scope = currScope.finishBranchArray(useJavaArray);
                            break;
                        case 6:
                            embeddedObject = p.getText();
                            Object value2 = embeddedObject;
                            currScope.addValue(value2);
                        case 7:
                            embeddedObject = intCoercions ? _coerceIntegral(p, ctxt) : p.getNumberValue();
                            Object value22 = embeddedObject;
                            currScope.addValue(value22);
                        case 8:
                            embeddedObject = ctxt.isEnabled(DeserializationFeature.USE_BIG_DECIMAL_FOR_FLOATS) ? p.getDecimalValue() : p.getNumberValue();
                            Object value222 = embeddedObject;
                            currScope.addValue(value222);
                        case 9:
                            embeddedObject = Boolean.TRUE;
                            Object value2222 = embeddedObject;
                            currScope.addValue(value2222);
                        case 10:
                            embeddedObject = Boolean.FALSE;
                            Object value22222 = embeddedObject;
                            currScope.addValue(value22222);
                        case 11:
                            embeddedObject = null;
                            Object value222222 = embeddedObject;
                            currScope.addValue(value222222);
                        case 12:
                            embeddedObject = p.getEmbeddedObject();
                            Object value2222222 = embeddedObject;
                            currScope.addValue(value2222222);
                    }
                }
            }
        }
    }

    private Object _deserializeAnyScalar(JsonParser p, DeserializationContext ctxt, int tokenType) throws IOException {
        switch (tokenType) {
            case 6:
                return p.getText();
            case 7:
                if (ctxt.isEnabled(DeserializationFeature.USE_BIG_INTEGER_FOR_INTS)) {
                    return p.getBigIntegerValue();
                }
                return p.getNumberValue();
            case 8:
                if (ctxt.isEnabled(DeserializationFeature.USE_BIG_DECIMAL_FOR_FLOATS)) {
                    return p.getDecimalValue();
                }
                return p.getNumberValue();
            case 9:
                return Boolean.TRUE;
            case 10:
                return Boolean.FALSE;
            case 11:
                return null;
            case 12:
                return p.getEmbeddedObject();
            default:
                return ctxt.handleUnexpectedToken(getValueType(ctxt), p);
        }
    }

    protected Object _mapObjectWithDups(JsonParser p, DeserializationContext ctxt, Map<String, Object> result, String initialKey, Object oldValue, Object newValue, String nextKey) throws IOException {
        boolean squashDups = ctxt.isEnabled(StreamReadCapability.DUPLICATE_PROPERTIES);
        if (squashDups) {
            _squashDups(result, initialKey, oldValue, newValue);
        }
        while (nextKey != null) {
            p.nextToken();
            Object newValue2 = deserialize(p, ctxt);
            Object oldValue2 = result.put(nextKey, newValue2);
            if (oldValue2 != null && squashDups) {
                _squashDups(result, nextKey, oldValue2, newValue2);
            }
            nextKey = p.nextFieldName();
        }
        return result;
    }

    private void _squashDups(Map<String, Object> result, String key, Object oldValue, Object newValue) {
        if (oldValue instanceof List) {
            ((List) oldValue).add(newValue);
            result.put(key, oldValue);
        } else {
            ArrayList<Object> l = new ArrayList<>();
            l.add(oldValue);
            l.add(newValue);
            result.put(key, l);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: agent.jar:BOOT-INF/lib/jackson-databind-2.15.3.jar:com/fasterxml/jackson/databind/deser/std/UntypedObjectDeserializerNR$Scope.class */
    public static final class Scope {
        private final Scope _parent;
        private Scope _child;
        private boolean _isObject;
        private boolean _squashDups;
        private String _deferredKey;
        private Map<String, Object> _map;
        private List<Object> _list;

        private Scope(Scope p) {
            this._parent = p;
            this._isObject = false;
            this._squashDups = false;
        }

        private Scope(Scope p, boolean isObject, boolean squashDups) {
            this._parent = p;
            this._isObject = isObject;
            this._squashDups = squashDups;
        }

        public static Scope rootObjectScope(boolean squashDups) {
            return new Scope(null, true, squashDups);
        }

        public static Scope rootArrayScope() {
            return new Scope(null);
        }

        private Scope resetAsArray() {
            this._isObject = false;
            return this;
        }

        private Scope resetAsObject(boolean squashDups) {
            this._isObject = true;
            this._squashDups = squashDups;
            return this;
        }

        public Scope childObject() {
            if (this._child == null) {
                return new Scope(this, true, this._squashDups);
            }
            return this._child.resetAsObject(this._squashDups);
        }

        public Scope childObject(String deferredKey) {
            this._deferredKey = deferredKey;
            if (this._child == null) {
                return new Scope(this, true, this._squashDups);
            }
            return this._child.resetAsObject(this._squashDups);
        }

        public Scope childArray() {
            if (this._child == null) {
                return new Scope(this);
            }
            return this._child.resetAsArray();
        }

        public Scope childArray(String deferredKey) {
            this._deferredKey = deferredKey;
            if (this._child == null) {
                return new Scope(this);
            }
            return this._child.resetAsArray();
        }

        public boolean isObject() {
            return this._isObject;
        }

        public void putValue(String key, Object value) {
            if (this._squashDups) {
                _putValueHandleDups(key, value);
                return;
            }
            if (this._map == null) {
                this._map = new LinkedHashMap();
            }
            this._map.put(key, value);
        }

        public Scope putDeferredValue(Object value) {
            String key = (String) Objects.requireNonNull(this._deferredKey);
            this._deferredKey = null;
            if (this._squashDups) {
                _putValueHandleDups(key, value);
                return this;
            }
            if (this._map == null) {
                this._map = new LinkedHashMap();
            }
            this._map.put(key, value);
            return this;
        }

        public void addValue(Object value) {
            if (this._list == null) {
                this._list = new ArrayList();
            }
            this._list.add(value);
        }

        public Object finishRootObject() {
            if (this._map == null) {
                return emptyMap();
            }
            return this._map;
        }

        public Scope finishBranchObject() {
            Object value;
            if (this._map == null) {
                value = new LinkedHashMap();
            } else {
                value = this._map;
                this._map = null;
            }
            if (this._parent.isObject()) {
                return this._parent.putDeferredValue(value);
            }
            this._parent.addValue(value);
            return this._parent;
        }

        public Object finishRootArray(boolean asJavaArray) {
            if (this._list == null) {
                if (asJavaArray) {
                    return UntypedObjectDeserializerNR.NO_OBJECTS;
                }
                return emptyList();
            }
            if (asJavaArray) {
                return this._list.toArray(UntypedObjectDeserializerNR.NO_OBJECTS);
            }
            return this._list;
        }

        public Scope finishBranchArray(boolean asJavaArray) {
            Object value;
            if (this._list == null) {
                if (asJavaArray) {
                    value = UntypedObjectDeserializerNR.NO_OBJECTS;
                } else {
                    value = emptyList();
                }
            } else {
                if (asJavaArray) {
                    value = this._list.toArray(UntypedObjectDeserializerNR.NO_OBJECTS);
                } else {
                    value = this._list;
                }
                this._list = null;
            }
            if (this._parent.isObject()) {
                return this._parent.putDeferredValue(value);
            }
            this._parent.addValue(value);
            return this._parent;
        }

        private void _putValueHandleDups(String key, Object newValue) {
            if (this._map == null) {
                this._map = new LinkedHashMap();
                this._map.put(key, newValue);
                return;
            }
            Object old = this._map.put(key, newValue);
            if (old != null) {
                if (old instanceof List) {
                    ((List) old).add(newValue);
                    this._map.put(key, old);
                } else {
                    ArrayList<Object> l = new ArrayList<>();
                    l.add(old);
                    l.add(newValue);
                    this._map.put(key, l);
                }
            }
        }

        public static Map<String, Object> emptyMap() {
            return new LinkedHashMap(2);
        }

        public static List<Object> emptyList() {
            return new ArrayList(2);
        }
    }
}
