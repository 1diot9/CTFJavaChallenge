package com.fasterxml.jackson.databind.deser.std;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.deser.ResolvableDeserializer;
import com.fasterxml.jackson.databind.jsontype.TypeDeserializer;
import java.io.IOException;

/* loaded from: agent.jar:BOOT-INF/lib/jackson-databind-2.15.3.jar:com/fasterxml/jackson/databind/deser/std/StdNodeBasedDeserializer.class */
public abstract class StdNodeBasedDeserializer<T> extends StdDeserializer<T> implements ResolvableDeserializer {
    private static final long serialVersionUID = 1;
    protected JsonDeserializer<Object> _treeDeserializer;

    public abstract T convert(JsonNode jsonNode, DeserializationContext deserializationContext) throws IOException;

    protected StdNodeBasedDeserializer(JavaType targetType) {
        super(targetType);
    }

    protected StdNodeBasedDeserializer(Class<T> targetType) {
        super((Class<?>) targetType);
    }

    protected StdNodeBasedDeserializer(StdNodeBasedDeserializer<?> src) {
        super(src);
        this._treeDeserializer = src._treeDeserializer;
    }

    @Override // com.fasterxml.jackson.databind.deser.ResolvableDeserializer
    public void resolve(DeserializationContext ctxt) throws JsonMappingException {
        this._treeDeserializer = ctxt.findRootValueDeserializer(ctxt.constructType(JsonNode.class));
    }

    public T convert(JsonNode root, DeserializationContext ctxt, T newValue) throws IOException {
        ctxt.handleBadMerge(this);
        return convert(root, ctxt);
    }

    @Override // com.fasterxml.jackson.databind.JsonDeserializer
    public T deserialize(JsonParser jp, DeserializationContext ctxt) throws IOException {
        JsonNode n = (JsonNode) this._treeDeserializer.deserialize(jp, ctxt);
        return convert(n, ctxt);
    }

    @Override // com.fasterxml.jackson.databind.JsonDeserializer
    public T deserialize(JsonParser jp, DeserializationContext ctxt, T newValue) throws IOException {
        JsonNode n = (JsonNode) this._treeDeserializer.deserialize(jp, ctxt);
        return convert(n, ctxt, newValue);
    }

    @Override // com.fasterxml.jackson.databind.deser.std.StdDeserializer, com.fasterxml.jackson.databind.JsonDeserializer
    public Object deserializeWithType(JsonParser jp, DeserializationContext ctxt, TypeDeserializer td) throws IOException {
        JsonNode n = (JsonNode) this._treeDeserializer.deserializeWithType(jp, ctxt, td);
        return convert(n, ctxt);
    }
}
