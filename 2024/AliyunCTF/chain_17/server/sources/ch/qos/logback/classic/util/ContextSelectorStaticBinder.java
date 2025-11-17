package ch.qos.logback.classic.util;

import ch.qos.logback.classic.ClassicConstants;
import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.classic.selector.ContextJNDISelector;
import ch.qos.logback.classic.selector.ContextSelector;
import ch.qos.logback.classic.selector.DefaultContextSelector;
import ch.qos.logback.core.util.Loader;
import ch.qos.logback.core.util.OptionHelper;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

/* loaded from: server.jar:BOOT-INF/lib/logback-classic-1.4.14.jar:ch/qos/logback/classic/util/ContextSelectorStaticBinder.class */
public class ContextSelectorStaticBinder {
    static ContextSelectorStaticBinder singleton = new ContextSelectorStaticBinder();
    ContextSelector contextSelector;
    Object key;

    public static ContextSelectorStaticBinder getSingleton() {
        return singleton;
    }

    public void init(LoggerContext defaultLoggerContext, Object key) throws ClassNotFoundException, NoSuchMethodException, InstantiationException, IllegalAccessException, InvocationTargetException {
        if (this.key == null) {
            this.key = key;
        } else if (this.key != key) {
            throw new IllegalAccessException("Only certain classes can access this method.");
        }
        String contextSelectorStr = OptionHelper.getSystemProperty(ClassicConstants.LOGBACK_CONTEXT_SELECTOR);
        if (contextSelectorStr == null) {
            this.contextSelector = new DefaultContextSelector(defaultLoggerContext);
        } else if (contextSelectorStr.equals("JNDI")) {
            this.contextSelector = new ContextJNDISelector(defaultLoggerContext);
        } else {
            this.contextSelector = dynamicalContextSelector(defaultLoggerContext, contextSelectorStr);
        }
    }

    static ContextSelector dynamicalContextSelector(LoggerContext defaultLoggerContext, String contextSelectorStr) throws ClassNotFoundException, SecurityException, NoSuchMethodException, IllegalArgumentException, InstantiationException, IllegalAccessException, InvocationTargetException {
        Class<?> contextSelectorClass = Loader.loadClass(contextSelectorStr);
        Constructor<?> cons = contextSelectorClass.getConstructor(LoggerContext.class);
        return (ContextSelector) cons.newInstance(defaultLoggerContext);
    }

    public ContextSelector getContextSelector() {
        return this.contextSelector;
    }
}
