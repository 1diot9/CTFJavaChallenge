package ch.qos.logback.classic.servlet;

import ch.qos.logback.classic.util.StatusViaSLF4JLoggerFactory;
import ch.qos.logback.core.CoreConstants;
import ch.qos.logback.core.util.OptionHelper;
import jakarta.servlet.ServletContainerInitializer;
import jakarta.servlet.ServletContext;
import jakarta.servlet.ServletException;
import java.util.Set;

/* loaded from: agent.jar:BOOT-INF/lib/logback-classic-1.4.14.jar:ch/qos/logback/classic/servlet/LogbackServletContainerInitializer.class */
public class LogbackServletContainerInitializer implements ServletContainerInitializer {
    @Override // jakarta.servlet.ServletContainerInitializer
    public void onStartup(Set<Class<?>> c, ServletContext ctx) throws ServletException {
        if (isDisabledByConfiguration(ctx)) {
            StatusViaSLF4JLoggerFactory.addInfo("Due to deployment instructions will NOT register an instance of " + String.valueOf(LogbackServletContextListener.class) + " to the current web-app", this);
            return;
        }
        StatusViaSLF4JLoggerFactory.addInfo("Adding an instance of  " + String.valueOf(LogbackServletContextListener.class) + " to the current web-app", this);
        LogbackServletContextListener lscl = new LogbackServletContextListener();
        ctx.addListener((ServletContext) lscl);
    }

    boolean isDisabledByConfiguration(ServletContext ctx) {
        String disableAttributeStr = null;
        Object disableAttribute = ctx.getInitParameter(CoreConstants.DISABLE_SERVLET_CONTAINER_INITIALIZER_KEY);
        if (disableAttribute instanceof String) {
            disableAttributeStr = (String) disableAttribute;
        }
        if (OptionHelper.isNullOrEmpty(disableAttributeStr)) {
            disableAttributeStr = OptionHelper.getSystemProperty(CoreConstants.DISABLE_SERVLET_CONTAINER_INITIALIZER_KEY);
        }
        if (OptionHelper.isNullOrEmpty(disableAttributeStr)) {
            disableAttributeStr = OptionHelper.getEnv(CoreConstants.DISABLE_SERVLET_CONTAINER_INITIALIZER_KEY);
        }
        return Boolean.parseBoolean(disableAttributeStr);
    }
}
