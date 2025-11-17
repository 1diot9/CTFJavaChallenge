package org.apache.catalina;

import jakarta.servlet.http.HttpSession;
import java.security.Principal;
import java.util.Iterator;

/* loaded from: agent.jar:BOOT-INF/lib/tomcat-embed-core-10.1.18.jar:org/apache/catalina/Session.class */
public interface Session {
    public static final String SESSION_CREATED_EVENT = "createSession";
    public static final String SESSION_DESTROYED_EVENT = "destroySession";
    public static final String SESSION_ACTIVATED_EVENT = "activateSession";
    public static final String SESSION_PASSIVATED_EVENT = "passivateSession";

    String getAuthType();

    void setAuthType(String str);

    long getCreationTime();

    long getCreationTimeInternal();

    void setCreationTime(long j);

    String getId();

    String getIdInternal();

    void setId(String str);

    void setId(String str, boolean z);

    long getThisAccessedTime();

    long getThisAccessedTimeInternal();

    long getLastAccessedTime();

    long getLastAccessedTimeInternal();

    long getIdleTime();

    long getIdleTimeInternal();

    Manager getManager();

    void setManager(Manager manager);

    int getMaxInactiveInterval();

    void setMaxInactiveInterval(int i);

    void setNew(boolean z);

    Principal getPrincipal();

    void setPrincipal(Principal principal);

    HttpSession getSession();

    void setValid(boolean z);

    boolean isValid();

    void access();

    void addSessionListener(SessionListener sessionListener);

    void endAccess();

    void expire();

    Object getNote(String str);

    Iterator<String> getNoteNames();

    void recycle();

    void removeNote(String str);

    void removeSessionListener(SessionListener sessionListener);

    void setNote(String str, Object obj);

    void tellChangedSessionId(String str, String str2, boolean z, boolean z2);

    boolean isAttributeDistributable(String str, Object obj);
}
