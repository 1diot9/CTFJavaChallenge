package ch.qos.logback.core.status;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.io.PrintStream;

/* loaded from: agent.jar:BOOT-INF/lib/logback-core-1.4.14.jar:ch/qos/logback/core/status/OnFileStatusListener.class */
public class OnFileStatusListener extends OnPrintStreamStatusListenerBase {
    String filename;
    PrintStream ps;

    @Override // ch.qos.logback.core.status.OnPrintStreamStatusListenerBase, ch.qos.logback.core.spi.LifeCycle
    public void start() {
        if (this.filename == null) {
            addInfo("File option not set. Defaulting to \"status.txt\"");
            this.filename = "status.txt";
        }
        try {
            FileOutputStream fos = new FileOutputStream(this.filename, true);
            this.ps = new PrintStream((OutputStream) fos, true);
            super.start();
        } catch (FileNotFoundException e) {
            addError("Failed to open [" + this.filename + "]", e);
        }
    }

    @Override // ch.qos.logback.core.status.OnPrintStreamStatusListenerBase, ch.qos.logback.core.spi.LifeCycle
    public void stop() {
        if (!this.isStarted) {
            return;
        }
        if (this.ps != null) {
            this.ps.close();
        }
        super.stop();
    }

    public String getFilename() {
        return this.filename;
    }

    public void setFilename(String filename) {
        this.filename = filename;
    }

    @Override // ch.qos.logback.core.status.OnPrintStreamStatusListenerBase
    protected PrintStream getPrintStream() {
        return this.ps;
    }
}
