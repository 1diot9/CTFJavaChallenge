package org.apache.catalina.authenticator;

import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import org.apache.catalina.connector.Request;

/* loaded from: agent.jar:BOOT-INF/lib/tomcat-embed-core-10.1.18.jar:org/apache/catalina/authenticator/NonLoginAuthenticator.class */
public final class NonLoginAuthenticator extends AuthenticatorBase {
    @Override // org.apache.catalina.authenticator.AuthenticatorBase
    protected boolean doAuthenticate(Request request, HttpServletResponse response) throws IOException {
        if (checkForCachedAuthentication(request, response, true)) {
            if (this.cache) {
                request.getSessionInternal(true).setPrincipal(request.getPrincipal());
                return true;
            }
            return true;
        }
        if (this.containerLog.isDebugEnabled()) {
            this.containerLog.debug("User authenticated without any roles");
            return true;
        }
        return true;
    }

    @Override // org.apache.catalina.authenticator.AuthenticatorBase
    protected String getAuthMethod() {
        return "NONE";
    }
}
