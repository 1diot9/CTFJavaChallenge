package ch.qos.logback.classic.spi;

import ch.qos.logback.classic.LoggerContext;
import java.io.Serializable;

/* loaded from: server.jar:BOOT-INF/lib/logback-classic-1.4.14.jar:ch/qos/logback/classic/spi/LoggerRemoteView.class */
public class LoggerRemoteView implements Serializable {
    private static final long serialVersionUID = 5028223666108713696L;
    final LoggerContextVO loggerContextView;
    final String name;
    static final /* synthetic */ boolean $assertionsDisabled;

    static {
        $assertionsDisabled = !LoggerRemoteView.class.desiredAssertionStatus();
    }

    public LoggerRemoteView(String name, LoggerContext lc) {
        this.name = name;
        if (!$assertionsDisabled && lc.getLoggerContextRemoteView() == null) {
            throw new AssertionError();
        }
        this.loggerContextView = lc.getLoggerContextRemoteView();
    }

    public LoggerContextVO getLoggerContextView() {
        return this.loggerContextView;
    }

    public String getName() {
        return this.name;
    }
}
