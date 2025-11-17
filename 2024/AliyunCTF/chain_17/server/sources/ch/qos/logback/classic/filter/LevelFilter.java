package ch.qos.logback.classic.filter;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.filter.AbstractMatcherFilter;
import ch.qos.logback.core.spi.FilterReply;

/* loaded from: server.jar:BOOT-INF/lib/logback-classic-1.4.14.jar:ch/qos/logback/classic/filter/LevelFilter.class */
public class LevelFilter extends AbstractMatcherFilter<ILoggingEvent> {
    Level level;

    @Override // ch.qos.logback.core.filter.Filter
    public FilterReply decide(ILoggingEvent event) {
        if (!isStarted()) {
            return FilterReply.NEUTRAL;
        }
        if (event.getLevel().equals(this.level)) {
            return this.onMatch;
        }
        return this.onMismatch;
    }

    public void setLevel(Level level) {
        this.level = level;
    }

    @Override // ch.qos.logback.core.filter.Filter, ch.qos.logback.core.spi.LifeCycle
    public void start() {
        if (this.level != null) {
            super.start();
        }
    }
}
