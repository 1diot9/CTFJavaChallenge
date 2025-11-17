package org.apache.catalina.authenticator;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import java.io.IOException;
import java.security.Principal;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import org.apache.catalina.Container;
import org.apache.catalina.Context;
import org.apache.catalina.Engine;
import org.apache.catalina.LifecycleException;
import org.apache.catalina.Manager;
import org.apache.catalina.Realm;
import org.apache.catalina.Session;
import org.apache.catalina.SessionListener;
import org.apache.catalina.connector.Request;
import org.apache.catalina.connector.Response;
import org.apache.catalina.valves.ValveBase;
import org.apache.juli.logging.Log;
import org.apache.tomcat.util.res.StringManager;

/* loaded from: agent.jar:BOOT-INF/lib/tomcat-embed-core-10.1.18.jar:org/apache/catalina/authenticator/SingleSignOn.class */
public class SingleSignOn extends ValveBase {
    private static final StringManager sm = StringManager.getManager((Class<?>) SingleSignOn.class);
    private Engine engine;
    protected Map<String, SingleSignOnEntry> cache;
    private boolean requireReauthentication;
    private String cookieDomain;
    private String cookieName;

    public SingleSignOn() {
        super(true);
        this.cache = new ConcurrentHashMap();
        this.requireReauthentication = false;
        this.cookieName = Constants.SINGLE_SIGN_ON_COOKIE;
    }

    public String getCookieDomain() {
        return this.cookieDomain;
    }

    public void setCookieDomain(String cookieDomain) {
        if (cookieDomain != null && cookieDomain.trim().length() == 0) {
            this.cookieDomain = null;
        } else {
            this.cookieDomain = cookieDomain;
        }
    }

    public String getCookieName() {
        return this.cookieName;
    }

    public void setCookieName(String cookieName) {
        this.cookieName = cookieName;
    }

    public boolean getRequireReauthentication() {
        return this.requireReauthentication;
    }

    public void setRequireReauthentication(boolean required) {
        this.requireReauthentication = required;
    }

