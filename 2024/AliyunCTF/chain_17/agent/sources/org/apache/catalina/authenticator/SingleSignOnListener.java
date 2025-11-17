package org.apache.catalina.authenticator;

import java.io.Serializable;
import org.apache.catalina.Authenticator;
import org.apache.catalina.Context;
import org.apache.catalina.Manager;
import org.apache.catalina.Session;
import org.apache.catalina.SessionEvent;
import org.apache.catalina.SessionListener;

/* loaded from: agent.jar:BOOT-INF/lib/tomcat-embed-core-10.1.18.jar:org/apache/catalina/authenticator/SingleSignOnListener.class */
public class SingleSignOnListener implements SessionListener, Serializable {
    private static final long serialVersionUID = 1;
    private final String ssoId;

    public SingleSignOnListener(String ssoId) {
        this.ssoId = ssoId;
    }

    @Override // org.apache.catalina.SessionListener
    public void sessionEvent(SessionEvent event) {
        Session session;
        Manager manager;
        SingleSignOn sso;
        if (!Session.SESSION_DESTROYED_EVENT.equals(event.getType()) || (manager = (session = event.getSession()).getManager()) == null) {
            return;
        }
        Context context = manager.getContext();
        Authenticator authenticator = context.getAuthenticator();
        if (!(authenticator instanceof AuthenticatorBase) || (sso = ((AuthenticatorBase) authenticator).sso) == null) {
            return;
        }
        sso.sessionDestroyed(this.ssoId, session);
    }
}
