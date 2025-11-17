package org.springframework.util;

/* loaded from: server.jar:BOOT-INF/lib/spring-core-6.1.3.jar:org/springframework/util/InvalidMimeTypeException.class */
public class InvalidMimeTypeException extends IllegalArgumentException {
    private final String mimeType;

    public InvalidMimeTypeException(String mimeType, String message) {
        super("Invalid mime type \"" + mimeType + "\": " + message);
        this.mimeType = mimeType;
    }

    public String getMimeType() {
        return this.mimeType;
    }
}
