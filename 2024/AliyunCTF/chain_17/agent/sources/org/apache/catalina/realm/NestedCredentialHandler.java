package org.apache.catalina.realm;

import java.util.ArrayList;
import java.util.List;
import org.apache.catalina.CredentialHandler;

/* loaded from: agent.jar:BOOT-INF/lib/tomcat-embed-core-10.1.18.jar:org/apache/catalina/realm/NestedCredentialHandler.class */
public class NestedCredentialHandler implements CredentialHandler {
    private final List<CredentialHandler> credentialHandlers = new ArrayList();

    @Override // org.apache.catalina.CredentialHandler
    public boolean matches(String inputCredentials, String storedCredentials) {
        for (CredentialHandler handler : this.credentialHandlers) {
            if (handler.matches(inputCredentials, storedCredentials)) {
                return true;
            }
        }
        return false;
    }

    @Override // org.apache.catalina.CredentialHandler
    public String mutate(String inputCredentials) {
        if (this.credentialHandlers.isEmpty()) {
            return null;
        }
        return this.credentialHandlers.get(0).mutate(inputCredentials);
    }

    public void addCredentialHandler(CredentialHandler handler) {
        this.credentialHandlers.add(handler);
    }

    public CredentialHandler[] getCredentialHandlers() {
        return (CredentialHandler[]) this.credentialHandlers.toArray(new CredentialHandler[0]);
    }
}
