package org.apache.tomcat.util.log;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

/* loaded from: agent.jar:BOOT-INF/lib/tomcat-embed-core-10.1.18.jar:org/apache/tomcat/util/log/CaptureLog.class */
class CaptureLog {
    private final ByteArrayOutputStream baos = new ByteArrayOutputStream();
    private final PrintStream ps = new PrintStream(this.baos);

    /* JADX INFO: Access modifiers changed from: protected */
    public PrintStream getStream() {
        return this.ps;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public void reset() {
        this.baos.reset();
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public String getCapture() {
        return this.baos.toString();
    }
}
