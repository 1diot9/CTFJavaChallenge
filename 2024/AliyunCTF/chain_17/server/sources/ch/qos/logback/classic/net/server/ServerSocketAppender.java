package ch.qos.logback.classic.net.server;

import ch.qos.logback.classic.net.LoggingEventPreSerializationTransformer;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.net.server.AbstractServerSocketAppender;
import ch.qos.logback.core.spi.PreSerializationTransformer;

/* loaded from: server.jar:BOOT-INF/lib/logback-classic-1.4.14.jar:ch/qos/logback/classic/net/server/ServerSocketAppender.class */
public class ServerSocketAppender extends AbstractServerSocketAppender<ILoggingEvent> {
    private static final PreSerializationTransformer<ILoggingEvent> pst = new LoggingEventPreSerializationTransformer();
    private boolean includeCallerData;

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // ch.qos.logback.core.net.server.AbstractServerSocketAppender
    public void postProcessEvent(ILoggingEvent event) {
        if (isIncludeCallerData()) {
            event.getCallerData();
        }
    }

    @Override // ch.qos.logback.core.net.server.AbstractServerSocketAppender
    protected PreSerializationTransformer<ILoggingEvent> getPST() {
        return pst;
    }

    public boolean isIncludeCallerData() {
        return this.includeCallerData;
    }

    public void setIncludeCallerData(boolean includeCallerData) {
        this.includeCallerData = includeCallerData;
    }
}
