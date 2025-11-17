package ch.qos.logback.classic.selector.servlet;

import ch.qos.logback.classic.ClassicConstants;
import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.classic.selector.ContextSelector;
import ch.qos.logback.classic.util.ContextSelectorStaticBinder;
import ch.qos.logback.core.util.JNDIUtil;
import jakarta.servlet.ServletContextEvent;
import jakarta.servlet.ServletContextListener;
import javax.naming.Context;
import javax.naming.NamingException;
import org.slf4j.Logger;

/* loaded from: agent.jar:BOOT-INF/lib/logback-classic-1.4.14.jar:ch/qos/logback/classic/selector/servlet/ContextDetachingSCL.class */
public class ContextDetachingSCL implements ServletContextListener {
    @Override // jakarta.servlet.ServletContextListener
    public void contextInitialized(ServletContextEvent arg0) {
    }

    @Override // jakarta.servlet.ServletContextListener
    public void contextDestroyed(ServletContextEvent servletContextEvent) {
        String loggerContextName = null;
        try {
            Context ctx = JNDIUtil.getInitialContext();
            loggerContextName = JNDIUtil.lookupString(ctx, ClassicConstants.JNDI_CONTEXT_NAME);
        } catch (NamingException e) {
        }
        if (loggerContextName != null) {
            System.out.println("About to detach context named " + loggerContextName);
            ContextSelector selector = ContextSelectorStaticBinder.getSingleton().getContextSelector();
            if (selector == null) {
                System.out.println("Selector is null, cannot detach context. Skipping.");
                return;
            }
            LoggerContext context = selector.getLoggerContext(loggerContextName);
            if (context != null) {
                Logger logger = context.getLogger("ROOT");
                logger.warn("Stopping logger context " + loggerContextName);
                selector.detachLoggerContext(loggerContextName);
                context.stop();
                return;
            }
            System.out.println("No context named " + loggerContextName + " was found.");
        }
    }
}
