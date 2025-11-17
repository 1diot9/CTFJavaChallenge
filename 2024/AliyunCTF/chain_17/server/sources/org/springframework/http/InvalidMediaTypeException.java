package org.springframework.http;

import org.springframework.util.InvalidMimeTypeException;

/* loaded from: server.jar:BOOT-INF/lib/spring-web-6.1.3.jar:org/springframework/http/InvalidMediaTypeException.class */
public class InvalidMediaTypeException extends IllegalArgumentException {
    private final String mediaType;

    public InvalidMediaTypeException(String mediaType, String message) {
        super("Invalid media type \"" + mediaType + "\": " + message);
        this.mediaType = mediaType;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public InvalidMediaTypeException(InvalidMimeTypeException ex) {
        super(ex.getMessage(), ex);
        this.mediaType = ex.getMimeType();
    }

    public String getMediaType() {
        return this.mediaType;
    }
}
