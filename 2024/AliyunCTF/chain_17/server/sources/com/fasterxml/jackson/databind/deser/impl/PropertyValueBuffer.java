package com.fasterxml.jackson.databind.deser.impl;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DatabindException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.deser.SettableAnyProperty;
import com.fasterxml.jackson.databind.deser.SettableBeanProperty;
import com.fasterxml.jackson.databind.deser.impl.PropertyValue;
import com.fasterxml.jackson.databind.introspect.AnnotatedMember;
import java.io.IOException;
import java.util.BitSet;

/* loaded from: server.jar:BOOT-INF/lib/jackson-databind-2.15.3.jar:com/fasterxml/jackson/databind/deser/impl/PropertyValueBuffer.class */
public class PropertyValueBuffer {
    protected final JsonParser _parser;
    protected final DeserializationContext _context;
    protected final ObjectIdReader _objectIdReader;
    protected final Object[] _creatorParameters;
    protected int _paramsNeeded;
    protected int _paramsSeen;
    protected final BitSet _paramsSeenBig;
    protected PropertyValue _buffered;
    protected Object _idValue;

    public PropertyValueBuffer(JsonParser p, DeserializationContext ctxt, int paramCount, ObjectIdReader oir) {
        this._parser = p;
        this._context = ctxt;
        this._paramsNeeded = paramCount;
        this._objectIdReader = oir;
        this._creatorParameters = new Object[paramCount];
        if (paramCount < 32) {
            this._paramsSeenBig = null;
        } else {
            this._paramsSeenBig = new BitSet();
        }
    }

    public final boolean hasParameter(SettableBeanProperty prop) {
        if (this._paramsSeenBig == null) {
            return ((this._paramsSeen >> prop.getCreatorIndex()) & 1) == 1;
        }
        return this._paramsSeenBig.get(prop.getCreatorIndex());
    }

    public Object getParameter(SettableBeanProperty prop) throws JsonMappingException {
        Object value;
        if (hasParameter(prop)) {
            value = this._creatorParameters[prop.getCreatorIndex()];
        } else {
            Object[] objArr = this._creatorParameters;
            int creatorIndex = prop.getCreatorIndex();
            Object _findMissing = _findMissing(prop);
            objArr[creatorIndex] = _findMissing;
            value = _findMissing;
        }
        if (value == null && this._context.isEnabled(DeserializationFeature.FAIL_ON_NULL_CREATOR_PROPERTIES)) {
            return this._context.reportInputMismatch(prop, "Null value for creator property '%s' (index %d); `DeserializationFeature.FAIL_ON_NULL_CREATOR_PROPERTIES` enabled", prop.getName(), Integer.valueOf(prop.getCreatorIndex()));
        }
        return value;
    }

    public Object[] getParameters(SettableBeanProperty[] props) throws JsonMappingException {
        if (this._paramsNeeded > 0) {
            if (this._paramsSeenBig == null) {
                int mask = this._paramsSeen;
                int ix = 0;
                int len = this._creatorParameters.length;
                while (ix < len) {
                    if ((mask & 1) == 0) {
                        this._creatorParameters[ix] = _findMissing(props[ix]);
                    }
                    ix++;
                    mask >>= 1;
                }
            } else {
                int len2 = this._creatorParameters.length;
                int ix2 = 0;
                while (true) {
                    int ix3 = this._paramsSeenBig.nextClearBit(ix2);
                    if (ix3 >= len2) {
                        break;
                    }
                    this._creatorParameters[ix3] = _findMissing(props[ix3]);
                    ix2 = ix3 + 1;
                }
            }
        }
        if (this._context.isEnabled(DeserializationFeature.FAIL_ON_NULL_CREATOR_PROPERTIES)) {
            for (int ix4 = 0; ix4 < props.length; ix4++) {
                if (this._creatorParameters[ix4] == null) {
                    SettableBeanProperty prop = props[ix4];
                    this._context.reportInputMismatch(prop, "Null value for creator property '%s' (index %d); `DeserializationFeature.FAIL_ON_NULL_CREATOR_PROPERTIES` enabled", prop.getName(), Integer.valueOf(props[ix4].getCreatorIndex()));
                }
            }
        }
        return this._creatorParameters;
    }

