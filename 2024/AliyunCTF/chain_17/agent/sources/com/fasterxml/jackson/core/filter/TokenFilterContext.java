package com.fasterxml.jackson.core.filter;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.JsonStreamContext;
import com.fasterxml.jackson.core.JsonToken;
import java.io.IOException;

/* loaded from: agent.jar:BOOT-INF/lib/jackson-core-2.15.3.jar:com/fasterxml/jackson/core/filter/TokenFilterContext.class */
public class TokenFilterContext extends JsonStreamContext {
    protected final TokenFilterContext _parent;
    protected TokenFilterContext _child;
    protected String _currentName;
    protected TokenFilter _filter;
    protected boolean _startHandled;
    protected boolean _needToHandleName;

    protected TokenFilterContext(int type, TokenFilterContext parent, TokenFilter filter, boolean startHandled) {
        this._type = type;
        this._parent = parent;
        this._filter = filter;
        this._index = -1;
        this._startHandled = startHandled;
        this._needToHandleName = false;
    }

    protected TokenFilterContext reset(int type, TokenFilter filter, boolean startWritten) {
        this._type = type;
        this._filter = filter;
        this._index = -1;
        this._currentName = null;
        this._startHandled = startWritten;
        this._needToHandleName = false;
        return this;
    }

    public static TokenFilterContext createRootContext(TokenFilter filter) {
        return new TokenFilterContext(0, null, filter, true);
    }

    public TokenFilterContext createChildArrayContext(TokenFilter filter, boolean writeStart) {
        TokenFilterContext ctxt = this._child;
        if (ctxt == null) {
            TokenFilterContext ctxt2 = new TokenFilterContext(1, this, filter, writeStart);
            this._child = ctxt2;
            return ctxt2;
        }
        return ctxt.reset(1, filter, writeStart);
    }

    public TokenFilterContext createChildObjectContext(TokenFilter filter, boolean writeStart) {
        TokenFilterContext ctxt = this._child;
        if (ctxt == null) {
            TokenFilterContext ctxt2 = new TokenFilterContext(2, this, filter, writeStart);
            this._child = ctxt2;
            return ctxt2;
        }
        return ctxt.reset(2, filter, writeStart);
    }

    public TokenFilter setFieldName(String name) throws JsonProcessingException {
        this._currentName = name;
        this._needToHandleName = true;
        return this._filter;
    }

    public TokenFilter checkValue(TokenFilter filter) {
        if (this._type == 2) {
            return filter;
        }
        int ix = this._index + 1;
        this._index = ix;
        if (this._type == 1) {
            return filter.includeElement(ix);
        }
        return filter.includeRootValue(ix);
    }

    public void ensureFieldNameWritten(JsonGenerator gen) throws IOException {
        if (this._needToHandleName) {
            this._needToHandleName = false;
            gen.writeFieldName(this._currentName);
        }
    }

    public void writePath(JsonGenerator gen) throws IOException {
        if (this._filter == null || this._filter == TokenFilter.INCLUDE_ALL) {
            return;
        }
        if (this._parent != null) {
            this._parent._writePath(gen);
        }
        if (this._startHandled) {
            if (this._needToHandleName) {
                gen.writeFieldName(this._currentName);
                return;
            }
            return;
        }
        this._startHandled = true;
        if (this._type == 2) {
            gen.writeStartObject();
            gen.writeFieldName(this._currentName);
        } else if (this._type == 1) {
            gen.writeStartArray();
        }
    }

    private void _writePath(JsonGenerator gen) throws IOException {
        if (this._filter == null || this._filter == TokenFilter.INCLUDE_ALL) {
            return;
        }
        if (this._parent != null) {
            this._parent._writePath(gen);
        }
        if (this._startHandled) {
            if (this._needToHandleName) {
                this._needToHandleName = false;
                gen.writeFieldName(this._currentName);
                return;
            }
            return;
        }
        this._startHandled = true;
        if (this._type == 2) {
            gen.writeStartObject();
            if (this._needToHandleName) {
                this._needToHandleName = false;
                gen.writeFieldName(this._currentName);
                return;
            }
            return;
        }
        if (this._type == 1) {
            gen.writeStartArray();
        }
    }

