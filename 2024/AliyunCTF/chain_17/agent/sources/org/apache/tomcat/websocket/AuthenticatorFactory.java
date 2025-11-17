package org.apache.tomcat.websocket;

import java.util.Iterator;
import java.util.ServiceLoader;

/* loaded from: agent.jar:BOOT-INF/lib/tomcat-embed-websocket-10.1.18.jar:org/apache/tomcat/websocket/AuthenticatorFactory.class */
public class AuthenticatorFactory {
    public static Authenticator getAuthenticator(String authScheme) {
        Authenticator auth;
        String lowerCase = authScheme.toLowerCase();
        boolean z = -1;
        switch (lowerCase.hashCode()) {
            case -1331913276:
                if (lowerCase.equals(DigestAuthenticator.schemeName)) {
                    z = true;
                    break;
                }
                break;
            case 93508654:
                if (lowerCase.equals(BasicAuthenticator.schemeName)) {
                    z = false;
                    break;
                }
                break;
        }
        switch (z) {
            case false:
                auth = new BasicAuthenticator();
                break;
            case true:
                auth = new DigestAuthenticator();
                break;
            default:
                auth = loadAuthenticators(authScheme);
                break;
        }
        return auth;
    }

    private static Authenticator loadAuthenticators(String authScheme) {
        ServiceLoader<Authenticator> serviceLoader = ServiceLoader.load(Authenticator.class);
        Iterator<Authenticator> it = serviceLoader.iterator();
        while (it.hasNext()) {
            Authenticator auth = it.next();
            if (auth.getSchemeName().equalsIgnoreCase(authScheme)) {
                return auth;
            }
        }
        return null;
    }
}
