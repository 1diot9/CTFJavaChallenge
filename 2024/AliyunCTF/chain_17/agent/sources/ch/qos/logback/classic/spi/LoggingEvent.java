package ch.qos.logback.classic.spi;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.classic.util.LogbackMDCAdapter;
import ch.qos.logback.core.spi.SequenceNumberGenerator;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.time.Clock;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import org.slf4j.Marker;
import org.slf4j.event.KeyValuePair;
import org.slf4j.helpers.MessageFormatter;
import org.slf4j.spi.MDCAdapter;

/* loaded from: agent.jar:BOOT-INF/lib/logback-classic-1.4.14.jar:ch/qos/logback/classic/spi/LoggingEvent.class */
public class LoggingEvent implements ILoggingEvent {
    transient String fqnOfLoggerClass;
    private String threadName;
    private String loggerName;
    private LoggerContext loggerContext;
    private LoggerContextVO loggerContextVO;
    private transient Level level;
    private String message;
    transient String formattedMessage;
    private transient Object[] argumentArray;
    private ThrowableProxy throwableProxy;
    private StackTraceElement[] callerDataArray;
    private List<Marker> markerList;
    private Map<String, String> mdcPropertyMap;
    List<KeyValuePair> keyValuePairs;
    private Instant instant;
    private long timeStamp;
    private int nanoseconds;
    private long sequenceNumber;

    public LoggingEvent() {
    }

    public LoggingEvent(String fqcn, Logger logger, Level level, String message, Throwable throwable, Object[] argArray) {
        SequenceNumberGenerator sequenceNumberGenerator;
        this.fqnOfLoggerClass = fqcn;
        this.loggerName = logger.getName();
        this.loggerContext = logger.getLoggerContext();
        this.loggerContextVO = this.loggerContext.getLoggerContextRemoteView();
        this.level = level;
        this.message = message;
        this.argumentArray = argArray;
        Instant instant = Clock.systemUTC().instant();
        initTmestampFields(instant);
        if (this.loggerContext != null && (sequenceNumberGenerator = this.loggerContext.getSequenceNumberGenerator()) != null) {
            this.sequenceNumber = sequenceNumberGenerator.nextSequenceNumber();
        }
        throwable = throwable == null ? extractThrowableAnRearrangeArguments(argArray) : throwable;
        if (throwable != null) {
            this.throwableProxy = new ThrowableProxy(throwable);
            if (this.loggerContext != null && this.loggerContext.isPackagingDataEnabled()) {
                this.throwableProxy.calculatePackagingData();
            }
        }
    }

    void initTmestampFields(Instant instant) {
        this.instant = instant;
        long epochSecond = instant.getEpochSecond();
        this.nanoseconds = instant.getNano();
        long milliseconds = this.nanoseconds / 1000000;
        this.timeStamp = (epochSecond * 1000) + milliseconds;
    }

    private Throwable extractThrowableAnRearrangeArguments(Object[] argArray) {
        Throwable extractedThrowable = EventArgUtil.extractThrowable(argArray);
        if (EventArgUtil.successfulExtraction(extractedThrowable)) {
            this.argumentArray = EventArgUtil.trimmedCopy(argArray);
        }
        return extractedThrowable;
    }

    public void setArgumentArray(Object[] argArray) {
        if (this.argumentArray != null) {
            throw new IllegalStateException("argArray has been already set");
        }
        this.argumentArray = argArray;
    }

    @Override // ch.qos.logback.classic.spi.ILoggingEvent
    public Object[] getArgumentArray() {
        return this.argumentArray;
    }

    public void addKeyValuePair(KeyValuePair kvp) {
        if (this.keyValuePairs == null) {
            this.keyValuePairs = new ArrayList(4);
        }
        this.keyValuePairs.add(kvp);
    }

    public void setKeyValuePairs(List<KeyValuePair> kvpList) {
        this.keyValuePairs = kvpList;
    }

    @Override // ch.qos.logback.classic.spi.ILoggingEvent
    public List<KeyValuePair> getKeyValuePairs() {
        return this.keyValuePairs;
    }

    @Override // ch.qos.logback.classic.spi.ILoggingEvent
    public Level getLevel() {
        return this.level;
    }

    @Override // ch.qos.logback.classic.spi.ILoggingEvent
    public String getLoggerName() {
        return this.loggerName;
    }

    public void setLoggerName(String loggerName) {
        this.loggerName = loggerName;
    }

    @Override // ch.qos.logback.classic.spi.ILoggingEvent
    public String getThreadName() {
        if (this.threadName == null) {
            this.threadName = Thread.currentThread().getName();
        }
        return this.threadName;
    }

    public void setThreadName(String threadName) throws IllegalStateException {
        if (this.threadName != null) {
            throw new IllegalStateException("threadName has been already set");
        }
        this.threadName = threadName;
    }

    @Override // ch.qos.logback.classic.spi.ILoggingEvent
    public IThrowableProxy getThrowableProxy() {
        return this.throwableProxy;
    }

    public void setThrowableProxy(ThrowableProxy tp) {
        if (this.throwableProxy != null) {
            throw new IllegalStateException("ThrowableProxy has been already set.");
        }
        this.throwableProxy = tp;
    }

