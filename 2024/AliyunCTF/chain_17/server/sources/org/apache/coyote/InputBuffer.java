package org.apache.coyote;

import java.io.IOException;
import org.apache.tomcat.util.net.ApplicationBufferHandler;

/* loaded from: server.jar:BOOT-INF/lib/tomcat-embed-core-10.1.18.jar:org/apache/coyote/InputBuffer.class */
public interface InputBuffer {
    int doRead(ApplicationBufferHandler applicationBufferHandler) throws IOException;

    int available();
}
