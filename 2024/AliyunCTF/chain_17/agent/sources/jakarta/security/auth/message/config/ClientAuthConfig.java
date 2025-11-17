package jakarta.security.auth.message.config;

import jakarta.security.auth.message.AuthException;
import java.util.Map;
import javax.security.auth.Subject;

/* loaded from: agent.jar:BOOT-INF/lib/tomcat-embed-core-10.1.18.jar:jakarta/security/auth/message/config/ClientAuthConfig.class */
public interface ClientAuthConfig extends AuthConfig {
    ClientAuthContext getAuthContext(String str, Subject subject, Map<String, Object> map) throws AuthException;
}