    @Override // ch.qos.logback.classic.spi.ILoggingEvent, ch.qos.logback.core.spi.DeferredProcessingAware
    public void prepareForDeferredProcessing() {
        getFormattedMessage();
        getThreadName();
        getMDCPropertyMap();
    }

    public void setLoggerContext(LoggerContext lc) {
        this.loggerContext = lc;
    }

    @Override // ch.qos.logback.classic.spi.ILoggingEvent
    public LoggerContextVO getLoggerContextVO() {
        return this.loggerContextVO;
    }

    public void setLoggerContextRemoteView(LoggerContextVO loggerContextVO) {
        this.loggerContextVO = loggerContextVO;
    }

    @Override // ch.qos.logback.classic.spi.ILoggingEvent
    public String getMessage() {
        return this.message;
    }

    public void setMessage(String message) {
        if (this.message != null) {
            throw new IllegalStateException("The message for this event has been set already.");
        }
        this.message = message;
    }

    @Override // ch.qos.logback.classic.spi.ILoggingEvent
    public Instant getInstant() {
        return this.instant;
    }

    public void setInstant(Instant instant) {
        initTmestampFields(instant);
    }

    @Override // ch.qos.logback.classic.spi.ILoggingEvent
    public long getTimeStamp() {
        return this.timeStamp;
    }

    @Override // ch.qos.logback.classic.spi.ILoggingEvent
    public int getNanoseconds() {
        return this.nanoseconds;
    }

    public void setTimeStamp(long timeStamp) {
        Instant instant = Instant.ofEpochMilli(timeStamp);
        setInstant(instant);
    }

    @Override // ch.qos.logback.classic.spi.ILoggingEvent
    public long getSequenceNumber() {
        return this.sequenceNumber;
    }

    public void setSequenceNumber(long sn) {
        this.sequenceNumber = sn;
    }

    public void setLevel(Level level) {
        if (this.level != null) {
            throw new IllegalStateException("The level has been already set for this event.");
        }
        this.level = level;
    }

    @Override // ch.qos.logback.classic.spi.ILoggingEvent
    public StackTraceElement[] getCallerData() {
        if (this.callerDataArray == null) {
            this.callerDataArray = CallerData.extract(new Throwable(), this.fqnOfLoggerClass, this.loggerContext.getMaxCallerDataDepth(), this.loggerContext.getFrameworkPackages());
        }
        return this.callerDataArray;
    }

    @Override // ch.qos.logback.classic.spi.ILoggingEvent
    public boolean hasCallerData() {
        return this.callerDataArray != null;
    }

    public void setCallerData(StackTraceElement[] callerDataArray) {
        this.callerDataArray = callerDataArray;
    }

    @Override // ch.qos.logback.classic.spi.ILoggingEvent
    public List<Marker> getMarkerList() {
        return this.markerList;
    }

    public void addMarker(Marker marker) {
        if (marker == null) {
            return;
        }
        if (this.markerList == null) {
            this.markerList = new ArrayList(4);
        }
        this.markerList.add(marker);
    }

    public long getContextBirthTime() {
        return this.loggerContextVO.getBirthTime();
    }

    @Override // ch.qos.logback.classic.spi.ILoggingEvent
    public String getFormattedMessage() {
        if (this.formattedMessage != null) {
            return this.formattedMessage;
        }
        if (this.argumentArray != null) {
            this.formattedMessage = MessageFormatter.arrayFormat(this.message, this.argumentArray).getMessage();
        } else {
            this.formattedMessage = this.message;
        }
        return this.formattedMessage;
    }

    @Override // ch.qos.logback.classic.spi.ILoggingEvent
    public Map<String, String> getMDCPropertyMap() {
        if (this.mdcPropertyMap == null) {
            MDCAdapter mdcAdapter = this.loggerContext.getMDCAdapter();
            if (mdcAdapter instanceof LogbackMDCAdapter) {
                this.mdcPropertyMap = ((LogbackMDCAdapter) mdcAdapter).getPropertyMap();
            } else {
                this.mdcPropertyMap = mdcAdapter.getCopyOfContextMap();
            }
        }
        if (this.mdcPropertyMap == null) {
            this.mdcPropertyMap = Collections.emptyMap();
        }
        return this.mdcPropertyMap;
    }

    public void setMDCPropertyMap(Map<String, String> map) {
        if (this.mdcPropertyMap != null) {
            throw new IllegalStateException("The MDCPropertyMap has been already set for this event.");
        }
        this.mdcPropertyMap = map;
    }

    @Override // ch.qos.logback.classic.spi.ILoggingEvent
    public Map<String, String> getMdc() {
        return getMDCPropertyMap();
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append('[');
        sb.append(this.level).append("] ");
        sb.append(getFormattedMessage());
        return sb.toString();
    }

    private void writeObject(ObjectOutputStream out) throws IOException {
        throw new UnsupportedOperationException(String.valueOf(getClass()) + " does not support serialization. Use LoggerEventVO instance instead. See also LoggerEventVO.build method.");
    }
}
