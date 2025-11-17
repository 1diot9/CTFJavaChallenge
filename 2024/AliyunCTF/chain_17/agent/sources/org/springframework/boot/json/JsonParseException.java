package org.springframework.boot.json;

/* loaded from: agent.jar:BOOT-INF/lib/spring-boot-3.2.2.jar:org/springframework/boot/json/JsonParseException.class */
public class JsonParseException extends IllegalArgumentException {
    public JsonParseException() {
        this(null);
    }

    public JsonParseException(Throwable cause) {
        super("Cannot parse JSON", cause);
    }
}
