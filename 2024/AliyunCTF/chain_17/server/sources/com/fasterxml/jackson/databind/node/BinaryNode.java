package com.fasterxml.jackson.databind.node;

import com.fasterxml.jackson.core.Base64Variants;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.databind.SerializerProvider;
import java.io.IOException;
import java.util.Arrays;

/* loaded from: server.jar:BOOT-INF/lib/jackson-databind-2.15.3.jar:com/fasterxml/jackson/databind/node/BinaryNode.class */
public class BinaryNode extends ValueNode {
    private static final long serialVersionUID = 2;
    static final BinaryNode EMPTY_BINARY_NODE = new BinaryNode(new byte[0]);
    protected final byte[] _data;

    public BinaryNode(byte[] data) {
        this._data = data;
    }

    public BinaryNode(byte[] data, int offset, int length) {
        if (offset == 0 && length == data.length) {
            this._data = data;
        } else {
            this._data = new byte[length];
            System.arraycopy(data, offset, this._data, 0, length);
        }
    }

    public static BinaryNode valueOf(byte[] data) {
        if (data == null) {
            return null;
        }
        if (data.length == 0) {
            return EMPTY_BINARY_NODE;
        }
        return new BinaryNode(data);
    }

    public static BinaryNode valueOf(byte[] data, int offset, int length) {
        if (data == null) {
            return null;
        }
        if (length == 0) {
            return EMPTY_BINARY_NODE;
        }
        return new BinaryNode(data, offset, length);
    }

    @Override // com.fasterxml.jackson.databind.JsonNode
    public JsonNodeType getNodeType() {
        return JsonNodeType.BINARY;
    }

    @Override // com.fasterxml.jackson.databind.node.ValueNode, com.fasterxml.jackson.databind.node.BaseJsonNode, com.fasterxml.jackson.core.TreeNode
    public JsonToken asToken() {
        return JsonToken.VALUE_EMBEDDED_OBJECT;
    }

    @Override // com.fasterxml.jackson.databind.JsonNode
    public byte[] binaryValue() {
        return this._data;
    }

    @Override // com.fasterxml.jackson.databind.JsonNode
    public String asText() {
        return Base64Variants.getDefaultVariant().encode(this._data, false);
    }

    @Override // com.fasterxml.jackson.databind.node.BaseJsonNode, com.fasterxml.jackson.databind.JsonSerializable
    public final void serialize(JsonGenerator jg, SerializerProvider provider) throws IOException {
        jg.writeBinary(provider.getConfig().getBase64Variant(), this._data, 0, this._data.length);
    }

    @Override // com.fasterxml.jackson.databind.JsonNode
    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }
        if (o == null || !(o instanceof BinaryNode)) {
            return false;
        }
        return Arrays.equals(((BinaryNode) o)._data, this._data);
    }

    @Override // com.fasterxml.jackson.databind.node.BaseJsonNode
    public int hashCode() {
        if (this._data == null) {
            return -1;
        }
        return this._data.length;
    }
}
