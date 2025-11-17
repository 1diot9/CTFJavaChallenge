package org.apache.catalina.manager;

import jakarta.servlet.http.HttpSession;
import java.security.Principal;
import java.util.Iterator;
import org.apache.catalina.Manager;
import org.apache.catalina.Session;
import org.apache.catalina.SessionListener;

/* loaded from: server.jar:BOOT-INF/lib/tomcat-embed-core-10.1.18.jar:org/apache/catalina/manager/DummyProxySession.class */
public class DummyProxySession implements Session {
    private String sessionId;

    public DummyProxySession(String sessionId) {
        this.sessionId = sessionId;
    }

    @Override // org.apache.catalina.Session
    public void access() {
    }

    @Override // org.apache.catalina.Session
    public void addSessionListener(SessionListener listener) {
    }

    @Override // org.apache.catalina.Session
    public void endAccess() {
    }

    @Override // org.apache.catalina.Session
    public void expire() {
    }

    @Override // org.apache.catalina.Session
    public String getAuthType() {
        return null;
    }

    @Override // org.apache.catalina.Session
    public long getCreationTime() {
        return 0L;
    }

    @Override // org.apache.catalina.Session
    public long getCreationTimeInternal() {
        return 0L;
    }

    @Override // org.apache.catalina.Session
    public String getId() {
        return this.sessionId;
    }

    @Override // org.apache.catalina.Session
    public String getIdInternal() {
        return this.sessionId;
    }

    @Override // org.apache.catalina.Session
    public long getLastAccessedTime() {
        return 0L;
    }

    @Override // org.apache.catalina.Session
    public long getLastAccessedTimeInternal() {
        return 0L;
    }

    @Override // org.apache.catalina.Session
    public long getIdleTime() {
        return 0L;
    }

    @Override // org.apache.catalina.Session
    public long getIdleTimeInternal() {
        return 0L;
    }

    @Override // org.apache.catalina.Session
    public Manager getManager() {
        return null;
    }

    @Override // org.apache.catalina.Session
    public int getMaxInactiveInterval() {
        return 0;
    }

    @Override // org.apache.catalina.Session
    public Object getNote(String name) {
        return null;
    }

    @Override // org.apache.catalina.Session
    public Iterator<String> getNoteNames() {
        return null;
    }

    @Override // org.apache.catalina.Session
    public Principal getPrincipal() {
        return null;
    }

    @Override // org.apache.catalina.Session
    public HttpSession getSession() {
        return null;
    }

    @Override // org.apache.catalina.Session
    public long getThisAccessedTime() {
        return 0L;
    }

    @Override // org.apache.catalina.Session
    public long getThisAccessedTimeInternal() {
        return 0L;
    }

    @Override // org.apache.catalina.Session
    public boolean isValid() {
        return false;
    }

    @Override // org.apache.catalina.Session
    public void recycle() {
    }

    @Override // org.apache.catalina.Session
    public void removeNote(String name) {
    }

    @Override // org.apache.catalina.Session
    public void removeSessionListener(SessionListener listener) {
    }

    @Override // org.apache.catalina.Session
    public void setAuthType(String authType) {
    }

    @Override // org.apache.catalina.Session
    public void setCreationTime(long time) {
    }

    @Override // org.apache.catalina.Session
    public void setId(String id) {
        this.sessionId = id;
    }

    @Override // org.apache.catalina.Session
    public void setId(String id, boolean notify) {
        this.sessionId = id;
    }

    @Override // org.apache.catalina.Session
    public void setManager(Manager manager) {
    }

    @Override // org.apache.catalina.Session
    public void setMaxInactiveInterval(int interval) {
    }

    @Override // org.apache.catalina.Session
    public void setNew(boolean isNew) {
    }

    @Override // org.apache.catalina.Session
    public void setNote(String name, Object value) {
    }

    @Override // org.apache.catalina.Session
    public void setPrincipal(Principal principal) {
    }

    @Override // org.apache.catalina.Session
    public void setValid(boolean isValid) {
    }

    @Override // org.apache.catalina.Session
    public void tellChangedSessionId(String newId, String oldId, boolean notifySessionListeners, boolean notifyContainerListeners) {
    }

    @Override // org.apache.catalina.Session
    public boolean isAttributeDistributable(String name, Object value) {
        return false;
    }
}
