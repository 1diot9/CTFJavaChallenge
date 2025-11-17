package org.apache.coyote.http11;

import java.io.IOException;
import org.apache.coyote.OutputBuffer;

/* loaded from: agent.jar:BOOT-INF/lib/tomcat-embed-core-10.1.18.jar:org/apache/coyote/http11/HttpOutputBuffer.class */
public interface HttpOutputBuffer extends OutputBuffer {
    void end() throws IOException;

    void flush() throws IOException;
}
