package org.springframework.boot.json;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.List;
import java.util.Map;

/* loaded from: agent.jar:BOOT-INF/lib/spring-boot-3.2.2.jar:org/springframework/boot/json/JacksonJsonParser.class */
public class JacksonJsonParser extends AbstractJsonParser {
    private static final MapTypeReference MAP_TYPE = new MapTypeReference();
    private static final ListTypeReference LIST_TYPE = new ListTypeReference();
    private ObjectMapper objectMapper;

    public JacksonJsonParser(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    public JacksonJsonParser() {
    }

    @Override // org.springframework.boot.json.JsonParser
    public Map<String, Object> parseMap(String json) {
        return (Map) tryParse(() -> {
            return (Map) getObjectMapper().readValue(json, MAP_TYPE);
        }, Exception.class);
    }

    @Override // org.springframework.boot.json.JsonParser
    public List<Object> parseList(String json) {
        return (List) tryParse(() -> {
            return (List) getObjectMapper().readValue(json, LIST_TYPE);
        }, Exception.class);
    }

    private ObjectMapper getObjectMapper() {
        if (this.objectMapper == null) {
            this.objectMapper = new ObjectMapper();
        }
        return this.objectMapper;
    }

    /* loaded from: agent.jar:BOOT-INF/lib/spring-boot-3.2.2.jar:org/springframework/boot/json/JacksonJsonParser$MapTypeReference.class */
    private static final class MapTypeReference extends TypeReference<Map<String, Object>> {
        private MapTypeReference() {
        }
    }

    /* loaded from: agent.jar:BOOT-INF/lib/spring-boot-3.2.2.jar:org/springframework/boot/json/JacksonJsonParser$ListTypeReference.class */
    private static final class ListTypeReference extends TypeReference<List<Object>> {
        private ListTypeReference() {
        }
    }
}
