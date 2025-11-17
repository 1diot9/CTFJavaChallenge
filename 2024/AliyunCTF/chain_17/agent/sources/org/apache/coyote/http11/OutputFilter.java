package org.apache.coyote.http11;

import org.apache.coyote.Response;

/* loaded from: agent.jar:BOOT-INF/lib/tomcat-embed-core-10.1.18.jar:org/apache/coyote/http11/OutputFilter.class */
public interface OutputFilter extends HttpOutputBuffer {
    void setResponse(Response response);

    void recycle();

    void setBuffer(HttpOutputBuffer httpOutputBuffer);
}
