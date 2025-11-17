package com.fasterxml.jackson.databind.node;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonPointer;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.core.ObjectCodec;
import com.fasterxml.jackson.core.StreamReadConstraints;
import com.fasterxml.jackson.core.exc.StreamConstraintsException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.jsontype.TypeSerializer;
import com.fasterxml.jackson.databind.util.ExceptionUtil;
import java.io.IOException;
import java.io.Serializable;
import java.math.BigDecimal;
import java.math.BigInteger;

/* loaded from: server.jar:BOOT-INF/lib/jackson-databind-2.15.3.jar:com/fasterxml/jackson/databind/node/BaseJsonNode.class */
public abstract class BaseJsonNode extends JsonNode implements Serializable {
    private static final long serialVersionUID = 1;

    public abstract int hashCode();

    public abstract JsonToken asToken();

    @Override // com.fasterxml.jackson.databind.JsonSerializable
    public abstract void serialize(JsonGenerator jsonGenerator, SerializerProvider serializerProvider) throws IOException;

    @Override // com.fasterxml.jackson.databind.JsonSerializable
    public abstract void serializeWithType(JsonGenerator jsonGenerator, SerializerProvider serializerProvider, TypeSerializer typeSerializer) throws IOException;

    Object writeReplace() {
        return NodeSerialization.from(this);
    }

    @Override // com.fasterxml.jackson.databind.JsonNode
    public final JsonNode findPath(String fieldName) {
        JsonNode value = findValue(fieldName);
        if (value == null) {
            return MissingNode.getInstance();
        }
        return value;
    }

    @Override // com.fasterxml.jackson.databind.JsonNode
    public JsonNode required(String fieldName) {
        return (JsonNode) _reportRequiredViolation("Node of type `%s` has no fields", getClass().getSimpleName());
    }

    @Override // com.fasterxml.jackson.databind.JsonNode
    public JsonNode required(int index) {
        return (JsonNode) _reportRequiredViolation("Node of type `%s` has no indexed values", getClass().getSimpleName());
    }

    @Override // com.fasterxml.jackson.core.TreeNode
    public JsonParser traverse() {
        return new TreeTraversingParser(this);
    }

    @Override // com.fasterxml.jackson.core.TreeNode
    public JsonParser traverse(ObjectCodec codec) {
        return new TreeTraversingParser(this, codec);
    }

    @Override // com.fasterxml.jackson.core.TreeNode
    public JsonParser.NumberType numberType() {
        return null;
    }

    @Override // com.fasterxml.jackson.databind.JsonNode
    public ObjectNode withObject(JsonPointer ptr, JsonNode.OverwriteMode overwriteMode, boolean preferIndex) {
        if (ptr.matches()) {
            if (this instanceof ObjectNode) {
                return (ObjectNode) this;
            }
            _reportWrongNodeType("Can only call `withObject()` with empty JSON Pointer on `ObjectNode`, not `%s`", getClass().getName());
        }
        ObjectNode n = _withObject(ptr, ptr, overwriteMode, preferIndex);
        if (n == null) {
            _reportWrongNodeType("Cannot replace context node (of type `%s`) using `withObject()` with  JSON Pointer '%s'", getClass().getName(), ptr);
        }
        return n;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public ObjectNode _withObject(JsonPointer origPtr, JsonPointer currentPtr, JsonNode.OverwriteMode overwriteMode, boolean preferIndex) {
        return null;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public void _withXxxVerifyReplace(JsonPointer origPtr, JsonPointer currentPtr, JsonNode.OverwriteMode overwriteMode, boolean preferIndex, JsonNode toReplace) {
        if (!_withXxxMayReplace(toReplace, overwriteMode)) {
            _reportWrongNodeType("Cannot replace `JsonNode` of type `%s` for property \"%s\" in JSON Pointer \"%s\" (mode `OverwriteMode.%s`)", toReplace.getClass().getName(), currentPtr.getMatchingProperty(), origPtr, overwriteMode);
        }
    }

    protected boolean _withXxxMayReplace(JsonNode node, JsonNode.OverwriteMode overwriteMode) {
        switch (overwriteMode) {
            case NONE:
                return false;
            case NULLS:
                return node.isNull();
            case SCALARS:
                return !node.isContainerNode();
            case ALL:
            default:
                return true;
        }
    }

    @Override // com.fasterxml.jackson.databind.JsonNode
    public ArrayNode withArray(JsonPointer ptr, JsonNode.OverwriteMode overwriteMode, boolean preferIndex) {
        if (ptr.matches()) {
            if (this instanceof ArrayNode) {
                return (ArrayNode) this;
            }
            _reportWrongNodeType("Can only call `withArray()` with empty JSON Pointer on `ArrayNode`, not `%s`", getClass().getName());
        }
        ArrayNode n = _withArray(ptr, ptr, overwriteMode, preferIndex);
        if (n == null) {
            _reportWrongNodeType("Cannot replace context node (of type `%s`) using `withArray()` with  JSON Pointer '%s'", getClass().getName(), ptr);
        }
        return n;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public ArrayNode _withArray(JsonPointer origPtr, JsonPointer currentPtr, JsonNode.OverwriteMode overwriteMode, boolean preferIndex) {
        return null;
    }

    @Override // com.fasterxml.jackson.databind.JsonNode
    public String toString() {
        return InternalNodeMapper.nodeToString(this);
    }

    @Override // com.fasterxml.jackson.databind.JsonNode
    public String toPrettyString() {
        return InternalNodeMapper.nodeToPrettyString(this);
    }

    protected <T> T _reportWrongNodeType(String msgTemplate, Object... args) {
        throw new UnsupportedOperationException(String.format(msgTemplate, args));
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public <T> T _reportWrongNodeOperation(String msgTemplate, Object... args) {
        throw new UnsupportedOperationException(String.format(msgTemplate, args));
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public JsonPointer _jsonPointerIfValid(String exprOrProperty) {
        if (exprOrProperty.isEmpty() || exprOrProperty.charAt(0) == '/') {
            return JsonPointer.compile(exprOrProperty);
        }
        return null;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public BigInteger _bigIntFromBigDec(BigDecimal value) {
        try {
            StreamReadConstraints.defaults().validateBigIntegerScale(value.scale());
        } catch (StreamConstraintsException e) {
            ExceptionUtil.throwSneaky(e);
        }
        return value.toBigInteger();
    }
}