    public TokenFilterContext closeArray(JsonGenerator gen) throws IOException {
        if (this._startHandled) {
            gen.writeEndArray();
        } else if (this._filter != null && this._filter != TokenFilter.INCLUDE_ALL && this._filter.includeEmptyArray(hasCurrentIndex())) {
            if (this._parent != null) {
                this._parent._writePath(gen);
            }
            gen.writeStartArray();
            gen.writeEndArray();
        }
        if (this._filter != null && this._filter != TokenFilter.INCLUDE_ALL) {
            this._filter.filterFinishArray();
        }
        return this._parent;
    }

    public TokenFilterContext closeObject(JsonGenerator gen) throws IOException {
        if (this._startHandled) {
            gen.writeEndObject();
        } else if (this._filter != null && this._filter != TokenFilter.INCLUDE_ALL && this._filter.includeEmptyObject(hasCurrentName())) {
            if (this._parent != null) {
                this._parent._writePath(gen);
            }
            gen.writeStartObject();
            gen.writeEndObject();
        }
        if (this._filter != null && this._filter != TokenFilter.INCLUDE_ALL) {
            this._filter.filterFinishObject();
        }
        return this._parent;
    }

    public void skipParentChecks() {
        this._filter = null;
        TokenFilterContext tokenFilterContext = this._parent;
        while (true) {
            TokenFilterContext ctxt = tokenFilterContext;
            if (ctxt != null) {
                ctxt._filter = null;
                tokenFilterContext = ctxt._parent;
            } else {
                return;
            }
        }
    }

    @Override // com.fasterxml.jackson.core.JsonStreamContext
    public Object getCurrentValue() {
        return null;
    }

    @Override // com.fasterxml.jackson.core.JsonStreamContext
    public void setCurrentValue(Object v) {
    }

    @Override // com.fasterxml.jackson.core.JsonStreamContext
    public final TokenFilterContext getParent() {
        return this._parent;
    }

    @Override // com.fasterxml.jackson.core.JsonStreamContext
    public final String getCurrentName() {
        return this._currentName;
    }

    @Override // com.fasterxml.jackson.core.JsonStreamContext
    public boolean hasCurrentName() {
        return this._currentName != null;
    }

    public TokenFilter getFilter() {
        return this._filter;
    }

    public boolean isStartHandled() {
        return this._startHandled;
    }

    public JsonToken nextTokenToRead() {
        if (!this._startHandled) {
            this._startHandled = true;
            if (this._type == 2) {
                return JsonToken.START_OBJECT;
            }
            return JsonToken.START_ARRAY;
        }
        if (this._needToHandleName && this._type == 2) {
            this._needToHandleName = false;
            return JsonToken.FIELD_NAME;
        }
        return null;
    }

    public TokenFilterContext findChildOf(TokenFilterContext parent) {
        if (this._parent == parent) {
            return this;
        }
        TokenFilterContext tokenFilterContext = this._parent;
        while (true) {
            TokenFilterContext curr = tokenFilterContext;
            if (curr != null) {
                TokenFilterContext p = curr._parent;
                if (p == parent) {
                    return curr;
                }
                tokenFilterContext = p;
            } else {
                return null;
            }
        }
    }

    protected void appendDesc(StringBuilder sb) {
        if (this._parent != null) {
            this._parent.appendDesc(sb);
        }
        if (this._type == 2) {
            sb.append('{');
            if (this._currentName != null) {
                sb.append('\"');
                sb.append(this._currentName);
                sb.append('\"');
            } else {
                sb.append('?');
            }
            sb.append('}');
            return;
        }
        if (this._type == 1) {
            sb.append('[');
            sb.append(getCurrentIndex());
            sb.append(']');
            return;
        }
        sb.append("/");
    }

    @Override // com.fasterxml.jackson.core.JsonStreamContext
    public String toString() {
        StringBuilder sb = new StringBuilder(64);
        appendDesc(sb);
        return sb.toString();
    }
}
