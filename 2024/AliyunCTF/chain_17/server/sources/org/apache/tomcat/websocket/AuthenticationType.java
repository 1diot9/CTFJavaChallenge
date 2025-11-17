package org.apache.tomcat.websocket;

/* loaded from: server.jar:BOOT-INF/lib/tomcat-embed-websocket-10.1.18.jar:org/apache/tomcat/websocket/AuthenticationType.class */
public enum AuthenticationType {
    WWW("Authorization", "WWW-Authenticate", Constants.WS_AUTHENTICATION_USER_NAME, Constants.WS_AUTHENTICATION_PASSWORD, Constants.WS_AUTHENTICATION_REALM),
    PROXY("Proxy-Authorization", "Proxy-Authenticate", Constants.WS_AUTHENTICATION_PROXY_USER_NAME, Constants.WS_AUTHENTICATION_PROXY_PASSWORD, Constants.WS_AUTHENTICATION_PROXY_REALM);

    private final String authorizationHeaderName;
    private final String authenticateHeaderName;
    private final String userNameProperty;
    private final String userPasswordProperty;
    private final String userRealmProperty;

    AuthenticationType(String authorizationHeaderName, String authenticateHeaderName, String userNameProperty, String userPasswordProperty, String userRealmProperty) {
        this.authorizationHeaderName = authorizationHeaderName;
        this.authenticateHeaderName = authenticateHeaderName;
        this.userNameProperty = userNameProperty;
        this.userPasswordProperty = userPasswordProperty;
        this.userRealmProperty = userRealmProperty;
    }

    public String getAuthorizationHeaderName() {
        return this.authorizationHeaderName;
    }

    public String getAuthenticateHeaderName() {
        return this.authenticateHeaderName;
    }

    public String getUserNameProperty() {
        return this.userNameProperty;
    }

    public String getUserPasswordProperty() {
        return this.userPasswordProperty;
    }

    public String getUserRealmProperty() {
        return this.userRealmProperty;
    }
}
