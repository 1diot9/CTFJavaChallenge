package jakarta.security.auth.message.module;

import jakarta.security.auth.message.AuthException;
import jakarta.security.auth.message.MessagePolicy;
import jakarta.security.auth.message.ServerAuth;
import java.util.Map;
import javax.security.auth.callback.CallbackHandler;

/* loaded from: server.jar:BOOT-INF/lib/tomcat-embed-core-10.1.18.jar:jakarta/security/auth/message/module/ServerAuthModule.class */
public interface ServerAuthModule extends ServerAuth {
    void initialize(MessagePolicy messagePolicy, MessagePolicy messagePolicy2, CallbackHandler callbackHandler, Map<String, Object> map) throws AuthException;

    Class<?>[] getSupportedMessageTypes();
}
