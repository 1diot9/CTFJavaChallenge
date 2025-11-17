package ch.qos.logback.core.status;

import java.io.PrintStream;

/* loaded from: server.jar:BOOT-INF/lib/logback-core-1.4.14.jar:ch/qos/logback/core/status/OnErrorConsoleStatusListener.class */
public class OnErrorConsoleStatusListener extends OnPrintStreamStatusListenerBase {
    @Override // ch.qos.logback.core.status.OnPrintStreamStatusListenerBase
    protected PrintStream getPrintStream() {
        return System.err;
    }
}
