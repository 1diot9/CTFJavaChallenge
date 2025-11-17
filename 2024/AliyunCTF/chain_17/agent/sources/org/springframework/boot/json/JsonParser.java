package org.springframework.boot.json;

import java.util.List;
import java.util.Map;

/* loaded from: agent.jar:BOOT-INF/lib/spring-boot-3.2.2.jar:org/springframework/boot/json/JsonParser.class */
public interface JsonParser {
    Map<String, Object> parseMap(String json) throws JsonParseException;

    List<Object> parseList(String json) throws JsonParseException;
}
