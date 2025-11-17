package jakarta.websocket;

/* loaded from: agent.jar:BOOT-INF/lib/tomcat-embed-websocket-10.1.18.jar:jakarta/websocket/SendResult.class */
public final class SendResult {
    private final Throwable exception;
    private final boolean ok;

    public SendResult(Throwable exception) {
        this.exception = exception;
        this.ok = exception == null;
    }

    public SendResult() {
        this(null);
    }

    public Throwable getException() {
        return this.exception;
    }

    public boolean isOK() {
        return this.ok;
    }
}
