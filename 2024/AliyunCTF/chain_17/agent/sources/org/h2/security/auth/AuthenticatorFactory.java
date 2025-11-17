package org.h2.security.auth;

/* loaded from: agent.jar:BOOT-INF/lib/h2-2.2.224.jar:org/h2/security/auth/AuthenticatorFactory.class */
public class AuthenticatorFactory {
    public static Authenticator createAuthenticator() {
        return DefaultAuthenticator.getInstance();
    }
}
