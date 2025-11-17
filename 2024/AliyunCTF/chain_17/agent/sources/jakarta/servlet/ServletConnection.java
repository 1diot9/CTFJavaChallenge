package jakarta.servlet;

/* loaded from: agent.jar:BOOT-INF/lib/tomcat-embed-core-10.1.18.jar:jakarta/servlet/ServletConnection.class */
public interface ServletConnection {
    String getConnectionId();

    String getProtocol();

    String getProtocolConnectionId();

    boolean isSecure();
}
