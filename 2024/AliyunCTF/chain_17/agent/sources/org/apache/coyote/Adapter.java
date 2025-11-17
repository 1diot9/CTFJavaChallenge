package org.apache.coyote;

import org.apache.tomcat.util.net.SocketEvent;

/* loaded from: agent.jar:BOOT-INF/lib/tomcat-embed-core-10.1.18.jar:org/apache/coyote/Adapter.class */
public interface Adapter {
    void service(Request request, Response response) throws Exception;

    boolean prepare(Request request, Response response) throws Exception;

    boolean asyncDispatch(Request request, Response response, SocketEvent socketEvent) throws Exception;

    void log(Request request, Response response, long j);

    void checkRecycled(Request request, Response response);

    String getDomain();
}
