package org.apache.coyote;

import java.io.IOException;
import java.nio.ByteBuffer;

/* loaded from: server.jar:BOOT-INF/lib/tomcat-embed-core-10.1.18.jar:org/apache/coyote/OutputBuffer.class */
public interface OutputBuffer {
    int doWrite(ByteBuffer byteBuffer) throws IOException;

    long getBytesWritten();
}
