package org.springframework.web.context;

import jakarta.servlet.ServletContext;
import jakarta.servlet.ServletContextEvent;
import jakarta.servlet.ServletContextListener;
import java.util.Enumeration;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.DisposableBean;

/* loaded from: agent.jar:BOOT-INF/lib/spring-web-6.1.3.jar:org/springframework/web/context/ContextCleanupListener.class */
public class ContextCleanupListener implements ServletContextListener {
    private static final Log logger = LogFactory.getLog((Class<?>) ContextCleanupListener.class);

    @Override // jakarta.servlet.ServletContextListener
    public void contextInitialized(ServletContextEvent event) {
    }

    @Override // jakarta.servlet.ServletContextListener
    public void contextDestroyed(ServletContextEvent event) {
        cleanupAttributes(event.getServletContext());
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static void cleanupAttributes(ServletContext servletContext) {
        Enumeration<String> attrNames = servletContext.getAttributeNames();
        while (attrNames.hasMoreElements()) {
            String attrName = attrNames.nextElement();
            if (attrName.startsWith("org.springframework.")) {
                Object attrValue = servletContext.getAttribute(attrName);
                if (attrValue instanceof DisposableBean) {
                    DisposableBean disposableBean = (DisposableBean) attrValue;
                    try {
                        disposableBean.destroy();
                    } catch (Throwable ex) {
                        if (logger.isWarnEnabled()) {
                            logger.warn("Invocation of destroy method failed on ServletContext attribute with name '" + attrName + "'", ex);
                        }
                    }
                }
            }
        }
    }
}
