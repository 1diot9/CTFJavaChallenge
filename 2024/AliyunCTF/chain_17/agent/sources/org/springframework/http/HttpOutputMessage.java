package org.springframework.http;

import java.io.IOException;
import java.io.OutputStream;

/* loaded from: agent.jar:BOOT-INF/lib/spring-web-6.1.3.jar:org/springframework/http/HttpOutputMessage.class */
public interface HttpOutputMessage extends HttpMessage {
    OutputStream getBody() throws IOException;
}
