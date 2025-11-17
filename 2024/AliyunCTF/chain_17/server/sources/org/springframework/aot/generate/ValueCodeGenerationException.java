package org.springframework.aot.generate;

import org.springframework.lang.Nullable;

/* loaded from: server.jar:BOOT-INF/lib/spring-core-6.1.3.jar:org/springframework/aot/generate/ValueCodeGenerationException.class */
public class ValueCodeGenerationException extends RuntimeException {

    @Nullable
    private final Object value;

    /* JADX INFO: Access modifiers changed from: protected */
    public ValueCodeGenerationException(String message, @Nullable Object value, @Nullable Throwable cause) {
        super(message, cause);
        this.value = value;
    }

    public ValueCodeGenerationException(@Nullable Object value, Throwable cause) {
        super(buildErrorMessage(value), cause);
        this.value = value;
    }

    private static String buildErrorMessage(@Nullable Object value) {
        StringBuilder message = new StringBuilder("Failed to generate code for '");
        message.append(value).append("'");
        if (value != null) {
            message.append(" with type ").append(value.getClass());
        }
        return message.toString();
    }

    @Nullable
    public Object getValue() {
        return this.value;
    }
}
