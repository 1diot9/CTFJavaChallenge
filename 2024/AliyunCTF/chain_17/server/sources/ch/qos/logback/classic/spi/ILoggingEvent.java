package ch.qos.logback.classic.spi;

import ch.qos.logback.classic.Level;
import ch.qos.logback.core.spi.DeferredProcessingAware;
import java.time.Instant;
import java.util.List;
import java.util.Map;
import org.slf4j.Marker;
import org.slf4j.event.KeyValuePair;

/* loaded from: server.jar:BOOT-INF/lib/logback-classic-1.4.14.jar:ch/qos/logback/classic/spi/ILoggingEvent.class */
public interface ILoggingEvent extends DeferredProcessingAware {
    String getThreadName();

    Level getLevel();

    String getMessage();

    Object[] getArgumentArray();

    String getFormattedMessage();

    String getLoggerName();

    LoggerContextVO getLoggerContextVO();

    IThrowableProxy getThrowableProxy();

    StackTraceElement[] getCallerData();

    boolean hasCallerData();

    List<Marker> getMarkerList();

    Map<String, String> getMDCPropertyMap();

    Map<String, String> getMdc();

    long getTimeStamp();

    int getNanoseconds();

    long getSequenceNumber();

    List<KeyValuePair> getKeyValuePairs();

    @Override // ch.qos.logback.core.spi.DeferredProcessingAware
    void prepareForDeferredProcessing();

    default Marker getMarker() {
        List<Marker> markers = getMarkerList();
        if (markers == null || markers.isEmpty()) {
            return null;
        }
        return markers.get(0);
    }

    default Instant getInstant() {
        return Instant.ofEpochMilli(getTimeStamp());
    }
}
