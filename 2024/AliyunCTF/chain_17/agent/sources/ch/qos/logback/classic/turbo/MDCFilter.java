package ch.qos.logback.classic.turbo;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import ch.qos.logback.core.spi.FilterReply;
import org.slf4j.MDC;
import org.slf4j.Marker;

/* loaded from: agent.jar:BOOT-INF/lib/logback-classic-1.4.14.jar:ch/qos/logback/classic/turbo/MDCFilter.class */
public class MDCFilter extends MatchingFilter {
    String MDCKey;
    String value;

    @Override // ch.qos.logback.classic.turbo.TurboFilter, ch.qos.logback.core.spi.LifeCycle
    public void start() {
        int errorCount = 0;
        if (this.value == null) {
            addError("'value' parameter is mandatory. Cannot start.");
            errorCount = 0 + 1;
        }
        if (this.MDCKey == null) {
            addError("'MDCKey' parameter is mandatory. Cannot start.");
            errorCount++;
        }
        if (errorCount == 0) {
            this.start = true;
        }
    }

    @Override // ch.qos.logback.classic.turbo.TurboFilter
    public FilterReply decide(Marker marker, Logger logger, Level level, String format, Object[] params, Throwable t) {
        if (!isStarted()) {
            return FilterReply.NEUTRAL;
        }
        String value = MDC.get(this.MDCKey);
        if (this.value.equals(value)) {
            return this.onMatch;
        }
        return this.onMismatch;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public void setMDCKey(String MDCKey) {
        this.MDCKey = MDCKey;
    }
}
