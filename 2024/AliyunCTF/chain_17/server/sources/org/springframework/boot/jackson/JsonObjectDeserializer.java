package org.springframework.boot.jackson;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.ObjectCodec;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.NullNode;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.function.Supplier;
import org.springframework.util.Assert;

/* loaded from: server.jar:BOOT-INF/lib/spring-boot-3.2.2.jar:org/springframework/boot/jackson/JsonObjectDeserializer.class */
public abstract class JsonObjectDeserializer<T> extends JsonDeserializer<T> {
    protected abstract T deserializeObject(JsonParser jsonParser, DeserializationContext context, ObjectCodec codec, JsonNode tree) throws IOException;

    @Override // com.fasterxml.jackson.databind.JsonDeserializer
    public final T deserialize(JsonParser jp, DeserializationContext ctxt) throws IOException {
        try {
            ObjectCodec codec = jp.getCodec();
            JsonNode tree = (JsonNode) codec.readTree(jp);
            return deserializeObject(jp, ctxt, codec, tree);
        } catch (Exception ex) {
            if (ex instanceof IOException) {
                IOException ioException = (IOException) ex;
                throw ioException;
            }
            throw new JsonMappingException(jp, "Object deserialize error", ex);
        }
    }

    protected final <D> D nullSafeValue(JsonNode jsonNode, Class<D> cls) {
        Assert.notNull(cls, "Type must not be null");
        if (jsonNode == null) {
            return null;
        }
        if (cls == String.class) {
            return (D) jsonNode.textValue();
        }
        if (cls == Boolean.class) {
            return (D) Boolean.valueOf(jsonNode.booleanValue());
        }
        if (cls == Long.class) {
            return (D) Long.valueOf(jsonNode.longValue());
        }
        if (cls == Integer.class) {
            return (D) Integer.valueOf(jsonNode.intValue());
        }
        if (cls == Short.class) {
            return (D) Short.valueOf(jsonNode.shortValue());
        }
        if (cls == Double.class) {
            return (D) Double.valueOf(jsonNode.doubleValue());
        }
        if (cls == Float.class) {
            return (D) Float.valueOf(jsonNode.floatValue());
        }
        if (cls == BigDecimal.class) {
            return (D) jsonNode.decimalValue();
        }
        if (cls == BigInteger.class) {
            return (D) jsonNode.bigIntegerValue();
        }
        throw new IllegalArgumentException("Unsupported value type " + cls.getName());
    }

    protected final JsonNode getRequiredNode(JsonNode tree, String fieldName) {
        Assert.notNull(tree, "Tree must not be null");
        JsonNode node = tree.get(fieldName);
        Assert.state((node == null || (node instanceof NullNode)) ? false : true, (Supplier<String>) () -> {
            return "Missing JSON field '" + fieldName + "'";
        });
        return node;
    }
}
