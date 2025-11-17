package ch.qos.logback.classic.selector;

import ch.qos.logback.classic.ClassicConstants;
import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.classic.joran.JoranConfigurator;
import ch.qos.logback.classic.util.ContextInitializer;
import ch.qos.logback.core.joran.spi.JoranException;
import ch.qos.logback.core.status.InfoStatus;
import ch.qos.logback.core.status.StatusManager;
import ch.qos.logback.core.status.StatusUtil;
import ch.qos.logback.core.status.WarnStatus;
import ch.qos.logback.core.util.JNDIUtil;
import ch.qos.logback.core.util.Loader;
import ch.qos.logback.core.util.StatusPrinter;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.naming.Context;
import javax.naming.NamingException;

/* loaded from: server.jar:BOOT-INF/lib/logback-classic-1.4.14.jar:ch/qos/logback/classic/selector/ContextJNDISelector.class */
public class ContextJNDISelector implements ContextSelector {
    private final Map<String, LoggerContext> synchronizedContextMap = Collections.synchronizedMap(new HashMap());
    private final LoggerContext defaultContext;
    private static final ThreadLocal<LoggerContext> threadLocal = new ThreadLocal<>();

    public ContextJNDISelector(LoggerContext context) {
        this.defaultContext = context;
    }

    @Override // ch.qos.logback.classic.selector.ContextSelector
    public LoggerContext getDefaultLoggerContext() {
        return this.defaultContext;
    }

    @Override // ch.qos.logback.classic.selector.ContextSelector
    public LoggerContext detachLoggerContext(String loggerContextName) {
        return this.synchronizedContextMap.remove(loggerContextName);
    }

    @Override // ch.qos.logback.classic.selector.ContextSelector
    public LoggerContext getLoggerContext() {
        String contextName = null;
        Context ctx = null;
        LoggerContext lc = threadLocal.get();
        if (lc != null) {
            return lc;
        }
        try {
            ctx = JNDIUtil.getInitialContext();
            contextName = JNDIUtil.lookupString(ctx, ClassicConstants.JNDI_CONTEXT_NAME);
        } catch (NamingException e) {
        }
        if (contextName == null) {
            return this.defaultContext;
        }
        LoggerContext loggerContext = this.synchronizedContextMap.get(contextName);
        if (loggerContext == null) {
            loggerContext = new LoggerContext();
            loggerContext.setName(contextName);
            this.synchronizedContextMap.put(contextName, loggerContext);
            URL url = findConfigFileURL(ctx, loggerContext);
            if (url != null) {
                configureLoggerContextByURL(loggerContext, url);
            } else {
                try {
                    new ContextInitializer(loggerContext).autoConfig();
                } catch (JoranException e2) {
                }
            }
            if (!StatusUtil.contextHasStatusListener(loggerContext)) {
                StatusPrinter.printInCaseOfErrorsOrWarnings(loggerContext);
            }
        }
        return loggerContext;
    }

    private String conventionalConfigFileName(String contextName) {
        return "logback-" + contextName + ".xml";
    }

    private URL findConfigFileURL(Context ctx, LoggerContext loggerContext) {
        StatusManager sm = loggerContext.getStatusManager();
        String jndiEntryForConfigResource = null;
        try {
            jndiEntryForConfigResource = JNDIUtil.lookupString(ctx, ClassicConstants.JNDI_CONFIGURATION_RESOURCE);
        } catch (NamingException e) {
            sm.add(new WarnStatus("JNDI lookup failed", this, e));
        }
        if (jndiEntryForConfigResource != null) {
            sm.add(new InfoStatus("Searching for [" + jndiEntryForConfigResource + "]", this));
            URL url = urlByResourceName(sm, jndiEntryForConfigResource);
            if (url == null) {
                String msg = "The jndi resource [" + jndiEntryForConfigResource + "] for context [" + loggerContext.getName() + "] does not lead to a valid file";
                sm.add(new WarnStatus(msg, this));
            }
            return url;
        }
        String resourceByConvention = conventionalConfigFileName(loggerContext.getName());
        return urlByResourceName(sm, resourceByConvention);
    }

    private URL urlByResourceName(StatusManager sm, String resourceName) {
        sm.add(new InfoStatus("Searching for [" + resourceName + "]", this));
        URL url = Loader.getResource(resourceName, Loader.getTCL());
        if (url != null) {
            return url;
        }
        return Loader.getResourceBySelfClassLoader(resourceName);
    }

    private void configureLoggerContextByURL(LoggerContext context, URL url) {
        try {
            JoranConfigurator configurator = new JoranConfigurator();
            context.reset();
            configurator.setContext(context);
            configurator.doConfigure(url);
        } catch (JoranException e) {
        }
        StatusPrinter.printInCaseOfErrorsOrWarnings(context);
    }

    @Override // ch.qos.logback.classic.selector.ContextSelector
    public List<String> getContextNames() {
        List<String> list = new ArrayList<>();
        list.addAll(this.synchronizedContextMap.keySet());
        return list;
    }

    @Override // ch.qos.logback.classic.selector.ContextSelector
    public LoggerContext getLoggerContext(String name) {
        return this.synchronizedContextMap.get(name);
    }

    public int getCount() {
        return this.synchronizedContextMap.size();
    }

    public void setLocalContext(LoggerContext context) {
        threadLocal.set(context);
    }

    public void removeLocalContext() {
        threadLocal.remove();
    }
}
