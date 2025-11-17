package org.slf4j.spi;

import org.slf4j.ILoggerFactory;
import org.slf4j.IMarkerFactory;

/* loaded from: server.jar:BOOT-INF/lib/slf4j-api-2.0.11.jar:org/slf4j/spi/SLF4JServiceProvider.class */
public interface SLF4JServiceProvider {
    ILoggerFactory getLoggerFactory();

    IMarkerFactory getMarkerFactory();

    MDCAdapter getMDCAdapter();

    String getRequestedApiVersion();

    void initialize();
}
