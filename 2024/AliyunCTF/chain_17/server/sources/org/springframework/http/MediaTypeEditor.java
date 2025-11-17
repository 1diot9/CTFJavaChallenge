package org.springframework.http;

import java.beans.PropertyEditorSupport;
import org.springframework.util.StringUtils;

/* loaded from: server.jar:BOOT-INF/lib/spring-web-6.1.3.jar:org/springframework/http/MediaTypeEditor.class */
public class MediaTypeEditor extends PropertyEditorSupport {
    public void setAsText(String text) {
        if (StringUtils.hasText(text)) {
            setValue(MediaType.parseMediaType(text));
        } else {
            setValue(null);
        }
    }

    public String getAsText() {
        MediaType mediaType = (MediaType) getValue();
        return mediaType != null ? mediaType.toString() : "";
    }
}
