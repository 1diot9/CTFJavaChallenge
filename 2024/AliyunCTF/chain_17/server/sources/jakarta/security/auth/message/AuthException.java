package jakarta.security.auth.message;

import javax.security.auth.login.LoginException;

/* loaded from: server.jar:BOOT-INF/lib/tomcat-embed-core-10.1.18.jar:jakarta/security/auth/message/AuthException.class */
public class AuthException extends LoginException {
    private static final long serialVersionUID = -1156951780670243758L;

    public AuthException() {
    }

    public AuthException(String msg) {
        super(msg);
    }

    public AuthException(String msg, Throwable cause) {
        super(msg);
        initCause(cause);
    }

    public AuthException(Throwable cause) {
        initCause(cause);
    }
}