    protected Object _findMissing(SettableBeanProperty prop) throws JsonMappingException {
        Object injectableValueId = prop.getInjectableValueId();
        if (injectableValueId != null) {
            return this._context.findInjectableValue(prop.getInjectableValueId(), prop, null);
        }
        if (prop.isRequired()) {
            this._context.reportInputMismatch(prop, "Missing required creator property '%s' (index %d)", prop.getName(), Integer.valueOf(prop.getCreatorIndex()));
        }
        if (this._context.isEnabled(DeserializationFeature.FAIL_ON_MISSING_CREATOR_PROPERTIES)) {
            this._context.reportInputMismatch(prop, "Missing creator property '%s' (index %d); `DeserializationFeature.FAIL_ON_MISSING_CREATOR_PROPERTIES` enabled", prop.getName(), Integer.valueOf(prop.getCreatorIndex()));
        }
        try {
            Object absentValue = prop.getNullValueProvider().getAbsentValue(this._context);
            if (absentValue != null) {
                return absentValue;
            }
            JsonDeserializer<Object> deser = prop.getValueDeserializer();
            return deser.getAbsentValue(this._context);
        } catch (DatabindException e) {
            AnnotatedMember member = prop.getMember();
            if (member != null) {
                e.prependPath(member.getDeclaringClass(), prop.getName());
            }
            throw e;
        }
    }

    public boolean readIdProperty(String propName) throws IOException {
        if (this._objectIdReader != null && propName.equals(this._objectIdReader.propertyName.getSimpleName())) {
            this._idValue = this._objectIdReader.readObjectReference(this._parser, this._context);
            return true;
        }
        return false;
    }

    public Object handleIdValue(DeserializationContext ctxt, Object bean) throws IOException {
        if (this._objectIdReader != null) {
            if (this._idValue != null) {
                ReadableObjectId roid = ctxt.findObjectId(this._idValue, this._objectIdReader.generator, this._objectIdReader.resolver);
                roid.bindItem(bean);
                SettableBeanProperty idProp = this._objectIdReader.idProperty;
                if (idProp != null) {
                    return idProp.setAndReturn(bean, this._idValue);
                }
            } else {
                ctxt.reportUnresolvedObjectId(this._objectIdReader, bean);
            }
        }
        return bean;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public PropertyValue buffered() {
        return this._buffered;
    }

    public boolean isComplete() {
        return this._paramsNeeded <= 0;
    }

    public boolean assignParameter(SettableBeanProperty prop, Object value) {
        int ix = prop.getCreatorIndex();
        this._creatorParameters[ix] = value;
        if (this._paramsSeenBig == null) {
            int old = this._paramsSeen;
            int newValue = old | (1 << ix);
            if (old != newValue) {
                this._paramsSeen = newValue;
                int i = this._paramsNeeded - 1;
                this._paramsNeeded = i;
                if (i <= 0) {
                    return this._objectIdReader == null || this._idValue != null;
                }
                return false;
            }
            return false;
        }
        if (!this._paramsSeenBig.get(ix)) {
            this._paramsSeenBig.set(ix);
            int i2 = this._paramsNeeded - 1;
            this._paramsNeeded = i2;
            if (i2 <= 0) {
            }
            return false;
        }
        return false;
    }

    public void bufferProperty(SettableBeanProperty prop, Object value) {
        this._buffered = new PropertyValue.Regular(this._buffered, value, prop);
    }

    public void bufferAnyProperty(SettableAnyProperty prop, String propName, Object value) {
        this._buffered = new PropertyValue.Any(this._buffered, value, prop, propName);
    }

    public void bufferMapProperty(Object key, Object value) {
        this._buffered = new PropertyValue.Map(this._buffered, value, key);
    }
}
