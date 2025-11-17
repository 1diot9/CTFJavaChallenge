package ch.qos.logback.core.util;

import ch.qos.logback.core.Context;
import ch.qos.logback.core.CoreConstants;
import ch.qos.logback.core.spi.ContextAware;
import ch.qos.logback.core.spi.LifeCycle;
import ch.qos.logback.core.status.OnConsoleStatusListener;
import ch.qos.logback.core.status.StatusListener;

/* loaded from: server.jar:BOOT-INF/lib/logback-core-1.4.14.jar:ch/qos/logback/core/util/StatusListenerConfigHelper.class */
public class StatusListenerConfigHelper {
    public static void installIfAsked(Context context) {
        String slClass = OptionHelper.getSystemProperty(CoreConstants.STATUS_LISTENER_CLASS_KEY);
        if (!OptionHelper.isNullOrEmpty(slClass)) {
            addStatusListener(context, slClass);
        }
    }

    private static void addStatusListener(Context context, String listenerClassName) {
        StatusListener listener;
        if (CoreConstants.SYSOUT.equalsIgnoreCase(listenerClassName) || CoreConstants.STDOUT.equalsIgnoreCase(listenerClassName)) {
            listener = new OnConsoleStatusListener();
        } else {
            listener = createListenerPerClassName(context, listenerClassName);
        }
        initAndAddListener(context, listener);
    }

    private static void initAndAddListener(Context context, StatusListener listener) {
        if (listener != null) {
            if (listener instanceof ContextAware) {
                ((ContextAware) listener).setContext(context);
            }
            boolean effectivelyAdded = context.getStatusManager().add(listener);
            if (effectivelyAdded && (listener instanceof LifeCycle)) {
                ((LifeCycle) listener).start();
            }
        }
    }

    private static StatusListener createListenerPerClassName(Context context, String listenerClass) {
        try {
            return (StatusListener) OptionHelper.instantiateByClassName(listenerClass, (Class<?>) StatusListener.class, context);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static void addOnConsoleListenerInstance(Context context, OnConsoleStatusListener onConsoleStatusListener) {
        onConsoleStatusListener.setContext(context);
        boolean effectivelyAdded = context.getStatusManager().add(onConsoleStatusListener);
        if (effectivelyAdded) {
            onConsoleStatusListener.start();
        }
    }
}
