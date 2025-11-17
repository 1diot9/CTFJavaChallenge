package jakarta.security.auth.message;

import javax.security.auth.Subject;

/* loaded from: agent.jar:BOOT-INF/lib/tomcat-embed-core-10.1.18.jar:jakarta/security/auth/message/ServerAuth.class */
public interface ServerAuth {
    AuthStatus validateRequest(MessageInfo messageInfo, Subject subject, Subject subject2) throws AuthException;

    default AuthStatus secureResponse(MessageInfo messageInfo, Subject serviceSubject) throws AuthException {
        return AuthStatus.SUCCESS;
    }

    default void cleanSubject(MessageInfo messageInfo, Subject subject) throws AuthException {
    }
}
