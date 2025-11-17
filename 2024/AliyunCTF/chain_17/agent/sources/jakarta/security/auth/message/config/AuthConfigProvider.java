package jakarta.security.auth.message.config;

import jakarta.security.auth.message.AuthException;
import javax.security.auth.callback.CallbackHandler;

/* loaded from: agent.jar:BOOT-INF/lib/tomcat-embed-core-10.1.18.jar:jakarta/security/auth/message/config/AuthConfigProvider.class */
public interface AuthConfigProvider {
    ClientAuthConfig getClientAuthConfig(String str, String str2, CallbackHandler callbackHandler) throws AuthException;

    ServerAuthConfig getServerAuthConfig(String str, String str2, CallbackHandler callbackHandler) throws AuthException;

    void refresh();
}
