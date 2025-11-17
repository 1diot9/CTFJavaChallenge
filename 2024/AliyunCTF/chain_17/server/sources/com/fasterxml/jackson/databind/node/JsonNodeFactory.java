package com.fasterxml.jackson.databind.node;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.util.RawValue;
import java.io.Serializable;
import java.math.BigDecimal;
import java.math.BigInteger;

/* loaded from: server.jar:BOOT-INF/lib/jackson-databind-2.15.3.jar:com/fasterxml/jackson/databind/node/JsonNodeFactory.class */
public class JsonNodeFactory implements Serializable, JsonNodeCreator {
    private static final long serialVersionUID = 1;
    protected static final int MAX_ELEMENT_INDEX_FOR_INSERT = 9999;

    @Deprecated
    private final boolean _cfgBigDecimalExact;
    public static final JsonNodeFactory instance = new JsonNodeFactory();

    public JsonNodeFactory(boolean bigDecimalExact) {
        this._cfgBigDecimalExact = bigDecimalExact;
    }

    protected JsonNodeFactory() {
        this(false);
    }

    @Deprecated
    public static JsonNodeFactory withExactBigDecimals(boolean bigDecimalExact) {
        return new JsonNodeFactory(bigDecimalExact);
    }

    public int getMaxElementIndexForInsert() {
        return MAX_ELEMENT_INDEX_FOR_INSERT;
    }

    public boolean willStripTrailingBigDecimalZeroes() {
        return !this._cfgBigDecimalExact;
    }

    @Override // com.fasterxml.jackson.databind.node.JsonNodeCreator
    public BooleanNode booleanNode(boolean v) {
        return v ? BooleanNode.getTrue() : BooleanNode.getFalse();
    }

    @Override // com.fasterxml.jackson.databind.node.JsonNodeCreator
    public NullNode nullNode() {
        return NullNode.getInstance();
    }

    public JsonNode missingNode() {
        return MissingNode.getInstance();
    }

    @Override // com.fasterxml.jackson.databind.node.JsonNodeCreator
    public NumericNode numberNode(byte v) {
        return IntNode.valueOf(v);
    }

    @Override // com.fasterxml.jackson.databind.node.JsonNodeCreator
    public ValueNode numberNode(Byte value) {
        return value == null ? nullNode() : IntNode.valueOf(value.intValue());
    }

    @Override // com.fasterxml.jackson.databind.node.JsonNodeCreator
    public NumericNode numberNode(short v) {
        return ShortNode.valueOf(v);
    }

    @Override // com.fasterxml.jackson.databind.node.JsonNodeCreator
    public ValueNode numberNode(Short value) {
        return value == null ? nullNode() : ShortNode.valueOf(value.shortValue());
    }

    @Override // com.fasterxml.jackson.databind.node.JsonNodeCreator
    public NumericNode numberNode(int v) {
        return IntNode.valueOf(v);
    }

    @Override // com.fasterxml.jackson.databind.node.JsonNodeCreator
    public ValueNode numberNode(Integer value) {
        return value == null ? nullNode() : IntNode.valueOf(value.intValue());
    }

    @Override // com.fasterxml.jackson.databind.node.JsonNodeCreator
    public NumericNode numberNode(long v) {
        return LongNode.valueOf(v);
    }

    @Override // com.fasterxml.jackson.databind.node.JsonNodeCreator
    public ValueNode numberNode(Long v) {
        if (v == null) {
            return nullNode();
        }
        return LongNode.valueOf(v.longValue());
    }

    @Override // com.fasterxml.jackson.databind.node.JsonNodeCreator
    public ValueNode numberNode(BigInteger v) {
        if (v == null) {
            return nullNode();
        }
        return BigIntegerNode.valueOf(v);
    }

    @Override // com.fasterxml.jackson.databind.node.JsonNodeCreator
    public NumericNode numberNode(float v) {
        return FloatNode.valueOf(v);
    }

    @Override // com.fasterxml.jackson.databind.node.JsonNodeCreator
    public ValueNode numberNode(Float value) {
        return value == null ? nullNode() : FloatNode.valueOf(value.floatValue());
    }

    @Override // com.fasterxml.jackson.databind.node.JsonNodeCreator
    public NumericNode numberNode(double v) {
        return DoubleNode.valueOf(v);
    }

    @Override // com.fasterxml.jackson.databind.node.JsonNodeCreator
    public ValueNode numberNode(Double value) {
        return value == null ? nullNode() : DoubleNode.valueOf(value.doubleValue());
    }

    @Override // com.fasterxml.jackson.databind.node.JsonNodeCreator
    public ValueNode numberNode(BigDecimal v) {
        if (v == null) {
            return nullNode();
        }
        return DecimalNode.valueOf(v);
    }

    @Override // com.fasterxml.jackson.databind.node.JsonNodeCreator
    public TextNode textNode(String text) {
        return TextNode.valueOf(text);
    }

    @Override // com.fasterxml.jackson.databind.node.JsonNodeCreator
    public BinaryNode binaryNode(byte[] data) {
        return BinaryNode.valueOf(data);
    }

    @Override // com.fasterxml.jackson.databind.node.JsonNodeCreator
    public BinaryNode binaryNode(byte[] data, int offset, int length) {
        return BinaryNode.valueOf(data, offset, length);
    }

    @Override // com.fasterxml.jackson.databind.node.JsonNodeCreator
    public ArrayNode arrayNode() {
        return new ArrayNode(this);
    }

    @Override // com.fasterxml.jackson.databind.node.JsonNodeCreator
    public ArrayNode arrayNode(int capacity) {
        return new ArrayNode(this, capacity);
    }

    @Override // com.fasterxml.jackson.databind.node.JsonNodeCreator
    public ObjectNode objectNode() {
        return new ObjectNode(this);
    }

    @Override // com.fasterxml.jackson.databind.node.JsonNodeCreator
    public ValueNode pojoNode(Object pojo) {
        return new POJONode(pojo);
    }

    @Override // com.fasterxml.jackson.databind.node.JsonNodeCreator
    public ValueNode rawValueNode(RawValue value) {
        return new POJONode(value);
    }

    protected boolean _inIntRange(long l) {
        int i = (int) l;
        long l2 = i;
        return l2 == l;
    }
}
