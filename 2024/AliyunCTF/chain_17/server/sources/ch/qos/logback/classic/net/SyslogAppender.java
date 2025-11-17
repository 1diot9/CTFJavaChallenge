package ch.qos.logback.classic.net;

import ch.qos.logback.classic.PatternLayout;
import ch.qos.logback.classic.pattern.SyslogStartConverter;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.classic.spi.IThrowableProxy;
import ch.qos.logback.classic.spi.StackTraceElementProxy;
import ch.qos.logback.classic.util.LevelToSyslogSeverity;
import ch.qos.logback.core.CoreConstants;
import ch.qos.logback.core.Layout;
import ch.qos.logback.core.net.SyslogAppenderBase;
import ch.qos.logback.core.net.SyslogOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.SocketException;
import java.net.UnknownHostException;

/* loaded from: server.jar:BOOT-INF/lib/logback-classic-1.4.14.jar:ch/qos/logback/classic/net/SyslogAppender.class */
public class SyslogAppender extends SyslogAppenderBase<ILoggingEvent> {
    public static final String DEFAULT_SUFFIX_PATTERN = "[%thread] %logger %msg";
    public static final String DEFAULT_STACKTRACE_PATTERN = "\t";
    PatternLayout stackTraceLayout = new PatternLayout();
    String stackTracePattern = DEFAULT_STACKTRACE_PATTERN;
    boolean throwableExcluded = false;

    @Override // ch.qos.logback.core.net.SyslogAppenderBase, ch.qos.logback.core.AppenderBase, ch.qos.logback.core.spi.LifeCycle
    public void start() {
        super.start();
        setupStackTraceLayout();
    }

    String getPrefixPattern() {
        return "%syslogStart{" + getFacility() + "}%nopex{}";
    }

    @Override // ch.qos.logback.core.net.SyslogAppenderBase
    public SyslogOutputStream createOutputStream() throws SocketException, UnknownHostException {
        return new SyslogOutputStream(getSyslogHost(), getPort());
    }

    @Override // ch.qos.logback.core.net.SyslogAppenderBase
    public int getSeverityForEvent(Object eventObject) {
        ILoggingEvent event = (ILoggingEvent) eventObject;
        return LevelToSyslogSeverity.convert(event);
    }

    @Override // ch.qos.logback.core.net.SyslogAppenderBase
    protected void postProcess(Object eventObject, OutputStream sw) {
        if (this.throwableExcluded) {
            return;
        }
        ILoggingEvent event = (ILoggingEvent) eventObject;
        IThrowableProxy tp = event.getThrowableProxy();
        if (tp == null) {
            return;
        }
        String stackTracePrefix = this.stackTraceLayout.doLayout(event);
        boolean isRootException = true;
        while (tp != null) {
            StackTraceElementProxy[] stepArray = tp.getStackTraceElementProxyArray();
            try {
                handleThrowableFirstLine(sw, tp, stackTracePrefix, isRootException);
                isRootException = false;
                for (StackTraceElementProxy step : stepArray) {
                    StringBuilder sb = new StringBuilder();
                    sb.append(stackTracePrefix).append(step);
                    sw.write(sb.toString().getBytes());
                    sw.flush();
                }
                tp = tp.getCause();
            } catch (IOException e) {
                return;
            }
        }
    }

    private void handleThrowableFirstLine(OutputStream sw, IThrowableProxy tp, String stackTracePrefix, boolean isRootException) throws IOException {
        StringBuilder sb = new StringBuilder().append(stackTracePrefix);
        if (!isRootException) {
            sb.append(CoreConstants.CAUSED_BY);
        }
        sb.append(tp.getClassName()).append(": ").append(tp.getMessage());
        sw.write(sb.toString().getBytes());
        sw.flush();
    }

    boolean stackTraceHeaderLine(StringBuilder sb, boolean topException) {
        return false;
    }

    @Override // ch.qos.logback.core.net.SyslogAppenderBase
    public Layout<ILoggingEvent> buildLayout() {
        PatternLayout layout = new PatternLayout();
        layout.getInstanceConverterMap().put("syslogStart", SyslogStartConverter.class.getName());
        if (this.suffixPattern == null) {
            this.suffixPattern = DEFAULT_SUFFIX_PATTERN;
        }
        layout.setPattern(getPrefixPattern() + this.suffixPattern);
        layout.setContext(getContext());
        layout.start();
        return layout;
    }

    private void setupStackTraceLayout() {
        this.stackTraceLayout.getInstanceConverterMap().put("syslogStart", SyslogStartConverter.class.getName());
        this.stackTraceLayout.setPattern(getPrefixPattern() + this.stackTracePattern);
        this.stackTraceLayout.setContext(getContext());
        this.stackTraceLayout.start();
    }

    public boolean isThrowableExcluded() {
        return this.throwableExcluded;
    }

    public void setThrowableExcluded(boolean throwableExcluded) {
        this.throwableExcluded = throwableExcluded;
    }

    public String getStackTracePattern() {
        return this.stackTracePattern;
    }

    public void setStackTracePattern(String stackTracePattern) {
        this.stackTracePattern = stackTracePattern;
    }
}
