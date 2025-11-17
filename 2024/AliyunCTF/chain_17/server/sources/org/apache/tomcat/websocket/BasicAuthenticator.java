package org.apache.tomcat.websocket;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.Map;

/* loaded from: server.jar:BOOT-INF/lib/tomcat-embed-websocket-10.1.18.jar:org/apache/tomcat/websocket/BasicAuthenticator.class */
public class BasicAuthenticator extends Authenticator {
    public static final String schemeName = "basic";
    public static final String charsetparam = "charset";

    @Override // org.apache.tomcat.websocket.Authenticator
    public String getAuthorization(String requestUri, String authenticateHeader, String userName, String userPassword, String userRealm) throws AuthenticationException {
        Charset charset;
        validateUsername(userName);
        validatePassword(userPassword);
        Map<String, String> parameterMap = parseAuthenticateHeader(authenticateHeader);
        String realm = parameterMap.get("realm");
        validateRealm(userRealm, realm);
        String userPass = userName + ":" + userPassword;
        if (parameterMap.get(charsetparam) != null && parameterMap.get(charsetparam).equalsIgnoreCase("UTF-8")) {
            charset = StandardCharsets.UTF_8;
        } else {
            charset = StandardCharsets.ISO_8859_1;
        }
        String base64 = Base64.getEncoder().encodeToString(userPass.getBytes(charset));
        return " Basic " + base64;
    }

    @Override // org.apache.tomcat.websocket.Authenticator
    public String getSchemeName() {
        return schemeName;
    }
}
