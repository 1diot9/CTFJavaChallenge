package org.apache.catalina.authenticator;

import java.io.Serializable;
import org.apache.catalina.Context;
import org.apache.catalina.Session;

/* loaded from: agent.jar:BOOT-INF/lib/tomcat-embed-core-10.1.18.jar:org/apache/catalina/authenticator/SingleSignOnSessionKey.class */
public class SingleSignOnSessionKey implements Serializable {
    private static final long serialVersionUID = 1;
    private final String sessionId;
    private final String contextName;
    private final String hostName;

    public SingleSignOnSessionKey(Session session) {
        this.sessionId = session.getId();
        Context context = session.getManager().getContext();
        this.contextName = context.getName();
        this.hostName = context.getParent().getName();
    }

    public String getSessionId() {
        return this.sessionId;
    }

    public String getContextName() {
        return this.contextName;
    }

    public String getHostName() {
        return this.hostName;
    }

    public int hashCode() {
        int result = (31 * 1) + (this.sessionId == null ? 0 : this.sessionId.hashCode());
        return (31 * ((31 * result) + (this.contextName == null ? 0 : this.contextName.hashCode()))) + (this.hostName == null ? 0 : this.hostName.hashCode());
    }

    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }
        SingleSignOnSessionKey other = (SingleSignOnSessionKey) obj;
        if (this.sessionId == null) {
            if (other.sessionId != null) {
                return false;
            }
        } else if (!this.sessionId.equals(other.sessionId)) {
            return false;
        }
        if (this.contextName == null) {
            if (other.contextName != null) {
                return false;
            }
        } else if (!this.contextName.equals(other.contextName)) {
            return false;
        }
        if (this.hostName == null) {
            if (other.hostName != null) {
                return false;
            }
            return true;
        }
        if (!this.hostName.equals(other.hostName)) {
            return false;
        }
        return true;
    }

    public String toString() {
        StringBuilder sb = new StringBuilder(128);
        sb.append("Host: [");
        sb.append(this.hostName);
        sb.append("], Context: [");
        sb.append(this.contextName);
        sb.append("], SessionID: [");
        sb.append(this.sessionId);
        sb.append(']');
        return sb.toString();
    }
}
