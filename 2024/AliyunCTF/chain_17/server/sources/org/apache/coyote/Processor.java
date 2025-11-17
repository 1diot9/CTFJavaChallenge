package org.apache.coyote;

import java.io.IOException;
import java.nio.ByteBuffer;
import org.apache.tomcat.util.net.AbstractEndpoint;
import org.apache.tomcat.util.net.SSLSupport;
import org.apache.tomcat.util.net.SocketEvent;
import org.apache.tomcat.util.net.SocketWrapperBase;

/* loaded from: server.jar:BOOT-INF/lib/tomcat-embed-core-10.1.18.jar:org/apache/coyote/Processor.class */
public interface Processor {
    AbstractEndpoint.Handler.SocketState process(SocketWrapperBase<?> socketWrapperBase, SocketEvent socketEvent) throws IOException;

    UpgradeToken getUpgradeToken();

    boolean isUpgrade();

    boolean isAsync();

    void timeoutAsync(long j);

    Request getRequest();

    void recycle();

    void setSslSupport(SSLSupport sSLSupport);

    ByteBuffer getLeftoverInput();

    void pause();

    boolean checkAsyncTimeoutGeneration();
}
