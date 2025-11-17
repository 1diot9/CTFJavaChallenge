package io.micrometer.common.util.internal.logging;

import java.util.logging.Logger;

/* loaded from: server.jar:BOOT-INF/lib/micrometer-commons-1.12.2.jar:io/micrometer/common/util/internal/logging/JdkLoggerFactory.class */
public class JdkLoggerFactory extends InternalLoggerFactory {
    public static final InternalLoggerFactory INSTANCE = new JdkLoggerFactory();

    private JdkLoggerFactory() {
    }

    @Override // io.micrometer.common.util.internal.logging.InternalLoggerFactory
    public InternalLogger newInstance(String name) {
        return new JdkLogger(Logger.getLogger(name));
    }
}
