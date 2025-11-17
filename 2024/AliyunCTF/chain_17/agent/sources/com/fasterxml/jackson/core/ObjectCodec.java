package com.fasterxml.jackson.core;

import com.fasterxml.jackson.core.type.ResolvedType;
import com.fasterxml.jackson.core.type.TypeReference;
import java.io.IOException;
import java.util.Iterator;

/* loaded from: agent.jar:BOOT-INF/lib/jackson-core-2.15.3.jar:com/fasterxml/jackson/core/ObjectCodec.class */
public abstract class ObjectCodec extends TreeCodec implements Versioned {
    @Override // com.fasterxml.jackson.core.Versioned
    public abstract Version version();

    public abstract <T> T readValue(JsonParser jsonParser, Class<T> cls) throws IOException;

    public abstract <T> T readValue(JsonParser jsonParser, TypeReference<T> typeReference) throws IOException;

    public abstract <T> T readValue(JsonParser jsonParser, ResolvedType resolvedType) throws IOException;

    public abstract <T> Iterator<T> readValues(JsonParser jsonParser, Class<T> cls) throws IOException;

    public abstract <T> Iterator<T> readValues(JsonParser jsonParser, TypeReference<T> typeReference) throws IOException;

    public abstract <T> Iterator<T> readValues(JsonParser jsonParser, ResolvedType resolvedType) throws IOException;

    public abstract void writeValue(JsonGenerator jsonGenerator, Object obj) throws IOException;

    @Override // com.fasterxml.jackson.core.TreeCodec
    public abstract <T extends TreeNode> T readTree(JsonParser jsonParser) throws IOException;

    @Override // com.fasterxml.jackson.core.TreeCodec
    public abstract void writeTree(JsonGenerator jsonGenerator, TreeNode treeNode) throws IOException;

    @Override // com.fasterxml.jackson.core.TreeCodec
    public abstract TreeNode createObjectNode();

    @Override // com.fasterxml.jackson.core.TreeCodec
    public abstract TreeNode createArrayNode();

    @Override // com.fasterxml.jackson.core.TreeCodec
    public abstract JsonParser treeAsTokens(TreeNode treeNode);

    public abstract <T> T treeToValue(TreeNode treeNode, Class<T> cls) throws JsonProcessingException;

    @Deprecated
    public JsonFactory getJsonFactory() {
        return getFactory();
    }

    public JsonFactory getFactory() {
        return getJsonFactory();
    }
}
