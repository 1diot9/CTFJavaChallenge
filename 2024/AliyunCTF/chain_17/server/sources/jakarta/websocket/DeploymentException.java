package jakarta.websocket;

/* loaded from: server.jar:BOOT-INF/lib/tomcat-embed-websocket-10.1.18.jar:jakarta/websocket/DeploymentException.class */
public class DeploymentException extends Exception {
    private static final long serialVersionUID = 1;

    public DeploymentException(String message) {
        super(message);
    }

    public DeploymentException(String message, Throwable cause) {
        super(message, cause);
    }
}
