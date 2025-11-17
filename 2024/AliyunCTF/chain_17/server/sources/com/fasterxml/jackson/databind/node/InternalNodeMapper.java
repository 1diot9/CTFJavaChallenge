package com.fasterxml.jackson.databind.node;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.JsonSerializable;
import com.fasterxml.jackson.databind.ObjectReader;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.json.JsonMapper;
import com.fasterxml.jackson.databind.jsontype.TypeSerializer;
import com.fasterxml.jackson.databind.ser.SerializerCache;
import java.io.IOException;
import java.util.Arrays;
import java.util.Iterator;
import java.util.Map;

/* JADX INFO: Access modifiers changed from: package-private */
/* loaded from: server.jar:BOOT-INF/lib/jackson-databind-2.15.3.jar:com/fasterxml/jackson/databind/node/InternalNodeMapper.class */
public final class InternalNodeMapper {
    private static final JsonMapper JSON_MAPPER = new JsonMapper();
    private static final ObjectWriter STD_WRITER = JSON_MAPPER.writer();
    private static final ObjectWriter PRETTY_WRITER = JSON_MAPPER.writer().withDefaultPrettyPrinter();
    private static final ObjectReader NODE_READER = JSON_MAPPER.readerFor(JsonNode.class);

    InternalNodeMapper() {
    }

    public static String nodeToString(BaseJsonNode n) {
        try {
            return STD_WRITER.writeValueAsString(_wrapper(n));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static String nodeToPrettyString(BaseJsonNode n) {
        try {
            return PRETTY_WRITER.writeValueAsString(_wrapper(n));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static byte[] valueToBytes(Object value) throws IOException {
        return JSON_MAPPER.writeValueAsBytes(value);
    }

    public static JsonNode bytesToNode(byte[] json) throws IOException {
        return (JsonNode) NODE_READER.readValue(json);
    }

    private static JsonSerializable _wrapper(BaseJsonNode root) {
        return new WrapperForSerializer(root);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    /* loaded from: server.jar:BOOT-INF/lib/jackson-databind-2.15.3.jar:com/fasterxml/jackson/databind/node/InternalNodeMapper$WrapperForSerializer.class */
    public static class WrapperForSerializer extends JsonSerializable.Base {
        protected final BaseJsonNode _root;
        protected SerializerProvider _context;

        public WrapperForSerializer(BaseJsonNode root) {
            this._root = root;
        }

        @Override // com.fasterxml.jackson.databind.JsonSerializable
        public void serialize(JsonGenerator g, SerializerProvider ctxt) throws IOException {
            this._context = ctxt;
            _serializeNonRecursive(g, this._root);
        }

        @Override // com.fasterxml.jackson.databind.JsonSerializable
        public void serializeWithType(JsonGenerator g, SerializerProvider ctxt, TypeSerializer typeSer) throws IOException {
            serialize(g, ctxt);
        }

        protected void _serializeNonRecursive(JsonGenerator g, JsonNode node) throws IOException {
            if (node instanceof ObjectNode) {
                g.writeStartObject(this, node.size());
                _serializeNonRecursive(g, new IteratorStack(), node.fields());
            } else if (node instanceof ArrayNode) {
                g.writeStartArray(this, node.size());
                _serializeNonRecursive(g, new IteratorStack(), node.elements());
            } else {
                node.serialize(g, this._context);
            }
        }

        protected void _serializeNonRecursive(JsonGenerator g, IteratorStack stack, Iterator<?> rootIterator) throws IOException {
            JsonNode value;
            Iterator<?> currIt = rootIterator;
            while (true) {
                if (currIt.hasNext()) {
                    Object elem = currIt.next();
                    if (elem instanceof Map.Entry) {
                        Map.Entry<String, JsonNode> en = (Map.Entry) elem;
                        g.writeFieldName(en.getKey());
                        value = en.getValue();
                    } else {
                        value = (JsonNode) elem;
                    }
                    if (value instanceof ObjectNode) {
                        stack.push(currIt);
                        currIt = value.fields();
                        g.writeStartObject(value, value.size());
                    } else if (value instanceof ArrayNode) {
                        stack.push(currIt);
                        currIt = value.elements();
                        g.writeStartArray(value, value.size());
                    } else if (value instanceof POJONode) {
                        try {
                            value.serialize(g, this._context);
                        } catch (IOException | RuntimeException e) {
                            g.writeString(String.format("[ERROR: (%s) %s]", e.getClass().getName(), e.getMessage()));
                        }
                    } else {
                        value.serialize(g, this._context);
                    }
                } else {
                    if (g.getOutputContext().inArray()) {
                        g.writeEndArray();
                    } else {
                        g.writeEndObject();
                    }
                    currIt = stack.popOrNull();
                    if (currIt == null) {
                        return;
                    }
                }
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* loaded from: server.jar:BOOT-INF/lib/jackson-databind-2.15.3.jar:com/fasterxml/jackson/databind/node/InternalNodeMapper$IteratorStack.class */
    public static final class IteratorStack {
        private Iterator<?>[] _stack;
        private int _top;
        private int _end;

        public void push(Iterator<?> it) {
            if (this._top < this._end) {
                Iterator<?>[] itArr = this._stack;
                int i = this._top;
                this._top = i + 1;
                itArr[i] = it;
                return;
            }
            if (this._stack == null) {
                this._end = 10;
                this._stack = new Iterator[this._end];
            } else {
                this._end += Math.min(SerializerCache.DEFAULT_MAX_CACHED, Math.max(20, this._end >> 1));
                this._stack = (Iterator[]) Arrays.copyOf(this._stack, this._end);
            }
            Iterator<?>[] itArr2 = this._stack;
            int i2 = this._top;
            this._top = i2 + 1;
            itArr2[i2] = it;
        }

        public Iterator<?> popOrNull() {
            if (this._top == 0) {
                return null;
            }
            Iterator<?>[] itArr = this._stack;
            int i = this._top - 1;
            this._top = i;
            return itArr[i];
        }
    }
}
