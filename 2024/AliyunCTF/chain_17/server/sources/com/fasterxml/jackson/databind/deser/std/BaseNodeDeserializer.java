package com.fasterxml.jackson.databind.deser.std;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.core.StreamReadCapability;
import com.fasterxml.jackson.databind.BeanProperty;
import com.fasterxml.jackson.databind.DeserializationConfig;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.cfg.DatatypeFeatures;
import com.fasterxml.jackson.databind.cfg.JsonNodeFeature;
import com.fasterxml.jackson.databind.deser.ContextualDeserializer;
import com.fasterxml.jackson.databind.jsontype.TypeDeserializer;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ContainerNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.databind.ser.SerializerCache;
import com.fasterxml.jackson.databind.type.LogicalType;
import com.fasterxml.jackson.databind.util.RawValue;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.Arrays;

/* compiled from: JsonNodeDeserializer.java */
/* loaded from: server.jar:BOOT-INF/lib/jackson-databind-2.15.3.jar:com/fasterxml/jackson/databind/deser/std/BaseNodeDeserializer.class */
abstract class BaseNodeDeserializer<T extends JsonNode> extends StdDeserializer<T> implements ContextualDeserializer {
    protected final Boolean _supportsUpdates;
    protected final boolean _mergeArrays;
    protected final boolean _mergeObjects;

    protected abstract JsonDeserializer<?> _createWithMerge(boolean z, boolean z2);

