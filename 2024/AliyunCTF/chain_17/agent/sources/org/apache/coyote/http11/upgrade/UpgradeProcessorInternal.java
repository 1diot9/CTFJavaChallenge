package org.apache.coyote.http11.upgrade;

import jakarta.servlet.ServletInputStream;
import jakarta.servlet.ServletOutputStream;
import java.io.IOException;
import org.apache.coyote.UpgradeToken;
import org.apache.juli.logging.Log;
import org.apache.juli.logging.LogFactory;
import org.apache.tomcat.util.net.AbstractEndpoint;
import org.apache.tomcat.util.net.SSLSupport;
import org.apache.tomcat.util.net.SocketEvent;
import org.apache.tomcat.util.net.SocketWrapperBase;

/* loaded from: agent.jar:BOOT-INF/lib/tomcat-embed-core-10.1.18.jar:org/apache/coyote/http11/upgrade/UpgradeProcessorInternal.class */
public class UpgradeProcessorInternal extends UpgradeProcessorBase {
    private static final Log log = LogFactory.getLog((Class<?>) UpgradeProcessorInternal.class);
    private final InternalHttpUpgradeHandler internalHttpUpgradeHandler;

    public UpgradeProcessorInternal(SocketWrapperBase<?> wrapper, UpgradeToken upgradeToken, UpgradeGroupInfo upgradeGroupInfo) {
        super(upgradeToken);
        this.internalHttpUpgradeHandler = (InternalHttpUpgradeHandler) upgradeToken.getHttpUpgradeHandler();
        wrapper.setReadTimeout(-1L);
        wrapper.setWriteTimeout(-1L);
        this.internalHttpUpgradeHandler.setSocketWrapper(wrapper);
        UpgradeInfo upgradeInfo = this.internalHttpUpgradeHandler.getUpgradeInfo();
        if (upgradeInfo != null && upgradeGroupInfo != null) {
            upgradeInfo.setGroupInfo(upgradeGroupInfo);
        }
    }

    @Override // org.apache.coyote.AbstractProcessorLight
    public AbstractEndpoint.Handler.SocketState dispatch(SocketEvent status) {
        return this.internalHttpUpgradeHandler.upgradeDispatch(status);
    }

    @Override // org.apache.coyote.Processor
    public final void setSslSupport(SSLSupport sslSupport) {
        this.internalHttpUpgradeHandler.setSslSupport(sslSupport);
    }

    @Override // org.apache.coyote.Processor
    public void pause() {
        this.internalHttpUpgradeHandler.pause();
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // org.apache.coyote.AbstractProcessorLight
    public Log getLog() {
        return log;
    }

    @Override // org.apache.coyote.http11.upgrade.UpgradeProcessorBase, org.apache.coyote.Processor
    public void timeoutAsync(long now) {
        this.internalHttpUpgradeHandler.timeoutAsync(now);
    }

    public boolean hasAsyncIO() {
        return this.internalHttpUpgradeHandler.hasAsyncIO();
    }

    @Override // java.lang.AutoCloseable
    public void close() throws Exception {
        UpgradeInfo upgradeInfo = this.internalHttpUpgradeHandler.getUpgradeInfo();
        if (upgradeInfo != null) {
            upgradeInfo.setGroupInfo(null);
        }
        this.internalHttpUpgradeHandler.destroy();
    }

    @Override // jakarta.servlet.http.WebConnection
    public ServletInputStream getInputStream() throws IOException {
        return null;
    }

    @Override // jakarta.servlet.http.WebConnection
    public ServletOutputStream getOutputStream() throws IOException {
        return null;
    }
}
