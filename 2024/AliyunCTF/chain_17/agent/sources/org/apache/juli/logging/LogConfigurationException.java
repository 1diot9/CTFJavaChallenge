package org.apache.juli.logging;

/* loaded from: agent.jar:BOOT-INF/lib/tomcat-embed-core-10.1.18.jar:org/apache/juli/logging/LogConfigurationException.class */
public class LogConfigurationException extends RuntimeException {
    private static final long serialVersionUID = 1;

    public LogConfigurationException() {
    }

    public LogConfigurationException(String message) {
        super(message);
    }

    public LogConfigurationException(Throwable cause) {
        super(cause);
    }

    public LogConfigurationException(String message, Throwable cause) {
        super(message, cause);
    }
}
