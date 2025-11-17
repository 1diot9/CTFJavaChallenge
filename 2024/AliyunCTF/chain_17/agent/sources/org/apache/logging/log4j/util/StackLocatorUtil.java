package org.apache.logging.log4j.util;

import java.util.Deque;
import java.util.NoSuchElementException;
import java.util.function.Predicate;
import org.apache.logging.log4j.status.StatusLogger;

/* loaded from: agent.jar:BOOT-INF/lib/log4j-api-2.21.1.jar:org/apache/logging/log4j/util/StackLocatorUtil.class */
public final class StackLocatorUtil {
    private static StackLocator stackLocator;
    private static volatile boolean errorLogged;

    static {
        stackLocator = null;
        stackLocator = StackLocator.getInstance();
    }

    private StackLocatorUtil() {
    }

    @PerformanceSensitive
    public static Class<?> getCallerClass(final int depth) {
        return stackLocator.getCallerClass(depth + 1);
    }

    public static StackTraceElement getStackTraceElement(final int depth) {
        return stackLocator.getStackTraceElement(depth + 1);
    }

    @PerformanceSensitive
    public static Class<?> getCallerClass(final String fqcn) {
        return getCallerClass(fqcn, "");
    }

    @PerformanceSensitive
    public static Class<?> getCallerClass(final String fqcn, final String pkg) {
        return stackLocator.getCallerClass(fqcn, pkg);
    }

    @PerformanceSensitive
    public static ClassLoader getCallerClassLoader(final int depth) {
        Class<?> callerClass = stackLocator.getCallerClass(depth + 1);
        if (callerClass != null) {
            return callerClass.getClassLoader();
        }
        return null;
    }

    @PerformanceSensitive
    public static Class<?> getCallerClass(final Class<?> sentinelClass, final Predicate<Class<?>> callerPredicate) {
        return stackLocator.getCallerClass(sentinelClass, callerPredicate);
    }

    @PerformanceSensitive
    public static Class<?> getCallerClass(final Class<?> anchor) {
        return stackLocator.getCallerClass(anchor);
    }

    @PerformanceSensitive
    public static Deque<Class<?>> getCurrentStackTrace() {
        return stackLocator.getCurrentStackTrace();
    }

    public static StackTraceElement calcLocation(final String fqcnOfLogger) {
        try {
            return stackLocator.calcLocation(fqcnOfLogger);
        } catch (NoSuchElementException ex) {
            if (!errorLogged) {
                errorLogged = true;
                StatusLogger.getLogger().warn("Unable to locate stack trace element for {}", fqcnOfLogger, ex);
                return null;
            }
            return null;
        }
    }
}
