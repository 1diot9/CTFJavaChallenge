package jakarta.security.auth.message;

import javax.security.auth.Subject;

/* loaded from: server.jar:BOOT-INF/lib/tomcat-embed-core-10.1.18.jar:jakarta/security/auth/message/ClientAuth.class */
public interface ClientAuth {
    AuthStatus secureRequest(MessageInfo messageInfo, Subject subject) throws AuthException;

    default AuthStatus validateResponse(MessageInfo messageInfo, Subject clientSubject, Subject serviceSubject) throws AuthException {
        return AuthStatus.SUCCESS;
    }

    default void cleanSubject(MessageInfo messageInfo, Subject subject) throws AuthException {
    }
}
