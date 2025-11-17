package org.apache.coyote;

import org.apache.coyote.http11.AbstractHttp11Protocol;
import org.apache.coyote.http11.upgrade.InternalHttpUpgradeHandler;
import org.apache.tomcat.util.net.SocketWrapperBase;

/* loaded from: agent.jar:BOOT-INF/lib/tomcat-embed-core-10.1.18.jar:org/apache/coyote/UpgradeProtocol.class */
public interface UpgradeProtocol {
    String getHttpUpgradeName(boolean z);

    byte[] getAlpnIdentifier();

    String getAlpnName();

    Processor getProcessor(SocketWrapperBase<?> socketWrapperBase, Adapter adapter);

    InternalHttpUpgradeHandler getInternalUpgradeHandler(SocketWrapperBase<?> socketWrapperBase, Adapter adapter, Request request);

    boolean accept(Request request);

    default void setHttp11Protocol(AbstractHttp11Protocol<?> protocol) {
    }
}
