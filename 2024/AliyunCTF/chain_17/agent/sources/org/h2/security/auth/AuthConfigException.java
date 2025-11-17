package org.h2.security.auth;

/* loaded from: agent.jar:BOOT-INF/lib/h2-2.2.224.jar:org/h2/security/auth/AuthConfigException.class */
public class AuthConfigException extends RuntimeException {
    private static final long serialVersionUID = 1;

    public AuthConfigException() {
    }

    public AuthConfigException(String str) {
        super(str);
    }

    public AuthConfigException(Throwable th) {
        super(th);
    }

    public AuthConfigException(String str, Throwable th) {
        super(str, th);
    }
}
