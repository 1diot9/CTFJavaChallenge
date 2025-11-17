package com.fasterxml.jackson.databind.deser.std;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.databind.BeanProperty;
import com.fasterxml.jackson.databind.DeserializationConfig;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.deser.std.BaseNodeDeserializer;
import com.fasterxml.jackson.databind.jsontype.TypeDeserializer;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.databind.type.LogicalType;
import java.io.IOException;

/* loaded from: server.jar:BOOT-INF/lib/jackson-databind-2.15.3.jar:com/fasterxml/jackson/databind/deser/std/JsonNodeDeserializer.class */
public class JsonNodeDeserializer extends BaseNodeDeserializer<JsonNode> {
    private static final JsonNodeDeserializer instance = new JsonNodeDeserializer();

    @Override // com.fasterxml.jackson.databind.deser.std.BaseNodeDeserializer, com.fasterxml.jackson.databind.deser.ContextualDeserializer
    public /* bridge */ /* synthetic */ JsonDeserializer createContextual(DeserializationContext deserializationContext, BeanProperty beanProperty) throws JsonMappingException {
        return super.createContextual(deserializationContext, beanProperty);
    }

    @Override // com.fasterxml.jackson.databind.deser.std.BaseNodeDeserializer, com.fasterxml.jackson.databind.JsonDeserializer
    public /* bridge */ /* synthetic */ boolean isCachable() {
        return super.isCachable();
    }

    @Override // com.fasterxml.jackson.databind.deser.std.BaseNodeDeserializer, com.fasterxml.jackson.databind.JsonDeserializer
    public /* bridge */ /* synthetic */ LogicalType logicalType() {
        return super.logicalType();
    }

    @Override // com.fasterxml.jackson.databind.deser.std.BaseNodeDeserializer, com.fasterxml.jackson.databind.deser.std.StdDeserializer, com.fasterxml.jackson.databind.JsonDeserializer
    public /* bridge */ /* synthetic */ Object deserializeWithType(JsonParser jsonParser, DeserializationContext deserializationContext, TypeDeserializer typeDeserializer) throws IOException {
        return super.deserializeWithType(jsonParser, deserializationContext, typeDeserializer);
    }

    protected JsonNodeDeserializer() {
        super(JsonNode.class, null);
    }

    protected JsonNodeDeserializer(JsonNodeDeserializer base, boolean mergeArrays, boolean mergeObjects) {
        super(base, mergeArrays, mergeObjects);
    }

    @Override // com.fasterxml.jackson.databind.deser.std.BaseNodeDeserializer
    protected JsonDeserializer<?> _createWithMerge(boolean mergeArrays, boolean mergeObjects) {
        return new JsonNodeDeserializer(this, mergeArrays, mergeObjects);
    }

    public static JsonDeserializer<? extends JsonNode> getDeserializer(Class<?> nodeClass) {
        if (nodeClass == ObjectNode.class) {
            return ObjectDeserializer.getInstance();
        }
        if (nodeClass == ArrayNode.class) {
            return ArrayDeserializer.getInstance();
        }
        return instance;
    }

    @Override // com.fasterxml.jackson.databind.JsonDeserializer, com.fasterxml.jackson.databind.deser.NullValueProvider
    public JsonNode getNullValue(DeserializationContext ctxt) {
        return ctxt.getNodeFactory().nullNode();
    }

    @Override // com.fasterxml.jackson.databind.JsonDeserializer, com.fasterxml.jackson.databind.deser.NullValueProvider
    public Object getAbsentValue(DeserializationContext ctxt) {
        return null;
    }

