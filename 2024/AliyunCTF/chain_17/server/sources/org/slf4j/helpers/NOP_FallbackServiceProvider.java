package org.slf4j.helpers;

import org.slf4j.ILoggerFactory;
import org.slf4j.IMarkerFactory;
import org.slf4j.spi.MDCAdapter;
import org.slf4j.spi.SLF4JServiceProvider;

/* loaded from: server.jar:BOOT-INF/lib/slf4j-api-2.0.11.jar:org/slf4j/helpers/NOP_FallbackServiceProvider.class */
public class NOP_FallbackServiceProvider implements SLF4JServiceProvider {
    public static String REQUESTED_API_VERSION = "2.0.99";
    private final ILoggerFactory loggerFactory = new NOPLoggerFactory();
    private final IMarkerFactory markerFactory = new BasicMarkerFactory();
    private final MDCAdapter mdcAdapter = new NOPMDCAdapter();

    @Override // org.slf4j.spi.SLF4JServiceProvider
    public ILoggerFactory getLoggerFactory() {
        return this.loggerFactory;
    }

    @Override // org.slf4j.spi.SLF4JServiceProvider
    public IMarkerFactory getMarkerFactory() {
        return this.markerFactory;
    }

    @Override // org.slf4j.spi.SLF4JServiceProvider
    public MDCAdapter getMDCAdapter() {
        return this.mdcAdapter;
    }

    @Override // org.slf4j.spi.SLF4JServiceProvider
    public String getRequestedApiVersion() {
        return REQUESTED_API_VERSION;
    }

    @Override // org.slf4j.spi.SLF4JServiceProvider
    public void initialize() {
    }
}
