package jakarta.security.auth.message.config;

import jakarta.security.auth.message.AuthException;
import java.util.Map;
import javax.security.auth.Subject;

/* loaded from: server.jar:BOOT-INF/lib/tomcat-embed-core-10.1.18.jar:jakarta/security/auth/message/config/ServerAuthConfig.class */
public interface ServerAuthConfig extends AuthConfig {
    ServerAuthContext getAuthContext(String str, Subject subject, Map<String, Object> map) throws AuthException;
}
