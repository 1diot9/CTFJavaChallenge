package org.springframework.aot.generate;

/* loaded from: server.jar:BOOT-INF/lib/spring-core-6.1.3.jar:org/springframework/aot/generate/UnsupportedTypeValueCodeGenerationException.class */
public class UnsupportedTypeValueCodeGenerationException extends ValueCodeGenerationException {
    public UnsupportedTypeValueCodeGenerationException(Object value) {
        super("Code generation does not support " + value.getClass().getName(), value, null);
    }
}
