package org.apache.coyote.http11.upgrade;

import jakarta.servlet.http.HttpUpgradeHandler;
import org.apache.tomcat.util.net.AbstractEndpoint;
import org.apache.tomcat.util.net.SSLSupport;
import org.apache.tomcat.util.net.SocketEvent;
import org.apache.tomcat.util.net.SocketWrapperBase;

/* loaded from: server.jar:BOOT-INF/lib/tomcat-embed-core-10.1.18.jar:org/apache/coyote/http11/upgrade/InternalHttpUpgradeHandler.class */
public interface InternalHttpUpgradeHandler extends HttpUpgradeHandler {
    AbstractEndpoint.Handler.SocketState upgradeDispatch(SocketEvent socketEvent);

    void timeoutAsync(long j);

    void setSocketWrapper(SocketWrapperBase<?> socketWrapperBase);

    void setSslSupport(SSLSupport sSLSupport);

    void pause();

    default boolean hasAsyncIO() {
        return false;
    }

    default UpgradeInfo getUpgradeInfo() {
        return null;
    }
}
