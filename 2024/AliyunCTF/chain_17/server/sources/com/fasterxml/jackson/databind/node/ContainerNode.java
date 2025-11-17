package com.fasterxml.jackson.databind.node;

import com.fasterxml.jackson.core.JsonPointer;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ContainerNode;
import com.fasterxml.jackson.databind.util.RawValue;
import java.math.BigDecimal;
import java.math.BigInteger;

/* loaded from: server.jar:BOOT-INF/lib/jackson-databind-2.15.3.jar:com/fasterxml/jackson/databind/node/ContainerNode.class */
public abstract class ContainerNode<T extends ContainerNode<T>> extends BaseJsonNode implements JsonNodeCreator {
    private static final long serialVersionUID = 1;
    protected final JsonNodeFactory _nodeFactory;

    @Override // com.fasterxml.jackson.databind.node.BaseJsonNode, com.fasterxml.jackson.core.TreeNode
    public abstract JsonToken asToken();

    @Override // com.fasterxml.jackson.databind.JsonNode, com.fasterxml.jackson.core.TreeNode
    public abstract int size();

    @Override // com.fasterxml.jackson.databind.JsonNode, com.fasterxml.jackson.core.TreeNode
    public abstract JsonNode get(int i);

    @Override // com.fasterxml.jackson.databind.JsonNode, com.fasterxml.jackson.core.TreeNode
    public abstract JsonNode get(String str);

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.fasterxml.jackson.databind.node.BaseJsonNode
    public abstract ObjectNode _withObject(JsonPointer jsonPointer, JsonPointer jsonPointer2, JsonNode.OverwriteMode overwriteMode, boolean z);

    public abstract T removeAll();

    /* JADX INFO: Access modifiers changed from: protected */
    public ContainerNode(JsonNodeFactory nc) {
        this._nodeFactory = nc;
    }

    protected ContainerNode() {
        this._nodeFactory = null;
    }

    @Override // com.fasterxml.jackson.databind.JsonNode
    public String asText() {
        return "";
    }

    @Override // com.fasterxml.jackson.databind.node.JsonNodeCreator
    public final BooleanNode booleanNode(boolean v) {
        return this._nodeFactory.booleanNode(v);
    }

    public JsonNode missingNode() {
        return this._nodeFactory.missingNode();
    }

    @Override // com.fasterxml.jackson.databind.node.JsonNodeCreator
    public final NullNode nullNode() {
        return this._nodeFactory.nullNode();
    }

    @Override // com.fasterxml.jackson.databind.node.JsonNodeCreator
    public final ArrayNode arrayNode() {
        return this._nodeFactory.arrayNode();
    }

    @Override // com.fasterxml.jackson.databind.node.JsonNodeCreator
    public final ArrayNode arrayNode(int capacity) {
        return this._nodeFactory.arrayNode(capacity);
    }

    @Override // com.fasterxml.jackson.databind.node.JsonNodeCreator
    public final ObjectNode objectNode() {
        return this._nodeFactory.objectNode();
    }

    @Override // com.fasterxml.jackson.databind.node.JsonNodeCreator
    public final NumericNode numberNode(byte v) {
        return this._nodeFactory.numberNode(v);
    }

    @Override // com.fasterxml.jackson.databind.node.JsonNodeCreator
    public final NumericNode numberNode(short v) {
        return this._nodeFactory.numberNode(v);
    }

    @Override // com.fasterxml.jackson.databind.node.JsonNodeCreator
    public final NumericNode numberNode(int v) {
        return this._nodeFactory.numberNode(v);
    }

    @Override // com.fasterxml.jackson.databind.node.JsonNodeCreator
    public final NumericNode numberNode(long v) {
        return this._nodeFactory.numberNode(v);
    }

    @Override // com.fasterxml.jackson.databind.node.JsonNodeCreator
    public final NumericNode numberNode(float v) {
        return this._nodeFactory.numberNode(v);
    }

    @Override // com.fasterxml.jackson.databind.node.JsonNodeCreator
    public final NumericNode numberNode(double v) {
        return this._nodeFactory.numberNode(v);
    }

    @Override // com.fasterxml.jackson.databind.node.JsonNodeCreator
    public final ValueNode numberNode(BigInteger v) {
        return this._nodeFactory.numberNode(v);
    }

    @Override // com.fasterxml.jackson.databind.node.JsonNodeCreator
    public final ValueNode numberNode(BigDecimal v) {
        return this._nodeFactory.numberNode(v);
    }

    @Override // com.fasterxml.jackson.databind.node.JsonNodeCreator
    public final ValueNode numberNode(Byte v) {
        return this._nodeFactory.numberNode(v);
    }

    @Override // com.fasterxml.jackson.databind.node.JsonNodeCreator
    public final ValueNode numberNode(Short v) {
        return this._nodeFactory.numberNode(v);
    }

    @Override // com.fasterxml.jackson.databind.node.JsonNodeCreator
    public final ValueNode numberNode(Integer v) {
        return this._nodeFactory.numberNode(v);
    }

    @Override // com.fasterxml.jackson.databind.node.JsonNodeCreator
    public final ValueNode numberNode(Long v) {
        return this._nodeFactory.numberNode(v);
    }

    @Override // com.fasterxml.jackson.databind.node.JsonNodeCreator
    public final ValueNode numberNode(Float v) {
        return this._nodeFactory.numberNode(v);
    }

    @Override // com.fasterxml.jackson.databind.node.JsonNodeCreator
    public final ValueNode numberNode(Double v) {
        return this._nodeFactory.numberNode(v);
    }

    @Override // com.fasterxml.jackson.databind.node.JsonNodeCreator
    public final TextNode textNode(String text) {
        return this._nodeFactory.textNode(text);
    }

    @Override // com.fasterxml.jackson.databind.node.JsonNodeCreator
    public final BinaryNode binaryNode(byte[] data) {
        return this._nodeFactory.binaryNode(data);
    }

    @Override // com.fasterxml.jackson.databind.node.JsonNodeCreator
    public final BinaryNode binaryNode(byte[] data, int offset, int length) {
        return this._nodeFactory.binaryNode(data, offset, length);
    }

    @Override // com.fasterxml.jackson.databind.node.JsonNodeCreator
    public final ValueNode pojoNode(Object pojo) {
        return this._nodeFactory.pojoNode(pojo);
    }

    @Override // com.fasterxml.jackson.databind.node.JsonNodeCreator
    public final ValueNode rawValueNode(RawValue value) {
        return this._nodeFactory.rawValueNode(value);
    }
}
