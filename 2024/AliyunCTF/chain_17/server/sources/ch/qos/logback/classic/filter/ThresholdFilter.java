package ch.qos.logback.classic.filter;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.filter.Filter;
import ch.qos.logback.core.spi.FilterReply;

/* loaded from: server.jar:BOOT-INF/lib/logback-classic-1.4.14.jar:ch/qos/logback/classic/filter/ThresholdFilter.class */
public class ThresholdFilter extends Filter<ILoggingEvent> {
    Level level;

    @Override // ch.qos.logback.core.filter.Filter
    public FilterReply decide(ILoggingEvent event) {
        if (!isStarted()) {
            return FilterReply.NEUTRAL;
        }
        if (event.getLevel().isGreaterOrEqual(this.level)) {
            return FilterReply.NEUTRAL;
        }
        return FilterReply.DENY;
    }

    public void setLevel(String level) {
        this.level = Level.toLevel(level);
    }

    @Override // ch.qos.logback.core.filter.Filter, ch.qos.logback.core.spi.LifeCycle
    public void start() {
        if (this.level != null) {
            super.start();
        }
    }
}
