package org.springframework.web.accept;

import java.util.List;
import org.springframework.http.MediaType;

/* loaded from: agent.jar:BOOT-INF/lib/spring-web-6.1.3.jar:org/springframework/web/accept/MediaTypeFileExtensionResolver.class */
public interface MediaTypeFileExtensionResolver {
    List<String> resolveFileExtensions(MediaType mediaType);

    List<String> getAllFileExtensions();
}
