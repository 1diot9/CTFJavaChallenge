package jakarta.servlet.http;

/* loaded from: server.jar:BOOT-INF/lib/tomcat-embed-core-10.1.18.jar:jakarta/servlet/http/HttpUpgradeHandler.class */
public interface HttpUpgradeHandler {
    void init(WebConnection webConnection);

    void destroy();
}
