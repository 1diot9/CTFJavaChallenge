package org.apache.catalina;

import java.util.EventObject;

/* loaded from: agent.jar:BOOT-INF/lib/tomcat-embed-core-10.1.18.jar:org/apache/catalina/SessionEvent.class */
public final class SessionEvent extends EventObject {
    private static final long serialVersionUID = 1;
    private final Object data;
    private final Session session;
    private final String type;

    public SessionEvent(Session session, String type, Object data) {
        super(session);
        this.session = session;
        this.type = type;
        this.data = data;
    }

    public Object getData() {
        return this.data;
    }

    public Session getSession() {
        return this.session;
    }

    public String getType() {
        return this.type;
    }

    @Override // java.util.EventObject
    public String toString() {
        return "SessionEvent['" + getSession() + "','" + getType() + "']";
    }
}
