package org.apache.catalina.session;

/* loaded from: agent.jar:BOOT-INF/lib/tomcat-embed-core-10.1.18.jar:org/apache/catalina/session/TooManyActiveSessionsException.class */
public class TooManyActiveSessionsException extends IllegalStateException {
    private static final long serialVersionUID = 1;
    private final int maxActiveSessions;

    public TooManyActiveSessionsException(String message, int maxActive) {
        super(message);
        this.maxActiveSessions = maxActive;
    }

    public int getMaxActiveSessions() {
        return this.maxActiveSessions;
    }
}
