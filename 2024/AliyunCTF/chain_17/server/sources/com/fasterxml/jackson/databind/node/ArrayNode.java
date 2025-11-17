package com.fasterxml.jackson.databind.node;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonPointer;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.core.type.WritableTypeId;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.jsontype.TypeSerializer;
import com.fasterxml.jackson.databind.util.RawValue;
import java.io.IOException;
import java.io.Serializable;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;

/* loaded from: server.jar:BOOT-INF/lib/jackson-databind-2.15.3.jar:com/fasterxml/jackson/databind/node/ArrayNode.class */
public class ArrayNode extends ContainerNode<ArrayNode> implements Serializable {
    private static final long serialVersionUID = 1;
    private final List<JsonNode> _children;

    public ArrayNode(JsonNodeFactory nf) {
        super(nf);
        this._children = new ArrayList();
    }

    public ArrayNode(JsonNodeFactory nf, int capacity) {
        super(nf);
        this._children = new ArrayList(capacity);
    }

    public ArrayNode(JsonNodeFactory nf, List<JsonNode> children) {
        super(nf);
        this._children = children;
    }

    @Override // com.fasterxml.jackson.databind.JsonNode
    protected JsonNode _at(JsonPointer ptr) {
        return get(ptr.getMatchingIndex());
    }

    @Override // com.fasterxml.jackson.databind.JsonNode
    public ArrayNode deepCopy() {
        ArrayNode ret = new ArrayNode(this._nodeFactory);
        for (JsonNode element : this._children) {
            ret._children.add(element.deepCopy());
        }
        return ret;
    }

    @Override // com.fasterxml.jackson.databind.JsonNode
    @Deprecated
    public ObjectNode with(String exprOrProperty) {
        JsonPointer ptr = _jsonPointerIfValid(exprOrProperty);
        if (ptr != null) {
            return withObject(ptr);
        }
        return (ObjectNode) super.with(exprOrProperty);
    }

    @Override // com.fasterxml.jackson.databind.JsonNode
    public ArrayNode withArray(String exprOrProperty) {
        JsonPointer ptr = _jsonPointerIfValid(exprOrProperty);
        if (ptr != null) {
            return withArray(ptr);
        }
        return (ArrayNode) super.withArray(exprOrProperty);
    }

    @Override // com.fasterxml.jackson.databind.node.ContainerNode, com.fasterxml.jackson.databind.node.BaseJsonNode
    protected ObjectNode _withObject(JsonPointer origPtr, JsonPointer currentPtr, JsonNode.OverwriteMode overwriteMode, boolean preferIndex) {
        if (currentPtr.matches()) {
            return null;
        }
        JsonNode n = _at(currentPtr);
        if (n != null && (n instanceof BaseJsonNode)) {
            ObjectNode found = ((BaseJsonNode) n)._withObject(origPtr, currentPtr.tail(), overwriteMode, preferIndex);
            if (found != null) {
                return found;
            }
            _withXxxVerifyReplace(origPtr, currentPtr, overwriteMode, preferIndex, n);
        }
        return _withObjectAddTailElement(currentPtr, preferIndex);
    }