    @Override // com.fasterxml.jackson.databind.JsonDeserializer
    public JsonNode deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
        BaseNodeDeserializer.ContainerStack stack = new BaseNodeDeserializer.ContainerStack();
        JsonNodeFactory nodeF = ctxt.getNodeFactory();
        switch (p.currentTokenId()) {
            case 1:
                return _deserializeContainerNoRecursion(p, ctxt, nodeF, stack, nodeF.objectNode());
            case 2:
                return nodeF.objectNode();
            case 3:
                return _deserializeContainerNoRecursion(p, ctxt, nodeF, stack, nodeF.arrayNode());
            case 4:
            default:
                return _deserializeAnyScalar(p, ctxt);
            case 5:
                return _deserializeObjectAtName(p, ctxt, nodeF, stack);
        }
    }

    @Override // com.fasterxml.jackson.databind.deser.std.BaseNodeDeserializer, com.fasterxml.jackson.databind.JsonDeserializer
    public Boolean supportsUpdate(DeserializationConfig config) {
        return this._supportsUpdates;
    }

    /* loaded from: server.jar:BOOT-INF/lib/jackson-databind-2.15.3.jar:com/fasterxml/jackson/databind/deser/std/JsonNodeDeserializer$ObjectDeserializer.class */
    static final class ObjectDeserializer extends BaseNodeDeserializer<ObjectNode> {
        private static final long serialVersionUID = 1;
        protected static final ObjectDeserializer _instance = new ObjectDeserializer();

        protected ObjectDeserializer() {
            super(ObjectNode.class, true);
        }

        public static ObjectDeserializer getInstance() {
            return _instance;
        }

        protected ObjectDeserializer(ObjectDeserializer base, boolean mergeArrays, boolean mergeObjects) {
            super(base, mergeArrays, mergeObjects);
        }

        @Override // com.fasterxml.jackson.databind.deser.std.BaseNodeDeserializer
        protected JsonDeserializer<?> _createWithMerge(boolean mergeArrays, boolean mergeObjects) {
            return new ObjectDeserializer(this, mergeArrays, mergeObjects);
        }

        @Override // com.fasterxml.jackson.databind.JsonDeserializer
        public ObjectNode deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
            JsonNodeFactory nodeF = ctxt.getNodeFactory();
            if (p.isExpectedStartObjectToken()) {
                ObjectNode root = nodeF.objectNode();
                _deserializeContainerNoRecursion(p, ctxt, nodeF, new BaseNodeDeserializer.ContainerStack(), root);
                return root;
            }
            if (p.hasToken(JsonToken.FIELD_NAME)) {
                return _deserializeObjectAtName(p, ctxt, nodeF, new BaseNodeDeserializer.ContainerStack());
            }
            if (p.hasToken(JsonToken.END_OBJECT)) {
                return nodeF.objectNode();
            }
            return (ObjectNode) ctxt.handleUnexpectedToken(ObjectNode.class, p);
        }

        @Override // com.fasterxml.jackson.databind.JsonDeserializer
        public ObjectNode deserialize(JsonParser p, DeserializationContext ctxt, ObjectNode node) throws IOException {
            if (p.isExpectedStartObjectToken() || p.hasToken(JsonToken.FIELD_NAME)) {
                return (ObjectNode) updateObject(p, ctxt, node, new BaseNodeDeserializer.ContainerStack());
            }
            return (ObjectNode) ctxt.handleUnexpectedToken(ObjectNode.class, p);
        }
    }

    /* loaded from: server.jar:BOOT-INF/lib/jackson-databind-2.15.3.jar:com/fasterxml/jackson/databind/deser/std/JsonNodeDeserializer$ArrayDeserializer.class */
    static final class ArrayDeserializer extends BaseNodeDeserializer<ArrayNode> {
        private static final long serialVersionUID = 1;
        protected static final ArrayDeserializer _instance = new ArrayDeserializer();

        protected ArrayDeserializer() {
            super(ArrayNode.class, true);
        }

        public static ArrayDeserializer getInstance() {
            return _instance;
        }

        protected ArrayDeserializer(ArrayDeserializer base, boolean mergeArrays, boolean mergeObjects) {
            super(base, mergeArrays, mergeObjects);
        }

        @Override // com.fasterxml.jackson.databind.deser.std.BaseNodeDeserializer
        protected JsonDeserializer<?> _createWithMerge(boolean mergeArrays, boolean mergeObjects) {
            return new ArrayDeserializer(this, mergeArrays, mergeObjects);
        }

        @Override // com.fasterxml.jackson.databind.JsonDeserializer
        public ArrayNode deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
            if (p.isExpectedStartArrayToken()) {
                JsonNodeFactory nodeF = ctxt.getNodeFactory();
                ArrayNode arrayNode = nodeF.arrayNode();
                _deserializeContainerNoRecursion(p, ctxt, nodeF, new BaseNodeDeserializer.ContainerStack(), arrayNode);
                return arrayNode;
            }
            return (ArrayNode) ctxt.handleUnexpectedToken(ArrayNode.class, p);
        }

        @Override // com.fasterxml.jackson.databind.JsonDeserializer
        public ArrayNode deserialize(JsonParser p, DeserializationContext ctxt, ArrayNode arrayNode) throws IOException {
            if (p.isExpectedStartArrayToken()) {
                _deserializeContainerNoRecursion(p, ctxt, ctxt.getNodeFactory(), new BaseNodeDeserializer.ContainerStack(), arrayNode);
                return arrayNode;
            }
            return (ArrayNode) ctxt.handleUnexpectedToken(ArrayNode.class, p);
        }
    }
}
