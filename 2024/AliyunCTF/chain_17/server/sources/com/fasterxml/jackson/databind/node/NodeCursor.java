package com.fasterxml.jackson.databind.node;

import com.fasterxml.jackson.core.JsonStreamContext;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.databind.JsonNode;
import java.util.Iterator;
import java.util.Map;

/* JADX INFO: Access modifiers changed from: package-private */
/* loaded from: server.jar:BOOT-INF/lib/jackson-databind-2.15.3.jar:com/fasterxml/jackson/databind/node/NodeCursor.class */
public abstract class NodeCursor extends JsonStreamContext {
    protected final NodeCursor _parent;
    protected String _currentName;
    protected Object _currentValue;

    public abstract JsonToken nextToken();

    public abstract JsonNode currentNode();

    public abstract NodeCursor startObject();

    public abstract NodeCursor startArray();

    public NodeCursor(int contextType, NodeCursor p) {
        this._type = contextType;
        this._index = -1;
        this._parent = p;
    }

    @Override // com.fasterxml.jackson.core.JsonStreamContext
    public final NodeCursor getParent() {
        return this._parent;
    }

    @Override // com.fasterxml.jackson.core.JsonStreamContext
    public final String getCurrentName() {
        return this._currentName;
    }

    public void overrideCurrentName(String name) {
        this._currentName = name;
    }

    @Override // com.fasterxml.jackson.core.JsonStreamContext
    public Object getCurrentValue() {
        return this._currentValue;
    }

    @Override // com.fasterxml.jackson.core.JsonStreamContext
    public void setCurrentValue(Object v) {
        this._currentValue = v;
    }

    public final NodeCursor iterateChildren() {
        JsonNode n = currentNode();
        if (n == null) {
            throw new IllegalStateException("No current node");
        }
        if (n.isArray()) {
            return new ArrayCursor(n, this);
        }
        if (n.isObject()) {
            return new ObjectCursor(n, this);
        }
        throw new IllegalStateException("Current node of type " + n.getClass().getName());
    }

    /* loaded from: server.jar:BOOT-INF/lib/jackson-databind-2.15.3.jar:com/fasterxml/jackson/databind/node/NodeCursor$RootCursor.class */
    protected static final class RootCursor extends NodeCursor {
        protected JsonNode _node;
        protected boolean _done;

        @Override // com.fasterxml.jackson.databind.node.NodeCursor, com.fasterxml.jackson.core.JsonStreamContext
        public /* bridge */ /* synthetic */ JsonStreamContext getParent() {
            return super.getParent();
        }

        public RootCursor(JsonNode n, NodeCursor p) {
            super(0, p);
            this._done = false;
            this._node = n;
        }

        @Override // com.fasterxml.jackson.databind.node.NodeCursor
        public void overrideCurrentName(String name) {
        }

        @Override // com.fasterxml.jackson.databind.node.NodeCursor
        public JsonToken nextToken() {
            if (!this._done) {
                this._index++;
                this._done = true;
                return this._node.asToken();
            }
            this._node = null;
            return null;
        }

        @Override // com.fasterxml.jackson.databind.node.NodeCursor
        public JsonNode currentNode() {
            if (this._done) {
                return this._node;
            }
            return null;
        }

        @Override // com.fasterxml.jackson.databind.node.NodeCursor
        public NodeCursor startArray() {
            return new ArrayCursor(this._node, this);
        }

        @Override // com.fasterxml.jackson.databind.node.NodeCursor
        public NodeCursor startObject() {
            return new ObjectCursor(this._node, this);
        }
    }

    /* loaded from: server.jar:BOOT-INF/lib/jackson-databind-2.15.3.jar:com/fasterxml/jackson/databind/node/NodeCursor$ArrayCursor.class */
    protected static final class ArrayCursor extends NodeCursor {
        protected Iterator<JsonNode> _contents;
        protected JsonNode _currentElement;

        @Override // com.fasterxml.jackson.databind.node.NodeCursor, com.fasterxml.jackson.core.JsonStreamContext
        public /* bridge */ /* synthetic */ JsonStreamContext getParent() {
            return super.getParent();
        }

        public ArrayCursor(JsonNode n, NodeCursor p) {
            super(1, p);
            this._contents = n.elements();
        }

        @Override // com.fasterxml.jackson.databind.node.NodeCursor
        public JsonToken nextToken() {
            if (!this._contents.hasNext()) {
                this._currentElement = null;
                return JsonToken.END_ARRAY;
            }
            this._index++;
            this._currentElement = this._contents.next();
            return this._currentElement.asToken();
        }

        @Override // com.fasterxml.jackson.databind.node.NodeCursor
        public JsonNode currentNode() {
            return this._currentElement;
        }

        @Override // com.fasterxml.jackson.databind.node.NodeCursor
        public NodeCursor startArray() {
            return new ArrayCursor(this._currentElement, this);
        }

        @Override // com.fasterxml.jackson.databind.node.NodeCursor
        public NodeCursor startObject() {
            return new ObjectCursor(this._currentElement, this);
        }
    }

    /* loaded from: server.jar:BOOT-INF/lib/jackson-databind-2.15.3.jar:com/fasterxml/jackson/databind/node/NodeCursor$ObjectCursor.class */
    protected static final class ObjectCursor extends NodeCursor {
        protected Iterator<Map.Entry<String, JsonNode>> _contents;
        protected Map.Entry<String, JsonNode> _current;
        protected boolean _needEntry;

        @Override // com.fasterxml.jackson.databind.node.NodeCursor, com.fasterxml.jackson.core.JsonStreamContext
        public /* bridge */ /* synthetic */ JsonStreamContext getParent() {
            return super.getParent();
        }

        public ObjectCursor(JsonNode n, NodeCursor p) {
            super(2, p);
            this._contents = n.fields();
            this._needEntry = true;
        }

        @Override // com.fasterxml.jackson.databind.node.NodeCursor
        public JsonToken nextToken() {
            if (this._needEntry) {
                if (!this._contents.hasNext()) {
                    this._currentName = null;
                    this._current = null;
                    return JsonToken.END_OBJECT;
                }
                this._index++;
                this._needEntry = false;
                this._current = this._contents.next();
                this._currentName = this._current == null ? null : this._current.getKey();
                return JsonToken.FIELD_NAME;
            }
            this._needEntry = true;
            return this._current.getValue().asToken();
        }

        @Override // com.fasterxml.jackson.databind.node.NodeCursor
        public JsonNode currentNode() {
            if (this._current == null) {
                return null;
            }
            return this._current.getValue();
        }

        @Override // com.fasterxml.jackson.databind.node.NodeCursor
        public NodeCursor startArray() {
            return new ArrayCursor(currentNode(), this);
        }

        @Override // com.fasterxml.jackson.databind.node.NodeCursor
        public NodeCursor startObject() {
            return new ObjectCursor(currentNode(), this);
        }
    }
}
