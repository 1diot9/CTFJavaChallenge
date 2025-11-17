package org.springframework.web.util;

import jakarta.servlet.ServletContextEvent;
import jakarta.servlet.ServletContextListener;
import java.beans.Introspector;
import org.springframework.beans.CachedIntrospectionResults;

/* loaded from: agent.jar:BOOT-INF/lib/spring-web-6.1.3.jar:org/springframework/web/util/IntrospectorCleanupListener.class */
public class IntrospectorCleanupListener implements ServletContextListener {
    @Override // jakarta.servlet.ServletContextListener
    public void contextInitialized(ServletContextEvent event) {
        CachedIntrospectionResults.acceptClassLoader(Thread.currentThread().getContextClassLoader());
    }

    @Override // jakarta.servlet.ServletContextListener
    public void contextDestroyed(ServletContextEvent event) {
        CachedIntrospectionResults.clearClassLoader(Thread.currentThread().getContextClassLoader());
        Introspector.flushCaches();
    }
}
