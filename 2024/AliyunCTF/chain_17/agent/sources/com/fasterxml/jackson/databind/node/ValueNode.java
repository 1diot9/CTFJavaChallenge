package com.fasterxml.jackson.databind.node;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonPointer;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.core.type.WritableTypeId;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.jsontype.TypeSerializer;
import java.io.IOException;
import java.util.List;

/* loaded from: agent.jar:BOOT-INF/lib/jackson-databind-2.15.3.jar:com/fasterxml/jackson/databind/node/ValueNode.class */
public abstract class ValueNode extends BaseJsonNode {
    private static final long serialVersionUID = 1;

    @Override // com.fasterxml.jackson.databind.node.BaseJsonNode, com.fasterxml.jackson.core.TreeNode
    public abstract JsonToken asToken();

    @Override // com.fasterxml.jackson.databind.JsonNode
    protected JsonNode _at(JsonPointer ptr) {
        return null;
    }

    @Override // com.fasterxml.jackson.databind.JsonNode
    public <T extends JsonNode> T deepCopy() {
        return this;
    }

    @Override // com.fasterxml.jackson.databind.node.BaseJsonNode, com.fasterxml.jackson.databind.JsonSerializable
    public void serializeWithType(JsonGenerator g, SerializerProvider provider, TypeSerializer typeSer) throws IOException {
        WritableTypeId typeIdDef = typeSer.writeTypePrefix(g, typeSer.typeId(this, asToken()));
        serialize(g, provider);
        typeSer.writeTypeSuffix(g, typeIdDef);
    }

    @Override // com.fasterxml.jackson.databind.JsonNode
    public boolean isEmpty() {
        return true;
    }

    @Override // com.fasterxml.jackson.databind.JsonNode, com.fasterxml.jackson.core.TreeNode
    public final JsonNode get(int index) {
        return null;
    }

    @Override // com.fasterxml.jackson.databind.JsonNode, com.fasterxml.jackson.core.TreeNode
    public final JsonNode path(int index) {
        return MissingNode.getInstance();
    }

    @Override // com.fasterxml.jackson.databind.JsonNode
    public final boolean has(int index) {
        return false;
    }

    @Override // com.fasterxml.jackson.databind.JsonNode
    public final boolean hasNonNull(int index) {
        return false;
    }

    @Override // com.fasterxml.jackson.databind.JsonNode, com.fasterxml.jackson.core.TreeNode
    public final JsonNode get(String fieldName) {
        return null;
    }

    @Override // com.fasterxml.jackson.databind.JsonNode, com.fasterxml.jackson.core.TreeNode
    public final JsonNode path(String fieldName) {
        return MissingNode.getInstance();
    }

    @Override // com.fasterxml.jackson.databind.JsonNode
    public final boolean has(String fieldName) {
        return false;
    }

    @Override // com.fasterxml.jackson.databind.JsonNode
    public final boolean hasNonNull(String fieldName) {
        return false;
    }

    @Override // com.fasterxml.jackson.databind.JsonNode
    public final JsonNode findValue(String fieldName) {
        return null;
    }

    @Override // com.fasterxml.jackson.databind.JsonNode
    public final ObjectNode findParent(String fieldName) {
        return null;
    }

    @Override // com.fasterxml.jackson.databind.JsonNode
    public final List<JsonNode> findValues(String fieldName, List<JsonNode> foundSoFar) {
        return foundSoFar;
    }

    @Override // com.fasterxml.jackson.databind.JsonNode
    public final List<String> findValuesAsText(String fieldName, List<String> foundSoFar) {
        return foundSoFar;
    }

    @Override // com.fasterxml.jackson.databind.JsonNode
    public final List<JsonNode> findParents(String fieldName, List<JsonNode> foundSoFar) {
        return foundSoFar;
    }
}
