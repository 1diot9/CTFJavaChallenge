package com.fasterxml.jackson.databind;

/* loaded from: agent.jar:BOOT-INF/lib/jackson-databind-2.15.3.jar:com/fasterxml/jackson/databind/RuntimeJsonMappingException.class */
public class RuntimeJsonMappingException extends RuntimeException {
    public RuntimeJsonMappingException(JsonMappingException cause) {
        super(cause);
    }

    public RuntimeJsonMappingException(String message) {
        super(message);
    }

    public RuntimeJsonMappingException(String message, JsonMappingException cause) {
        super(message, cause);
    }
}
