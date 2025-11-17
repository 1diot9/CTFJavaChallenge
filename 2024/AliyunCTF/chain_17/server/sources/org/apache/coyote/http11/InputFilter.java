package org.apache.coyote.http11;

import java.io.IOException;
import org.apache.coyote.InputBuffer;
import org.apache.coyote.Request;
import org.apache.tomcat.util.buf.ByteChunk;

/* loaded from: server.jar:BOOT-INF/lib/tomcat-embed-core-10.1.18.jar:org/apache/coyote/http11/InputFilter.class */
public interface InputFilter extends InputBuffer {
    void setRequest(Request request);

    void recycle();

    ByteChunk getEncodingName();

    void setBuffer(InputBuffer inputBuffer);

    long end() throws IOException;

    boolean isFinished();
}
