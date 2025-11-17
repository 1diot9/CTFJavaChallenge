package com.fasterxml.jackson.databind.node;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.databind.JsonSerializable;
import com.fasterxml.jackson.databind.SerializerProvider;
import java.io.IOException;

/* loaded from: server.jar:BOOT-INF/lib/jackson-databind-2.15.3.jar:com/fasterxml/jackson/databind/node/POJONode.class */
public class POJONode extends ValueNode {
    private static final long serialVersionUID = 2;
    protected final Object _value;

    public POJONode(Object v) {
        this._value = v;
    }

    @Override // com.fasterxml.jackson.databind.JsonNode
    public JsonNodeType getNodeType() {
        return JsonNodeType.POJO;
    }

    @Override // com.fasterxml.jackson.databind.node.ValueNode, com.fasterxml.jackson.databind.node.BaseJsonNode, com.fasterxml.jackson.core.TreeNode
    public JsonToken asToken() {
        return JsonToken.VALUE_EMBEDDED_OBJECT;
    }

    @Override // com.fasterxml.jackson.databind.JsonNode
    public byte[] binaryValue() throws IOException {
        if (this._value instanceof byte[]) {
            return (byte[]) this._value;
        }
        return super.binaryValue();
    }

    @Override // com.fasterxml.jackson.databind.JsonNode
    public String asText() {
        return this._value == null ? "null" : this._value.toString();
    }

    @Override // com.fasterxml.jackson.databind.JsonNode
    public String asText(String defaultValue) {
        return this._value == null ? defaultValue : this._value.toString();
    }

    @Override // com.fasterxml.jackson.databind.JsonNode
    public boolean asBoolean(boolean defaultValue) {
        if (this._value != null && (this._value instanceof Boolean)) {
            return ((Boolean) this._value).booleanValue();
        }
        return defaultValue;
    }

    @Override // com.fasterxml.jackson.databind.JsonNode
    public int asInt(int defaultValue) {
        if (this._value instanceof Number) {
            return ((Number) this._value).intValue();
        }
        return defaultValue;
    }

    @Override // com.fasterxml.jackson.databind.JsonNode
    public long asLong(long defaultValue) {
        if (this._value instanceof Number) {
            return ((Number) this._value).longValue();
        }
        return defaultValue;
    }

    @Override // com.fasterxml.jackson.databind.JsonNode
    public double asDouble(double defaultValue) {
        if (this._value instanceof Number) {
            return ((Number) this._value).doubleValue();
        }
        return defaultValue;
    }

    @Override // com.fasterxml.jackson.databind.node.BaseJsonNode, com.fasterxml.jackson.databind.JsonSerializable
    public final void serialize(JsonGenerator gen, SerializerProvider ctxt) throws IOException {
        if (this._value == null) {
            ctxt.defaultSerializeNull(gen);
        } else if (this._value instanceof JsonSerializable) {
            ((JsonSerializable) this._value).serialize(gen, ctxt);
        } else {
            ctxt.defaultSerializeValue(this._value, gen);
        }
    }

    public Object getPojo() {
        return this._value;
    }

    @Override // com.fasterxml.jackson.databind.JsonNode
    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }
        if (o != null && (o instanceof POJONode)) {
            return _pojoEquals((POJONode) o);
        }
        return false;
    }

    protected boolean _pojoEquals(POJONode other) {
        if (this._value == null) {
            return other._value == null;
        }
        return this._value.equals(other._value);
    }

    @Override // com.fasterxml.jackson.databind.node.BaseJsonNode
    public int hashCode() {
        return this._value.hashCode();
    }
}