    @Override // org.apache.catalina.Valve
    public void invoke(Request request, Response response) throws IOException, ServletException {
        request.removeNote(Constants.REQ_SSOID_NOTE);
        if (this.containerLog.isDebugEnabled()) {
            this.containerLog.debug(sm.getString("singleSignOn.debug.invoke", request.getRequestURI()));
        }
        if (request.getUserPrincipal() != null) {
            if (this.containerLog.isDebugEnabled()) {
                this.containerLog.debug(sm.getString("singleSignOn.debug.hasPrincipal", request.getUserPrincipal().getName()));
            }
            getNext().invoke(request, response);
            return;
        }
        if (this.containerLog.isDebugEnabled()) {
            this.containerLog.debug(sm.getString("singleSignOn.debug.cookieCheck"));
        }
        Cookie cookie = null;
        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            int length = cookies.length;
            int i = 0;
            while (true) {
                if (i >= length) {
                    break;
                }
                Cookie value = cookies[i];
                if (!this.cookieName.equals(value.getName())) {
                    i++;
                } else {
                    cookie = value;
                    break;
                }
            }
        }
        if (cookie == null) {
            if (this.containerLog.isDebugEnabled()) {
                this.containerLog.debug(sm.getString("singleSignOn.debug.cookieNotFound"));
            }
            getNext().invoke(request, response);
            return;
        }
        if (this.containerLog.isDebugEnabled()) {
            this.containerLog.debug(sm.getString("singleSignOn.debug.principalCheck", cookie.getValue()));
        }
        SingleSignOnEntry entry = this.cache.get(cookie.getValue());
        if (entry != null) {
            if (this.containerLog.isDebugEnabled()) {
                Log log = this.containerLog;
                StringManager stringManager = sm;
                Object[] objArr = new Object[2];
                objArr[0] = entry.getPrincipal() != null ? entry.getPrincipal().getName() : "";
                objArr[1] = entry.getAuthType();
                log.debug(stringManager.getString("singleSignOn.debug.principalFound", objArr));
            }
            request.setNote(Constants.REQ_SSOID_NOTE, cookie.getValue());
            if (!getRequireReauthentication()) {
                request.setAuthType(entry.getAuthType());
                request.setUserPrincipal(entry.getPrincipal());
            }
        } else {
            if (this.containerLog.isDebugEnabled()) {
                this.containerLog.debug(sm.getString("singleSignOn.debug.principalNotFound", cookie.getValue()));
            }
            cookie.setValue("REMOVE");
            cookie.setMaxAge(0);
            cookie.setPath("/");
            String domain = getCookieDomain();
            if (domain != null) {
                cookie.setDomain(domain);
            }
            cookie.setSecure(request.isSecure());
            if (request.getServletContext().getSessionCookieConfig().isHttpOnly() || request.getContext().getUseHttpOnly()) {
                cookie.setHttpOnly(true);
            }
            cookie.setAttribute("Partitioned", Boolean.toString(request.getContext().getUsePartitioned()));
            response.addCookie(cookie);
        }
        getNext().invoke(request, response);
    }

    public void sessionDestroyed(String ssoId, Session session) {
        if (!getState().isAvailable()) {
            return;
        }
        if ((session.getMaxInactiveInterval() > 0 && session.getIdleTimeInternal() >= session.getMaxInactiveInterval() * 1000) || !session.getManager().getContext().getState().isAvailable()) {
            if (this.containerLog.isDebugEnabled()) {
                this.containerLog.debug(sm.getString("singleSignOn.debug.sessionTimeout", ssoId, session));
            }
            removeSession(ssoId, session);
        } else {
            if (this.containerLog.isDebugEnabled()) {
                this.containerLog.debug(sm.getString("singleSignOn.debug.sessionLogout", ssoId, session));
            }
            removeSession(ssoId, session);
            if (this.cache.containsKey(ssoId)) {
                deregister(ssoId);
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public boolean associate(String ssoId, Session session) {
        SingleSignOnEntry sso = this.cache.get(ssoId);
        if (sso == null) {
            if (this.containerLog.isDebugEnabled()) {
                this.containerLog.debug(sm.getString("singleSignOn.debug.associateFail", ssoId, session));
                return false;
            }
            return false;
        }
        if (this.containerLog.isDebugEnabled()) {
            this.containerLog.debug(sm.getString("singleSignOn.debug.associate", ssoId, session));
        }
        sso.addSession(this, ssoId, session);
        return true;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public void deregister(String ssoId) {
        SingleSignOnEntry sso = this.cache.remove(ssoId);
        if (sso == null) {
            if (this.containerLog.isDebugEnabled()) {
                this.containerLog.debug(sm.getString("singleSignOn.debug.deregisterFail", ssoId));
                return;
            }
            return;
        }
        Set<SingleSignOnSessionKey> ssoKeys = sso.findSessions();
        if (ssoKeys.size() == 0 && this.containerLog.isDebugEnabled()) {
            this.containerLog.debug(sm.getString("singleSignOn.debug.deregisterNone", ssoId));
        }
        for (SingleSignOnSessionKey ssoKey : ssoKeys) {
            if (this.containerLog.isDebugEnabled()) {
                this.containerLog.debug(sm.getString("singleSignOn.debug.deregister", ssoKey, ssoId));
            }
            expire(ssoKey);
        }
    }

    private void expire(SingleSignOnSessionKey key) {
        if (this.engine == null) {
            this.containerLog.warn(sm.getString("singleSignOn.sessionExpire.engineNull", key));
            return;
        }
        Container host = this.engine.findChild(key.getHostName());
        if (host == null) {
            this.containerLog.warn(sm.getString("singleSignOn.sessionExpire.hostNotFound", key));
            return;
        }
        Context context = (Context) host.findChild(key.getContextName());
        if (context == null) {
            this.containerLog.warn(sm.getString("singleSignOn.sessionExpire.contextNotFound", key));
            return;
        }
        Manager manager = context.getManager();
        if (manager == null) {
            this.containerLog.warn(sm.getString("singleSignOn.sessionExpire.managerNotFound", key));
            return;
        }
        try {
            Session session = manager.findSession(key.getSessionId());
            if (session == null) {
                this.containerLog.warn(sm.getString("singleSignOn.sessionExpire.sessionNotFound", key));
            } else {
                session.expire();
            }
        } catch (IOException e) {
            this.containerLog.warn(sm.getString("singleSignOn.sessionExpire.managerError", key), e);
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public boolean reauthenticate(String ssoId, Realm realm, Request request) {
        String username;
        Principal reauthPrincipal;
        if (ssoId == null || realm == null) {
            return false;
        }
        boolean reauthenticated = false;
        SingleSignOnEntry entry = this.cache.get(ssoId);
        if (entry != null && entry.getCanReauthenticate() && (username = entry.getUsername()) != null && (reauthPrincipal = realm.authenticate(username, entry.getPassword())) != null) {
            reauthenticated = true;
            request.setAuthType(entry.getAuthType());
            request.setUserPrincipal(reauthPrincipal);
        }
        return reauthenticated;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public void register(String ssoId, Principal principal, String authType, String username, String password) {
        if (this.containerLog.isDebugEnabled()) {
            Log log = this.containerLog;
            StringManager stringManager = sm;
            Object[] objArr = new Object[3];
            objArr[0] = ssoId;
            objArr[1] = principal != null ? principal.getName() : "";
            objArr[2] = authType;
            log.debug(stringManager.getString("singleSignOn.debug.register", objArr));
        }
        this.cache.put(ssoId, new SingleSignOnEntry(principal, authType, username, password));
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public boolean update(String ssoId, Principal principal, String authType, String username, String password) {
        SingleSignOnEntry sso = this.cache.get(ssoId);
        if (sso != null && !sso.getCanReauthenticate()) {
            if (this.containerLog.isDebugEnabled()) {
                this.containerLog.debug(sm.getString("singleSignOn.debug.update", ssoId, authType));
            }
            sso.updateCredentials(principal, authType, username, password);
            return true;
        }
        return false;
    }

    protected void removeSession(String ssoId, Session session) {
        if (this.containerLog.isDebugEnabled()) {
            this.containerLog.debug(sm.getString("singleSignOn.debug.removeSession", session, ssoId));
        }
        SingleSignOnEntry entry = this.cache.get(ssoId);
        if (entry == null) {
            return;
        }
        entry.removeSession(session);
        if (entry.findSessions().size() == 0) {
            deregister(ssoId);
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public SessionListener getSessionListener(String ssoId) {
        return new SingleSignOnListener(ssoId);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // org.apache.catalina.valves.ValveBase, org.apache.catalina.util.LifecycleBase
    public synchronized void startInternal() throws LifecycleException {
        Container c;
        Container container = getContainer();
        while (true) {
            c = container;
            if (c == null || (c instanceof Engine)) {
                break;
            } else {
                container = c.getParent();
            }
        }
        if (c != null) {
            this.engine = (Engine) c;
        }
        super.startInternal();
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // org.apache.catalina.valves.ValveBase, org.apache.catalina.util.LifecycleBase
    public synchronized void stopInternal() throws LifecycleException {
        super.stopInternal();
        this.engine = null;
    }
}