    public BaseNodeDeserializer(Class<T> vc, Boolean supportsUpdates) {
        super((Class<?>) vc);
        this._supportsUpdates = supportsUpdates;
        this._mergeArrays = true;
        this._mergeObjects = true;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public BaseNodeDeserializer(BaseNodeDeserializer<?> base, boolean mergeArrays, boolean mergeObjects) {
        super(base);
        this._supportsUpdates = base._supportsUpdates;
        this._mergeArrays = mergeArrays;
        this._mergeObjects = mergeObjects;
    }

    @Override // com.fasterxml.jackson.databind.deser.std.StdDeserializer, com.fasterxml.jackson.databind.JsonDeserializer
    public Object deserializeWithType(JsonParser p, DeserializationContext ctxt, TypeDeserializer typeDeserializer) throws IOException {
        return typeDeserializer.deserializeTypedFromAny(p, ctxt);
    }

    @Override // com.fasterxml.jackson.databind.JsonDeserializer
    public LogicalType logicalType() {
        return LogicalType.Untyped;
    }

    @Override // com.fasterxml.jackson.databind.JsonDeserializer
    public boolean isCachable() {
        return true;
    }

    @Override // com.fasterxml.jackson.databind.JsonDeserializer
    public Boolean supportsUpdate(DeserializationConfig config) {
        return this._supportsUpdates;
    }

    @Override // com.fasterxml.jackson.databind.deser.ContextualDeserializer
    public JsonDeserializer<?> createContextual(DeserializationContext ctxt, BeanProperty property) throws JsonMappingException {
        DeserializationConfig cfg = ctxt.getConfig();
        Boolean mergeArr = cfg.getDefaultMergeable(ArrayNode.class);
        Boolean mergeObj = cfg.getDefaultMergeable(ObjectNode.class);
        Boolean mergeNode = cfg.getDefaultMergeable(JsonNode.class);
        boolean mergeArrays = _shouldMerge(mergeArr, mergeNode);
        boolean mergeObjects = _shouldMerge(mergeObj, mergeNode);
        if (mergeArrays != this._mergeArrays || mergeObjects != this._mergeObjects) {
            return _createWithMerge(mergeArrays, mergeObjects);
        }
        return this;
    }

    private static boolean _shouldMerge(Boolean specificMerge, Boolean generalMerge) {
        if (specificMerge != null) {
            return specificMerge.booleanValue();
        }
        if (generalMerge != null) {
            return generalMerge.booleanValue();
        }
        return true;
    }

    protected void _handleDuplicateField(JsonParser p, DeserializationContext ctxt, JsonNodeFactory nodeFactory, String fieldName, ObjectNode objectNode, JsonNode oldValue, JsonNode newValue) throws IOException {
        if (ctxt.isEnabled(DeserializationFeature.FAIL_ON_READING_DUP_TREE_KEY)) {
            ctxt.reportInputMismatch(JsonNode.class, "Duplicate field '%s' for `ObjectNode`: not allowed when `DeserializationFeature.FAIL_ON_READING_DUP_TREE_KEY` enabled", fieldName);
        }
        if (ctxt.isEnabled(StreamReadCapability.DUPLICATE_PROPERTIES)) {
            if (oldValue.isArray()) {
                ((ArrayNode) oldValue).add(newValue);
                objectNode.replace(fieldName, oldValue);
            } else {
                ArrayNode arr = nodeFactory.arrayNode();
                arr.add(oldValue);
                arr.add(newValue);
                objectNode.replace(fieldName, arr);
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public final ObjectNode _deserializeObjectAtName(JsonParser p, DeserializationContext ctxt, JsonNodeFactory nodeFactory, ContainerStack stack) throws IOException {
        JsonNode value;
        ObjectNode node = nodeFactory.objectNode();
        String currentName = p.currentName();
        while (true) {
            String key = currentName;
            if (key != null) {
                JsonToken t = p.nextToken();
                if (t == null) {
                    t = JsonToken.NOT_AVAILABLE;
                }
                switch (t.id()) {
                    case 1:
                        value = _deserializeContainerNoRecursion(p, ctxt, nodeFactory, stack, nodeFactory.objectNode());
                        break;
                    case 3:
                        value = _deserializeContainerNoRecursion(p, ctxt, nodeFactory, stack, nodeFactory.arrayNode());
                        break;
                    default:
                        value = _deserializeAnyScalar(p, ctxt);
                        break;
                }
                JsonNode old = node.replace(key, value);
                if (old != null) {
                    _handleDuplicateField(p, ctxt, nodeFactory, key, node, old, value);
                }
                currentName = p.nextFieldName();
            } else {
                return node;
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    /* JADX WARN: Multi-variable type inference failed */
    public final JsonNode updateObject(JsonParser p, DeserializationContext ctxt, ObjectNode node, ContainerStack stack) throws IOException {
        String key;
        JsonNode value;
        if (p.isExpectedStartObjectToken()) {
            key = p.nextFieldName();
        } else {
            if (!p.hasToken(JsonToken.FIELD_NAME)) {
                return (JsonNode) deserialize(p, ctxt);
            }
            key = p.currentName();
        }
        JsonNodeFactory nodeFactory = ctxt.getNodeFactory();
        while (key != null) {
            JsonToken t = p.nextToken();
            JsonNode old = node.get(key);
            if (old != null) {
                if (old instanceof ObjectNode) {
                    if (t == JsonToken.START_OBJECT && this._mergeObjects) {
                        JsonNode newValue = updateObject(p, ctxt, (ObjectNode) old, stack);
                        if (newValue != old) {
                            node.set(key, newValue);
                        }
                    }
                } else if ((old instanceof ArrayNode) && t == JsonToken.START_ARRAY && this._mergeArrays) {
                    _deserializeContainerNoRecursion(p, ctxt, nodeFactory, stack, (ArrayNode) old);
                }
                key = p.nextFieldName();
            }
            if (t == null) {
                t = JsonToken.NOT_AVAILABLE;
            }
            switch (t.id()) {
                case 1:
                    value = _deserializeContainerNoRecursion(p, ctxt, nodeFactory, stack, nodeFactory.objectNode());
                    break;
                case 2:
                case 4:
                case 5:
                case 8:
                default:
                    value = _deserializeRareScalar(p, ctxt);
                    break;
                case 3:
                    value = _deserializeContainerNoRecursion(p, ctxt, nodeFactory, stack, nodeFactory.arrayNode());
                    break;
                case 6:
                    value = nodeFactory.textNode(p.getText());
                    break;
                case 7:
                    value = _fromInt(p, ctxt, nodeFactory);
                    break;
                case 9:
                    value = nodeFactory.booleanNode(true);
                    break;
                case 10:
                    value = nodeFactory.booleanNode(false);
                    break;
                case 11:
                    if (!ctxt.isEnabled(JsonNodeFeature.READ_NULL_PROPERTIES)) {
                        break;
                    } else {
                        value = nodeFactory.nullNode();
                        break;
                    }
            }
            node.set(key, value);
            key = p.nextFieldName();
        }
        return node;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    /* JADX WARN: Failed to find 'out' block for switch in B:11:0x003d. Please report as an issue. */
    /* JADX WARN: Failed to find 'out' block for switch in B:52:0x0180. Please report as an issue. */
    /* JADX WARN: Removed duplicated region for block: B:21:0x0149  */
    /* JADX WARN: Removed duplicated region for block: B:23:0x0158 A[SYNTHETIC] */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct code enable 'Show inconsistent code' option in preferences
    */
    public final com.fasterxml.jackson.databind.node.ContainerNode<?> _deserializeContainerNoRecursion(com.fasterxml.jackson.core.JsonParser r10, com.fasterxml.jackson.databind.DeserializationContext r11, com.fasterxml.jackson.databind.node.JsonNodeFactory r12, com.fasterxml.jackson.databind.deser.std.BaseNodeDeserializer.ContainerStack r13, com.fasterxml.jackson.databind.node.ContainerNode<?> r14) throws java.io.IOException {
        /*
            Method dump skipped, instructions count: 616
            To view this dump change 'Code comments level' option to 'DEBUG'
        */
        throw new UnsupportedOperationException("Method not decompiled: com.fasterxml.jackson.databind.deser.std.BaseNodeDeserializer._deserializeContainerNoRecursion(com.fasterxml.jackson.core.JsonParser, com.fasterxml.jackson.databind.DeserializationContext, com.fasterxml.jackson.databind.node.JsonNodeFactory, com.fasterxml.jackson.databind.deser.std.BaseNodeDeserializer$ContainerStack, com.fasterxml.jackson.databind.node.ContainerNode):com.fasterxml.jackson.databind.node.ContainerNode");
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public final JsonNode _deserializeAnyScalar(JsonParser p, DeserializationContext ctxt) throws IOException {
        JsonNodeFactory nodeF = ctxt.getNodeFactory();
        switch (p.currentTokenId()) {
            case 2:
                return nodeF.objectNode();
            case 3:
            case 4:
            case 5:
            default:
                return (JsonNode) ctxt.handleUnexpectedToken(handledType(), p);
            case 6:
                return nodeF.textNode(p.getText());
            case 7:
                return _fromInt(p, ctxt, nodeF);
            case 8:
                return _fromFloat(p, ctxt, nodeF);
            case 9:
                return nodeF.booleanNode(true);
            case 10:
                return nodeF.booleanNode(false);
            case 11:
                return nodeF.nullNode();
            case 12:
                return _fromEmbedded(p, ctxt);
        }
    }

    protected final JsonNode _deserializeRareScalar(JsonParser p, DeserializationContext ctxt) throws IOException {
        switch (p.currentTokenId()) {
            case 2:
                return ctxt.getNodeFactory().objectNode();
            case 8:
                return _fromFloat(p, ctxt, ctxt.getNodeFactory());
            case 12:
                return _fromEmbedded(p, ctxt);
            default:
                return (JsonNode) ctxt.handleUnexpectedToken(handledType(), p);
        }
    }

    protected final JsonNode _fromInt(JsonParser p, int coercionFeatures, JsonNodeFactory nodeFactory) throws IOException {
        if (coercionFeatures != 0) {
            if (DeserializationFeature.USE_BIG_INTEGER_FOR_INTS.enabledIn(coercionFeatures)) {
                return nodeFactory.numberNode(p.getBigIntegerValue());
            }
            return nodeFactory.numberNode(p.getLongValue());
        }
        JsonParser.NumberType nt = p.getNumberType();
        if (nt == JsonParser.NumberType.INT) {
            return nodeFactory.numberNode(p.getIntValue());
        }
        if (nt == JsonParser.NumberType.LONG) {
            return nodeFactory.numberNode(p.getLongValue());
        }
        return nodeFactory.numberNode(p.getBigIntegerValue());
    }

    protected final JsonNode _fromInt(JsonParser p, DeserializationContext ctxt, JsonNodeFactory nodeFactory) throws IOException {
        JsonParser.NumberType nt;
        int feats = ctxt.getDeserializationFeatures();
        if ((feats & F_MASK_INT_COERCIONS) != 0) {
            if (DeserializationFeature.USE_BIG_INTEGER_FOR_INTS.enabledIn(feats)) {
                nt = JsonParser.NumberType.BIG_INTEGER;
            } else if (DeserializationFeature.USE_LONG_FOR_INTS.enabledIn(feats)) {
                nt = JsonParser.NumberType.LONG;
            } else {
                nt = p.getNumberType();
            }
        } else {
            nt = p.getNumberType();
        }
        if (nt == JsonParser.NumberType.INT) {
            return nodeFactory.numberNode(p.getIntValue());
        }
        if (nt == JsonParser.NumberType.LONG) {
            return nodeFactory.numberNode(p.getLongValue());
        }
        return nodeFactory.numberNode(p.getBigIntegerValue());
    }

    protected final JsonNode _fromFloat(JsonParser p, DeserializationContext ctxt, JsonNodeFactory nodeFactory) throws IOException {
        JsonParser.NumberType nt = p.getNumberType();
        if (nt == JsonParser.NumberType.BIG_DECIMAL) {
            return _fromBigDecimal(ctxt, nodeFactory, p.getDecimalValue());
        }
        if (ctxt.isEnabled(DeserializationFeature.USE_BIG_DECIMAL_FOR_FLOATS)) {
            if (p.isNaN()) {
                return nodeFactory.numberNode(p.getDoubleValue());
            }
            return _fromBigDecimal(ctxt, nodeFactory, p.getDecimalValue());
        }
        if (nt == JsonParser.NumberType.FLOAT) {
            return nodeFactory.numberNode(p.getFloatValue());
        }
        return nodeFactory.numberNode(p.getDoubleValue());
    }

    protected final JsonNode _fromBigDecimal(DeserializationContext ctxt, JsonNodeFactory nodeFactory, BigDecimal bigDec) {
        boolean normalize;
        DatatypeFeatures dtf = ctxt.getDatatypeFeatures();
        if (dtf.isExplicitlySet(JsonNodeFeature.STRIP_TRAILING_BIGDECIMAL_ZEROES)) {
            normalize = dtf.isEnabled(JsonNodeFeature.STRIP_TRAILING_BIGDECIMAL_ZEROES);
        } else {
            normalize = nodeFactory.willStripTrailingBigDecimalZeroes();
        }
        if (normalize) {
            try {
                bigDec = bigDec.stripTrailingZeros();
            } catch (ArithmeticException e) {
            }
        }
        return nodeFactory.numberNode(bigDec);
    }

    protected final JsonNode _fromEmbedded(JsonParser p, DeserializationContext ctxt) throws IOException {
        JsonNodeFactory nodeF = ctxt.getNodeFactory();
        Object ob = p.getEmbeddedObject();
        if (ob == null) {
            return nodeF.nullNode();
        }
        Class<?> type = ob.getClass();
        if (type == byte[].class) {
            return nodeF.binaryNode((byte[]) ob);
        }
        if (ob instanceof RawValue) {
            return nodeF.rawValueNode((RawValue) ob);
        }
        if (ob instanceof JsonNode) {
            return (JsonNode) ob;
        }
        return nodeF.pojoNode(ob);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* compiled from: JsonNodeDeserializer.java */
    /* loaded from: server.jar:BOOT-INF/lib/jackson-databind-2.15.3.jar:com/fasterxml/jackson/databind/deser/std/BaseNodeDeserializer$ContainerStack.class */
    public static final class ContainerStack {
        private ContainerNode[] _stack;
        private int _top;
        private int _end;

        public int size() {
            return this._top;
        }

        public void push(ContainerNode node) {
            if (this._top < this._end) {
                ContainerNode[] containerNodeArr = this._stack;
                int i = this._top;
                this._top = i + 1;
                containerNodeArr[i] = node;
                return;
            }
            if (this._stack == null) {
                this._end = 10;
                this._stack = new ContainerNode[this._end];
            } else {
                this._end += Math.min(SerializerCache.DEFAULT_MAX_CACHED, Math.max(20, this._end >> 1));
                this._stack = (ContainerNode[]) Arrays.copyOf(this._stack, this._end);
            }
            ContainerNode[] containerNodeArr2 = this._stack;
            int i2 = this._top;
            this._top = i2 + 1;
            containerNodeArr2[i2] = node;
        }

        public ContainerNode popOrNull() {
            if (this._top == 0) {
                return null;
            }
            ContainerNode[] containerNodeArr = this._stack;
            int i = this._top - 1;
            this._top = i;
            return containerNodeArr[i];
        }
    }
}