    @Override // com.fasterxml.jackson.databind.node.BaseJsonNode
    protected ArrayNode _withArray(JsonPointer origPtr, JsonPointer currentPtr, JsonNode.OverwriteMode overwriteMode, boolean preferIndex) {
        if (currentPtr.matches()) {
            return this;
        }
        JsonNode n = _at(currentPtr);
        if (n != null && (n instanceof BaseJsonNode)) {
            ArrayNode found = ((BaseJsonNode) n)._withArray(origPtr, currentPtr.tail(), overwriteMode, preferIndex);
            if (found != null) {
                return found;
            }
            _withXxxVerifyReplace(origPtr, currentPtr, overwriteMode, preferIndex, n);
        }
        return _withArrayAddTailElement(currentPtr, preferIndex);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public ObjectNode _withObjectAddTailElement(JsonPointer tail, boolean preferIndex) {
        int index = tail.getMatchingIndex();
        if (index < 0) {
            return null;
        }
        JsonPointer tail2 = tail.tail();
        if (tail2.matches()) {
            ObjectNode result = objectNode();
            _withXxxSetArrayElement(index, result);
            return result;
        }
        if (preferIndex && tail2.mayMatchElement()) {
            ArrayNode next = arrayNode();
            _withXxxSetArrayElement(index, next);
            return next._withObjectAddTailElement(tail2, preferIndex);
        }
        ObjectNode next2 = objectNode();
        _withXxxSetArrayElement(index, next2);
        return next2._withObjectAddTailProperty(tail2, preferIndex);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public ArrayNode _withArrayAddTailElement(JsonPointer tail, boolean preferIndex) {
        int index = tail.getMatchingIndex();
        if (index < 0) {
            return null;
        }
        JsonPointer tail2 = tail.tail();
        if (tail2.matches()) {
            ArrayNode result = arrayNode();
            _withXxxSetArrayElement(index, result);
            return result;
        }
        if (preferIndex && tail2.mayMatchElement()) {
            ArrayNode next = arrayNode();
            _withXxxSetArrayElement(index, next);
            return next._withArrayAddTailElement(tail2, preferIndex);
        }
        ObjectNode next2 = objectNode();
        _withXxxSetArrayElement(index, next2);
        return next2._withArrayAddTailProperty(tail2, preferIndex);
    }

    protected void _withXxxSetArrayElement(int index, JsonNode value) {
        if (index >= size()) {
            int max = this._nodeFactory.getMaxElementIndexForInsert();
            if (index > max) {
                _reportWrongNodeOperation("Too big Array index (%d; max %d) to use for insert with `JsonPointer`", Integer.valueOf(index), Integer.valueOf(max));
            }
            while (index >= size()) {
                addNull();
            }
        }
        set(index, value);
    }

    @Override // com.fasterxml.jackson.databind.JsonSerializable.Base
    public boolean isEmpty(SerializerProvider serializers) {
        return this._children.isEmpty();
    }

    @Override // com.fasterxml.jackson.databind.JsonNode
    public JsonNodeType getNodeType() {
        return JsonNodeType.ARRAY;
    }

    @Override // com.fasterxml.jackson.databind.JsonNode, com.fasterxml.jackson.core.TreeNode
    public boolean isArray() {
        return true;
    }

    @Override // com.fasterxml.jackson.databind.node.ContainerNode, com.fasterxml.jackson.databind.node.BaseJsonNode, com.fasterxml.jackson.core.TreeNode
    public JsonToken asToken() {
        return JsonToken.START_ARRAY;
    }

    @Override // com.fasterxml.jackson.databind.node.ContainerNode, com.fasterxml.jackson.databind.JsonNode, com.fasterxml.jackson.core.TreeNode
    public int size() {
        return this._children.size();
    }

    @Override // com.fasterxml.jackson.databind.JsonNode
    public boolean isEmpty() {
        return this._children.isEmpty();
    }

    @Override // com.fasterxml.jackson.databind.JsonNode
    public Iterator<JsonNode> elements() {
        return this._children.iterator();
    }

    @Override // com.fasterxml.jackson.databind.node.ContainerNode, com.fasterxml.jackson.databind.JsonNode, com.fasterxml.jackson.core.TreeNode
    public JsonNode get(int index) {
        if (index >= 0 && index < this._children.size()) {
            return this._children.get(index);
        }
        return null;
    }

    @Override // com.fasterxml.jackson.databind.node.ContainerNode, com.fasterxml.jackson.databind.JsonNode, com.fasterxml.jackson.core.TreeNode
    public JsonNode get(String fieldName) {
        return null;
    }

    @Override // com.fasterxml.jackson.databind.JsonNode, com.fasterxml.jackson.core.TreeNode
    public JsonNode path(String fieldName) {
        return MissingNode.getInstance();
    }

    @Override // com.fasterxml.jackson.databind.JsonNode, com.fasterxml.jackson.core.TreeNode
    public JsonNode path(int index) {
        if (index >= 0 && index < this._children.size()) {
            return this._children.get(index);
        }
        return MissingNode.getInstance();
    }

    @Override // com.fasterxml.jackson.databind.node.BaseJsonNode, com.fasterxml.jackson.databind.JsonNode
    public JsonNode required(int index) {
        if (index >= 0 && index < this._children.size()) {
            return this._children.get(index);
        }
        return (JsonNode) _reportRequiredViolation("No value at index #%d [0, %d) of `ArrayNode`", Integer.valueOf(index), Integer.valueOf(this._children.size()));
    }

    @Override // com.fasterxml.jackson.databind.JsonNode
    public boolean equals(Comparator<JsonNode> comparator, JsonNode o) {
        if (!(o instanceof ArrayNode)) {
            return false;
        }
        ArrayNode other = (ArrayNode) o;
        int len = this._children.size();
        if (other.size() != len) {
            return false;
        }
        List<JsonNode> l1 = this._children;
        List<JsonNode> l2 = other._children;
        for (int i = 0; i < len; i++) {
            if (!l1.get(i).equals(comparator, l2.get(i))) {
                return false;
            }
        }
        return true;
    }

    @Override // com.fasterxml.jackson.databind.node.BaseJsonNode, com.fasterxml.jackson.databind.JsonSerializable
    public void serialize(JsonGenerator g, SerializerProvider provider) throws IOException {
        List<JsonNode> c = this._children;
        int size = c.size();
        g.writeStartArray(this, size);
        for (int i = 0; i < size; i++) {
            JsonNode value = c.get(i);
            value.serialize(g, provider);
        }
        g.writeEndArray();
    }

    @Override // com.fasterxml.jackson.databind.node.BaseJsonNode, com.fasterxml.jackson.databind.JsonSerializable
    public void serializeWithType(JsonGenerator g, SerializerProvider provider, TypeSerializer typeSer) throws IOException {
        WritableTypeId typeIdDef = typeSer.writeTypePrefix(g, typeSer.typeId(this, JsonToken.START_ARRAY));
        for (JsonNode n : this._children) {
            ((BaseJsonNode) n).serialize(g, provider);
        }
        typeSer.writeTypeSuffix(g, typeIdDef);
    }

    @Override // com.fasterxml.jackson.databind.JsonNode
    public JsonNode findValue(String fieldName) {
        for (JsonNode node : this._children) {
            JsonNode value = node.findValue(fieldName);
            if (value != null) {
                return value;
            }
        }
        return null;
    }

    @Override // com.fasterxml.jackson.databind.JsonNode
    public List<JsonNode> findValues(String fieldName, List<JsonNode> foundSoFar) {
        for (JsonNode node : this._children) {
            foundSoFar = node.findValues(fieldName, foundSoFar);
        }
        return foundSoFar;
    }

    @Override // com.fasterxml.jackson.databind.JsonNode
    public List<String> findValuesAsText(String fieldName, List<String> foundSoFar) {
        for (JsonNode node : this._children) {
            foundSoFar = node.findValuesAsText(fieldName, foundSoFar);
        }
        return foundSoFar;
    }

    @Override // com.fasterxml.jackson.databind.JsonNode
    public ObjectNode findParent(String fieldName) {
        for (JsonNode node : this._children) {
            JsonNode parent = node.findParent(fieldName);
            if (parent != null) {
                return (ObjectNode) parent;
            }
        }
        return null;
    }

    @Override // com.fasterxml.jackson.databind.JsonNode
    public List<JsonNode> findParents(String fieldName, List<JsonNode> foundSoFar) {
        for (JsonNode node : this._children) {
            foundSoFar = node.findParents(fieldName, foundSoFar);
        }
        return foundSoFar;
    }

    public JsonNode set(int index, JsonNode value) {
        if (value == null) {
            value = nullNode();
        }
        if (index < 0 || index >= this._children.size()) {
            throw new IndexOutOfBoundsException("Illegal index " + index + ", array size " + size());
        }
        return this._children.set(index, value);
    }

    public ArrayNode add(JsonNode value) {
        if (value == null) {
            value = nullNode();
        }
        _add(value);
        return this;
    }

    public ArrayNode addAll(ArrayNode other) {
        this._children.addAll(other._children);
        return this;
    }

    public ArrayNode addAll(Collection<? extends JsonNode> nodes) {
        for (JsonNode node : nodes) {
            add(node);
        }
        return this;
    }

    public ArrayNode insert(int index, JsonNode value) {
        if (value == null) {
            value = nullNode();
        }
        _insert(index, value);
        return this;
    }

    public JsonNode remove(int index) {
        if (index >= 0 && index < this._children.size()) {
            return this._children.remove(index);
        }
        return null;
    }

    /* JADX WARN: Can't rename method to resolve collision */
    @Override // com.fasterxml.jackson.databind.node.ContainerNode
    public ArrayNode removeAll() {
        this._children.clear();
        return this;
    }

    public ArrayNode addArray() {
        ArrayNode n = arrayNode();
        _add(n);
        return n;
    }

    public ObjectNode addObject() {
        ObjectNode n = objectNode();
        _add(n);
        return n;
    }

    public ArrayNode addPOJO(Object pojo) {
        return _add(pojo == null ? nullNode() : pojoNode(pojo));
    }

    public ArrayNode addRawValue(RawValue raw) {
        return _add(raw == null ? nullNode() : rawValueNode(raw));
    }

    public ArrayNode addNull() {
        return _add(nullNode());
    }

    public ArrayNode add(short v) {
        return _add(numberNode(v));
    }

    public ArrayNode add(Short v) {
        return _add(v == null ? nullNode() : numberNode(v.shortValue()));
    }

    public ArrayNode add(int v) {
        return _add(numberNode(v));
    }

    public ArrayNode add(Integer v) {
        return _add(v == null ? nullNode() : numberNode(v.intValue()));
    }

    public ArrayNode add(long v) {
        return _add(numberNode(v));
    }

    public ArrayNode add(Long v) {
        return _add(v == null ? nullNode() : numberNode(v.longValue()));
    }

    public ArrayNode add(float v) {
        return _add(numberNode(v));
    }

    public ArrayNode add(Float v) {
        return _add(v == null ? nullNode() : numberNode(v.floatValue()));
    }

    public ArrayNode add(double v) {
        return _add(numberNode(v));
    }

    public ArrayNode add(Double v) {
        return _add(v == null ? nullNode() : numberNode(v.doubleValue()));
    }

    public ArrayNode add(BigDecimal v) {
        return _add(v == null ? nullNode() : numberNode(v));
    }

    public ArrayNode add(BigInteger v) {
        return _add(v == null ? nullNode() : numberNode(v));
    }

    public ArrayNode add(String v) {
        return _add(v == null ? nullNode() : textNode(v));
    }

    public ArrayNode add(boolean v) {
        return _add(booleanNode(v));
    }

    public ArrayNode add(Boolean v) {
        return _add(v == null ? nullNode() : booleanNode(v.booleanValue()));
    }

    public ArrayNode add(byte[] v) {
        return _add(v == null ? nullNode() : binaryNode(v));
    }

    public ArrayNode insertArray(int index) {
        ArrayNode n = arrayNode();
        _insert(index, n);
        return n;
    }

    public ObjectNode insertObject(int index) {
        ObjectNode n = objectNode();
        _insert(index, n);
        return n;
    }

    public ArrayNode insertNull(int index) {
        return _insert(index, nullNode());
    }

    public ArrayNode insertPOJO(int index, Object pojo) {
        return _insert(index, pojo == null ? nullNode() : pojoNode(pojo));
    }

    public ArrayNode insertRawValue(int index, RawValue raw) {
        return _insert(index, raw == null ? nullNode() : rawValueNode(raw));
    }

    public ArrayNode insert(int index, short v) {
        return _insert(index, numberNode(v));
    }

    public ArrayNode insert(int index, Short value) {
        return _insert(index, value == null ? nullNode() : numberNode(value.shortValue()));
    }

    public ArrayNode insert(int index, int v) {
        return _insert(index, numberNode(v));
    }

    public ArrayNode insert(int index, Integer v) {
        return _insert(index, v == null ? nullNode() : numberNode(v.intValue()));
    }

    public ArrayNode insert(int index, long v) {
        return _insert(index, numberNode(v));
    }

    public ArrayNode insert(int index, Long v) {
        return _insert(index, v == null ? nullNode() : numberNode(v.longValue()));
    }

    public ArrayNode insert(int index, float v) {
        return _insert(index, numberNode(v));
    }

    public ArrayNode insert(int index, Float v) {
        return _insert(index, v == null ? nullNode() : numberNode(v.floatValue()));
    }

    public ArrayNode insert(int index, double v) {
        return _insert(index, numberNode(v));
    }

    public ArrayNode insert(int index, Double v) {
        return _insert(index, v == null ? nullNode() : numberNode(v.doubleValue()));
    }

    public ArrayNode insert(int index, BigDecimal v) {
        return _insert(index, v == null ? nullNode() : numberNode(v));
    }

    public ArrayNode insert(int index, BigInteger v) {
        return _insert(index, v == null ? nullNode() : numberNode(v));
    }

    public ArrayNode insert(int index, String v) {
        return _insert(index, v == null ? nullNode() : textNode(v));
    }

    public ArrayNode insert(int index, boolean v) {
        return _insert(index, booleanNode(v));
    }

    public ArrayNode insert(int index, Boolean value) {
        if (value == null) {
            return insertNull(index);
        }
        return _insert(index, booleanNode(value.booleanValue()));
    }

    public ArrayNode insert(int index, byte[] v) {
        if (v == null) {
            return insertNull(index);
        }
        return _insert(index, binaryNode(v));
    }

    public ArrayNode setNull(int index) {
        return _set(index, nullNode());
    }

    public ArrayNode setPOJO(int index, Object pojo) {
        return _set(index, pojo == null ? nullNode() : pojoNode(pojo));
    }

    public ArrayNode setRawValue(int index, RawValue raw) {
        return _set(index, raw == null ? nullNode() : rawValueNode(raw));
    }

    public ArrayNode set(int index, short v) {
        return _set(index, numberNode(v));
    }

    public ArrayNode set(int index, Short v) {
        return _set(index, v == null ? nullNode() : numberNode(v.shortValue()));
    }

    public ArrayNode set(int index, int v) {
        return _set(index, numberNode(v));
    }

    public ArrayNode set(int index, Integer v) {
        return _set(index, v == null ? nullNode() : numberNode(v.intValue()));
    }

    public ArrayNode set(int index, long v) {
        return _set(index, numberNode(v));
    }

    public ArrayNode set(int index, Long v) {
        return _set(index, v == null ? nullNode() : numberNode(v.longValue()));
    }

    public ArrayNode set(int index, float v) {
        return _set(index, numberNode(v));
    }

    public ArrayNode set(int index, Float v) {
        return _set(index, v == null ? nullNode() : numberNode(v.floatValue()));
    }

    public ArrayNode set(int index, double v) {
        return _set(index, numberNode(v));
    }

    public ArrayNode set(int index, Double v) {
        return _set(index, v == null ? nullNode() : numberNode(v.doubleValue()));
    }

    public ArrayNode set(int index, BigDecimal v) {
        return _set(index, v == null ? nullNode() : numberNode(v));
    }

    public ArrayNode set(int index, BigInteger v) {
        return _set(index, v == null ? nullNode() : numberNode(v));
    }

    public ArrayNode set(int index, String v) {
        return _set(index, v == null ? nullNode() : textNode(v));
    }

    public ArrayNode set(int index, boolean v) {
        return _set(index, booleanNode(v));
    }

    public ArrayNode set(int index, Boolean v) {
        return _set(index, v == null ? nullNode() : booleanNode(v.booleanValue()));
    }

    public ArrayNode set(int index, byte[] v) {
        return _set(index, v == null ? nullNode() : binaryNode(v));
    }

    @Override // com.fasterxml.jackson.databind.JsonNode
    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }
        if (o != null && (o instanceof ArrayNode)) {
            return this._children.equals(((ArrayNode) o)._children);
        }
        return false;
    }

    protected boolean _childrenEqual(ArrayNode other) {
        return this._children.equals(other._children);
    }

    @Override // com.fasterxml.jackson.databind.node.BaseJsonNode
    public int hashCode() {
        return this._children.hashCode();
    }

    protected ArrayNode _set(int index, JsonNode node) {
        if (index < 0 || index >= this._children.size()) {
            throw new IndexOutOfBoundsException("Illegal index " + index + ", array size " + size());
        }
        this._children.set(index, node);
        return this;
    }

    protected ArrayNode _add(JsonNode node) {
        this._children.add(node);
        return this;
    }

    protected ArrayNode _insert(int index, JsonNode node) {
        if (index < 0) {
            this._children.add(0, node);
        } else if (index >= this._children.size()) {
            this._children.add(node);
        } else {
            this._children.add(index, node);
        }
        return this;
    }
}
