package ch.qos.logback.classic.spi;

import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.classic.util.ContextInitializer;
import ch.qos.logback.classic.util.LogbackMDCAdapter;
import ch.qos.logback.core.joran.spi.JoranException;
import ch.qos.logback.core.status.StatusUtil;
import ch.qos.logback.core.util.StatusPrinter;
import org.slf4j.ILoggerFactory;
import org.slf4j.IMarkerFactory;
import org.slf4j.helpers.BasicMarkerFactory;
import org.slf4j.helpers.Util;
import org.slf4j.spi.MDCAdapter;
import org.slf4j.spi.SLF4JServiceProvider;

/* loaded from: agent.jar:BOOT-INF/lib/logback-classic-1.4.14.jar:ch/qos/logback/classic/spi/LogbackServiceProvider.class */
public class LogbackServiceProvider implements SLF4JServiceProvider {
    static final String NULL_CS_URL = "http://logback.qos.ch/codes.html#null_CS";
    public static String REQUESTED_API_VERSION = "2.0.99";
    private LoggerContext defaultLoggerContext;
    private IMarkerFactory markerFactory;
    private LogbackMDCAdapter mdcAdapter;

    @Override // org.slf4j.spi.SLF4JServiceProvider
    public void initialize() {
        this.defaultLoggerContext = new LoggerContext();
        this.defaultLoggerContext.setName("default");
        initializeLoggerContext();
        this.defaultLoggerContext.start();
        this.markerFactory = new BasicMarkerFactory();
        this.mdcAdapter = new LogbackMDCAdapter();
        this.defaultLoggerContext.setMDCAdapter(this.mdcAdapter);
    }

    private void initializeLoggerContext() {
        try {
            try {
                new ContextInitializer(this.defaultLoggerContext).autoConfig();
            } catch (JoranException je) {
                Util.report("Failed to auto configure default logger context", je);
            }
            if (!StatusUtil.contextHasStatusListener(this.defaultLoggerContext)) {
                StatusPrinter.printInCaseOfErrorsOrWarnings(this.defaultLoggerContext);
            }
        } catch (Exception t) {
            Util.report("Failed to instantiate [" + LoggerContext.class.getName() + "]", t);
        }
    }

    @Override // org.slf4j.spi.SLF4JServiceProvider
    public ILoggerFactory getLoggerFactory() {
        return this.defaultLoggerContext;
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
}
