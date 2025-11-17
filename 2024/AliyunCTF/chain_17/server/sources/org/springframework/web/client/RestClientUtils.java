package org.springframework.web.client;

import java.io.IOException;
import java.nio.charset.Charset;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpInputMessage;
import org.springframework.http.HttpMessage;
import org.springframework.http.MediaType;
import org.springframework.lang.Nullable;
import org.springframework.util.FileCopyUtils;

/* JADX INFO: Access modifiers changed from: package-private */
/* loaded from: server.jar:BOOT-INF/lib/spring-web-6.1.3.jar:org/springframework/web/client/RestClientUtils.class */
public abstract class RestClientUtils {
    RestClientUtils() {
    }

    public static byte[] getBody(HttpInputMessage message) {
        try {
            return FileCopyUtils.copyToByteArray(message.getBody());
        } catch (IOException e) {
            return new byte[0];
        }
    }

    @Nullable
    public static Charset getCharset(HttpMessage response) {
        HttpHeaders headers = response.getHeaders();
        MediaType contentType = headers.getContentType();
        if (contentType != null) {
            return contentType.getCharset();
        }
        return null;
    }
}
