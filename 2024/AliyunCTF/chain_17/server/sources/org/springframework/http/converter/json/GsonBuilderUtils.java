package org.springframework.http.converter.json;

import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import java.lang.reflect.Type;
import java.util.Base64;

/* loaded from: server.jar:BOOT-INF/lib/spring-web-6.1.3.jar:org/springframework/http/converter/json/GsonBuilderUtils.class */
public abstract class GsonBuilderUtils {
    public static GsonBuilder gsonBuilderWithBase64EncodedByteArrays() {
        GsonBuilder builder = new GsonBuilder();
        builder.registerTypeHierarchyAdapter(byte[].class, new Base64TypeAdapter());
        return builder;
    }

    /* loaded from: server.jar:BOOT-INF/lib/spring-web-6.1.3.jar:org/springframework/http/converter/json/GsonBuilderUtils$Base64TypeAdapter.class */
    private static class Base64TypeAdapter implements JsonSerializer<byte[]>, JsonDeserializer<byte[]> {
        private Base64TypeAdapter() {
        }

        public JsonElement serialize(byte[] src, Type typeOfSrc, JsonSerializationContext context) {
            return new JsonPrimitive(Base64.getEncoder().encodeToString(src));
        }

        /* renamed from: deserialize, reason: merged with bridge method [inline-methods] */
        public byte[] m2618deserialize(JsonElement json, Type type, JsonDeserializationContext cxt) {
            return Base64.getDecoder().decode(json.getAsString());
        }
    }
}
