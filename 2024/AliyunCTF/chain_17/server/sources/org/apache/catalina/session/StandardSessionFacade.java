package org.apache.catalina.session;

import jakarta.servlet.ServletContext;
import jakarta.servlet.http.HttpSession;
import java.util.Enumeration;

/* loaded from: server.jar:BOOT-INF/lib/tomcat-embed-core-10.1.18.jar:org/apache/catalina/session/StandardSessionFacade.class */
public class StandardSessionFacade implements HttpSession {
    private final HttpSession session;

    public StandardSessionFacade(HttpSession session) {
        this.session = session;
    }

    @Override // jakarta.servlet.http.HttpSession, org.apache.catalina.Session
    public long getCreationTime() {
        return this.session.getCreationTime();
    }

    @Override // jakarta.servlet.http.HttpSession, org.apache.catalina.Session
    public String getId() {
        return this.session.getId();
    }

    @Override // jakarta.servlet.http.HttpSession, org.apache.catalina.Session
    public long getLastAccessedTime() {
        return this.session.getLastAccessedTime();
    }

    @Override // jakarta.servlet.http.HttpSession
    public ServletContext getServletContext() {
        return this.session.getServletContext();
    }

    @Override // jakarta.servlet.http.HttpSession, org.apache.catalina.Session
    public void setMaxInactiveInterval(int interval) {
        this.session.setMaxInactiveInterval(interval);
    }

    @Override // jakarta.servlet.http.HttpSession, org.apache.catalina.Session
    public int getMaxInactiveInterval() {
        return this.session.getMaxInactiveInterval();
    }

    @Override // jakarta.servlet.http.HttpSession
    public Object getAttribute(String name) {
        return this.session.getAttribute(name);
    }

    @Override // jakarta.servlet.http.HttpSession
    public Enumeration<String> getAttributeNames() {
        return this.session.getAttributeNames();
    }

    @Override // jakarta.servlet.http.HttpSession
    public void setAttribute(String name, Object value) {
        this.session.setAttribute(name, value);
    }

    @Override // jakarta.servlet.http.HttpSession
    public void removeAttribute(String name) {
        this.session.removeAttribute(name);
    }

    @Override // jakarta.servlet.http.HttpSession
    public void invalidate() {
        this.session.invalidate();
    }

    @Override // jakarta.servlet.http.HttpSession
    public boolean isNew() {
        return this.session.isNew();
    }
}
