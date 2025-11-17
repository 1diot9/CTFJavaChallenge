package org.apache.coyote.http11.upgrade;

import jakarta.servlet.http.WebConnection;
import java.io.IOException;
import java.nio.ByteBuffer;
import org.apache.coyote.AbstractProcessorLight;
import org.apache.coyote.Request;
import org.apache.coyote.UpgradeToken;
import org.apache.tomcat.util.net.AbstractEndpoint;
import org.apache.tomcat.util.net.SocketWrapperBase;

/* loaded from: agent.jar:BOOT-INF/lib/tomcat-embed-core-10.1.18.jar:org/apache/coyote/http11/upgrade/UpgradeProcessorBase.class */
public abstract class UpgradeProcessorBase extends AbstractProcessorLight implements WebConnection {
    protected static final int INFINITE_TIMEOUT = -1;
    private final UpgradeToken upgradeToken;

    public UpgradeProcessorBase(UpgradeToken upgradeToken) {
        this.upgradeToken = upgradeToken;
    }

    @Override // org.apache.coyote.Processor
    public final boolean isUpgrade() {
        return true;
    }

    @Override // org.apache.coyote.Processor
    public UpgradeToken getUpgradeToken() {
        return this.upgradeToken;
    }

    @Override // org.apache.coyote.Processor
    public final void recycle() {
    }

    @Override // org.apache.coyote.AbstractProcessorLight
    public final AbstractEndpoint.Handler.SocketState service(SocketWrapperBase<?> socketWrapper) throws IOException {
        return null;
    }

    @Override // org.apache.coyote.AbstractProcessorLight
    public final AbstractEndpoint.Handler.SocketState asyncPostProcess() {
        return null;
    }

    @Override // org.apache.coyote.Processor
    public final boolean isAsync() {
        return false;
    }

    @Override // org.apache.coyote.Processor
    public final Request getRequest() {
        return null;
    }

    @Override // org.apache.coyote.Processor
    public ByteBuffer getLeftoverInput() {
        return null;
    }

    @Override // org.apache.coyote.Processor
    public boolean checkAsyncTimeoutGeneration() {
        return false;
    }

    @Override // org.apache.coyote.Processor
    public void timeoutAsync(long now) {
    }
}
