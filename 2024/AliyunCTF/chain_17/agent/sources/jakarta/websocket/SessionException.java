package jakarta.websocket;

/* loaded from: agent.jar:BOOT-INF/lib/tomcat-embed-websocket-10.1.18.jar:jakarta/websocket/SessionException.class */
public class SessionException extends Exception {
    private static final long serialVersionUID = 1;
    private final Session session;

    public SessionException(String message, Throwable cause, Session session) {
        super(message, cause);
        this.session = session;
    }

    public Session getSession() {
        return this.session;
    }
}
