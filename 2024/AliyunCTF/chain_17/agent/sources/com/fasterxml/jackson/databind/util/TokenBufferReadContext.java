package com.fasterxml.jackson.databind.util;

import com.fasterxml.jackson.core.JsonLocation;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.JsonStreamContext;
import com.fasterxml.jackson.core.io.ContentReference;
import com.fasterxml.jackson.core.json.JsonReadContext;

/* loaded from: agent.jar:BOOT-INF/lib/jackson-databind-2.15.3.jar:com/fasterxml/jackson/databind/util/TokenBufferReadContext.class */
public class TokenBufferReadContext extends JsonStreamContext {
    protected final JsonStreamContext _parent;
    protected final JsonLocation _startLocation;
    protected String _currentName;
    protected Object _currentValue;

    protected TokenBufferReadContext(JsonStreamContext base, ContentReference srcRef) {
        super(base);
        this._parent = base.getParent();
        this._currentName = base.getCurrentName();
        this._currentValue = base.getCurrentValue();
        if (base instanceof JsonReadContext) {
            JsonReadContext rc = (JsonReadContext) base;
            this._startLocation = rc.startLocation(srcRef);
        } else {
            this._startLocation = JsonLocation.NA;
        }
    }

    @Deprecated
    protected TokenBufferReadContext(JsonStreamContext base, Object srcRef) {
        this(base, srcRef instanceof ContentReference ? (ContentReference) srcRef : ContentReference.rawReference(srcRef));
    }

    protected TokenBufferReadContext(JsonStreamContext base, JsonLocation startLoc) {
        super(base);
        this._parent = base.getParent();
        this._currentName = base.getCurrentName();
        this._currentValue = base.getCurrentValue();
        this._startLocation = startLoc;
    }

    protected TokenBufferReadContext() {
        super(0, -1);
        this._parent = null;
        this._startLocation = JsonLocation.NA;
    }

    protected TokenBufferReadContext(TokenBufferReadContext parent, int type, int index) {
        super(type, index);
        this._parent = parent;
        this._startLocation = parent._startLocation;
    }

    @Override // com.fasterxml.jackson.core.JsonStreamContext
    public Object getCurrentValue() {
        return this._currentValue;
    }

    @Override // com.fasterxml.jackson.core.JsonStreamContext
    public void setCurrentValue(Object v) {
        this._currentValue = v;
    }

    public static TokenBufferReadContext createRootContext(JsonStreamContext origContext) {
        if (origContext == null) {
            return new TokenBufferReadContext();
        }
        return new TokenBufferReadContext(origContext, ContentReference.unknown());
    }

    public TokenBufferReadContext createChildArrayContext() {
        this._index++;
        return new TokenBufferReadContext(this, 1, -1);
    }

    public TokenBufferReadContext createChildObjectContext() {
        this._index++;
        return new TokenBufferReadContext(this, 2, -1);
    }

    public TokenBufferReadContext parentOrCopy() {
        if (this._parent instanceof TokenBufferReadContext) {
            return (TokenBufferReadContext) this._parent;
        }
        if (this._parent == null) {
            return new TokenBufferReadContext();
        }
        return new TokenBufferReadContext(this._parent, this._startLocation);
    }

    @Override // com.fasterxml.jackson.core.JsonStreamContext
    public String getCurrentName() {
        return this._currentName;
    }

    @Override // com.fasterxml.jackson.core.JsonStreamContext
    public boolean hasCurrentName() {
        return this._currentName != null;
    }

    @Override // com.fasterxml.jackson.core.JsonStreamContext
    public JsonStreamContext getParent() {
        return this._parent;
    }

    public void setCurrentName(String name) throws JsonProcessingException {
        this._currentName = name;
    }

    public void updateForValue() {
        this._index++;
    }
}
