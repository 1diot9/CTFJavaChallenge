package org.apache.catalina.authenticator.jaspic;

import jakarta.security.auth.message.AuthException;
import jakarta.security.auth.message.AuthStatus;
import jakarta.security.auth.message.MessageInfo;
import jakarta.security.auth.message.config.ServerAuthContext;
import jakarta.security.auth.message.module.ServerAuthModule;
import java.util.List;
import javax.security.auth.Subject;

/* loaded from: agent.jar:BOOT-INF/lib/tomcat-embed-core-10.1.18.jar:org/apache/catalina/authenticator/jaspic/SimpleServerAuthContext.class */
public class SimpleServerAuthContext implements ServerAuthContext {
    private final List<ServerAuthModule> modules;

    public SimpleServerAuthContext(List<ServerAuthModule> modules) {
        this.modules = modules;
    }

    @Override // jakarta.security.auth.message.ServerAuth
    public AuthStatus validateRequest(MessageInfo messageInfo, Subject clientSubject, Subject serviceSubject) throws AuthException {
        for (int moduleIndex = 0; moduleIndex < this.modules.size(); moduleIndex++) {
            ServerAuthModule module = this.modules.get(moduleIndex);
            AuthStatus result = module.validateRequest(messageInfo, clientSubject, serviceSubject);
            if (result != AuthStatus.SEND_FAILURE) {
                messageInfo.getMap().put("moduleIndex", Integer.valueOf(moduleIndex));
                return result;
            }
        }
        return AuthStatus.SEND_FAILURE;
    }

    @Override // jakarta.security.auth.message.ServerAuth
    public AuthStatus secureResponse(MessageInfo messageInfo, Subject serviceSubject) throws AuthException {
        ServerAuthModule module = this.modules.get(((Integer) messageInfo.getMap().get("moduleIndex")).intValue());
        return module.secureResponse(messageInfo, serviceSubject);
    }

    @Override // jakarta.security.auth.message.ServerAuth
    public void cleanSubject(MessageInfo messageInfo, Subject subject) throws AuthException {
        for (ServerAuthModule module : this.modules) {
            module.cleanSubject(messageInfo, subject);
        }
    }
}
