package org.h2.security.auth;

/* loaded from: agent.jar:BOOT-INF/lib/h2-2.2.224.jar:org/h2/security/auth/AuthenticationException.class */
public class AuthenticationException extends Exception {
    private static final long serialVersionUID = 1;

    public AuthenticationException() {
    }

    public AuthenticationException(String str) {
        super(str);
    }

    public AuthenticationException(Throwable th) {
        super(th);
    }

    public AuthenticationException(String str, Throwable th) {
        super(str, th);
    }
}
