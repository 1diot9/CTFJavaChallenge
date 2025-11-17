package org.springframework.web.util;

import jakarta.servlet.http.HttpSessionEvent;
import jakarta.servlet.http.HttpSessionListener;
import java.io.Serializable;

/* loaded from: agent.jar:BOOT-INF/lib/spring-web-6.1.3.jar:org/springframework/web/util/HttpSessionMutexListener.class */
public class HttpSessionMutexListener implements HttpSessionListener {
    @Override // jakarta.servlet.http.HttpSessionListener
    public void sessionCreated(HttpSessionEvent event) {
        event.getSession().setAttribute(WebUtils.SESSION_MUTEX_ATTRIBUTE, new Mutex());
    }

    @Override // jakarta.servlet.http.HttpSessionListener
    public void sessionDestroyed(HttpSessionEvent event) {
        event.getSession().removeAttribute(WebUtils.SESSION_MUTEX_ATTRIBUTE);
    }

    /* loaded from: agent.jar:BOOT-INF/lib/spring-web-6.1.3.jar:org/springframework/web/util/HttpSessionMutexListener$Mutex.class */
    private static class Mutex implements Serializable {
        private Mutex() {
        }
    }
}
