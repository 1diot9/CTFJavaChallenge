package io.micrometer.common.util.internal.logging;

import java.util.Objects;

/* loaded from: agent.jar:BOOT-INF/lib/micrometer-commons-1.12.2.jar:io/micrometer/common/util/internal/logging/InternalLoggerFactory.class */
public abstract class InternalLoggerFactory {
    private static volatile InternalLoggerFactory defaultFactory;

    protected abstract InternalLogger newInstance(String str);

    private static InternalLoggerFactory newDefaultFactory(String name) {
        InternalLoggerFactory f;
        try {
            f = Slf4JLoggerFactory.INSTANCE;
            f.newInstance(name).debug("Using SLF4J as the default logging framework");
        } catch (Throwable th) {
            f = JdkLoggerFactory.INSTANCE;
            f.newInstance(name).debug("Using java.util.logging as the default logging framework");
        }
        return f;
    }

    public static InternalLoggerFactory getDefaultFactory() {
        if (defaultFactory == null) {
            defaultFactory = newDefaultFactory(InternalLoggerFactory.class.getName());
        }
        return defaultFactory;
    }

    public static void setDefaultFactory(InternalLoggerFactory defaultFactory2) {
        Objects.requireNonNull(defaultFactory2, "defaultFactory");
        defaultFactory = defaultFactory2;
    }

    public static InternalLogger getInstance(Class<?> clazz) {
        return getInstance(clazz.getName());
    }

    public static InternalLogger getInstance(String name) {
        return getDefaultFactory().newInstance(name);
    }
}
