package ch.qos.logback.core.spi;

/* loaded from: agent.jar:BOOT-INF/lib/logback-core-1.4.14.jar:ch/qos/logback/core/spi/ScanException.class */
public class ScanException extends Exception {
    private static final long serialVersionUID = -3132040414328475658L;
    Throwable cause;

    public ScanException(String msg) {
        super(msg);
    }

    public ScanException(String msg, Throwable rootCause) {
        super(msg);
        this.cause = rootCause;
    }

    @Override // java.lang.Throwable
    public Throwable getCause() {
        return this.cause;
    }
}
