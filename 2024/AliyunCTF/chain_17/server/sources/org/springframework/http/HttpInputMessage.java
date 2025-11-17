package org.springframework.http;

import java.io.IOException;
import java.io.InputStream;

/* loaded from: server.jar:BOOT-INF/lib/spring-web-6.1.3.jar:org/springframework/http/HttpInputMessage.class */
public interface HttpInputMessage extends HttpMessage {
    InputStream getBody() throws IOException;
}
