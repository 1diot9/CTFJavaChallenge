package org.springframework.http;

import java.io.IOException;
import java.io.OutputStream;

/* loaded from: agent.jar:BOOT-INF/lib/spring-web-6.1.3.jar:org/springframework/http/StreamingHttpOutputMessage.class */
public interface StreamingHttpOutputMessage extends HttpOutputMessage {
    void setBody(Body body);

    @FunctionalInterface
    /* loaded from: agent.jar:BOOT-INF/lib/spring-web-6.1.3.jar:org/springframework/http/StreamingHttpOutputMessage$Body.class */
    public interface Body {
        void writeTo(OutputStream outputStream) throws IOException;

        default boolean repeatable() {
            return false;
        }
    }
}
