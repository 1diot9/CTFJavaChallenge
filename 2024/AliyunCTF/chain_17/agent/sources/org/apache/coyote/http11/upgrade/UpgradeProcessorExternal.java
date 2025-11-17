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
import org.apache.tomcat.util.res.StringManager;

/* loaded from: agent.jar:BOOT-INF/lib/tomcat-embed-core-10.1.18.jar:org/apache/coyote/http11/upgrade/UpgradeProcessorExternal.class */
public class UpgradeProcessorExternal extends UpgradeProcessorBase {
    private static final Log log = LogFactory.getLog((Class<?>) UpgradeProcessorExternal.class);
    private static final StringManager sm = StringManager.getManager((Class<?>) UpgradeProcessorExternal.class);
    private final UpgradeServletInputStream upgradeServletInputStream;
    private final UpgradeServletOutputStream upgradeServletOutputStream;
    private final UpgradeInfo upgradeInfo;

    public UpgradeProcessorExternal(SocketWrapperBase<?> wrapper, UpgradeToken upgradeToken, UpgradeGroupInfo upgradeGroupInfo) {
        super(upgradeToken);
        this.upgradeInfo = new UpgradeInfo();
        if (upgradeGroupInfo != null) {
            upgradeGroupInfo.addUpgradeInfo(this.upgradeInfo);
        }
        this.upgradeServletInputStream = new UpgradeServletInputStream(this, wrapper, this.upgradeInfo);
        this.upgradeServletOutputStream = new UpgradeServletOutputStream(this, wrapper, this.upgradeInfo);
        wrapper.setReadTimeout(-1L);
        wrapper.setWriteTimeout(-1L);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // org.apache.coyote.AbstractProcessorLight
    public Log getLog() {
        return log;
    }

    @Override // java.lang.AutoCloseable
    public void close() throws Exception {
        this.upgradeServletInputStream.close();
        this.upgradeServletOutputStream.close();
        this.upgradeInfo.setGroupInfo(null);
    }

    @Override // jakarta.servlet.http.WebConnection
    public ServletInputStream getInputStream() throws IOException {
        return this.upgradeServletInputStream;
    }

    @Override // jakarta.servlet.http.WebConnection
    public ServletOutputStream getOutputStream() throws IOException {
        return this.upgradeServletOutputStream;
    }

    @Override // org.apache.coyote.AbstractProcessorLight
    public final AbstractEndpoint.Handler.SocketState dispatch(SocketEvent status) {
        if (status == SocketEvent.OPEN_READ) {
            this.upgradeServletInputStream.onDataAvailable();
        } else if (status == SocketEvent.OPEN_WRITE) {
            this.upgradeServletOutputStream.onWritePossible();
        } else {
            if (status == SocketEvent.STOP) {
                if (log.isDebugEnabled()) {
                    log.debug(sm.getString("upgradeProcessor.stop"));
                }
                try {
                    this.upgradeServletInputStream.close();
                } catch (IOException ioe) {
                    log.debug(sm.getString("upgradeProcessor.isCloseFail", ioe));
                }
                try {
                    this.upgradeServletOutputStream.close();
                } catch (IOException ioe2) {
                    log.debug(sm.getString("upgradeProcessor.osCloseFail", ioe2));
                }
                return AbstractEndpoint.Handler.SocketState.CLOSED;
            }
            if (log.isDebugEnabled()) {
                log.debug(sm.getString("upgradeProcessor.unexpectedState"));
            }
            return AbstractEndpoint.Handler.SocketState.CLOSED;
        }
        if (this.upgradeServletInputStream.isClosed() && this.upgradeServletOutputStream.isClosed()) {
            if (log.isDebugEnabled()) {
                log.debug(sm.getString("upgradeProcessor.requiredClose", Boolean.valueOf(this.upgradeServletInputStream.isClosed()), Boolean.valueOf(this.upgradeServletOutputStream.isClosed())));
            }
            return AbstractEndpoint.Handler.SocketState.CLOSED;
        }
        return AbstractEndpoint.Handler.SocketState.UPGRADED;
    }

    @Override // org.apache.coyote.Processor
    public final void setSslSupport(SSLSupport sslSupport) {
    }

    @Override // org.apache.coyote.Processor
    public void pause() {
    }
}
