package org.springframework.boot.json;

import org.springframework.util.ClassUtils;

/* loaded from: agent.jar:BOOT-INF/lib/spring-boot-3.2.2.jar:org/springframework/boot/json/JsonParserFactory.class */
public abstract class JsonParserFactory {
    public static JsonParser getJsonParser() {
        if (ClassUtils.isPresent("com.fasterxml.jackson.databind.ObjectMapper", null)) {
            return new JacksonJsonParser();
        }
        if (ClassUtils.isPresent("com.google.gson.Gson", null)) {
            return new GsonJsonParser();
        }
        return new BasicJsonParser();
    }
}
