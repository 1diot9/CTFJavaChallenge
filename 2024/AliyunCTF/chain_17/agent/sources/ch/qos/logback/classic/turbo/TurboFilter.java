package ch.qos.logback.classic.turbo;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import ch.qos.logback.core.spi.ContextAwareBase;
import ch.qos.logback.core.spi.FilterReply;
import ch.qos.logback.core.spi.LifeCycle;
import org.slf4j.Marker;

/* loaded from: agent.jar:BOOT-INF/lib/logback-classic-1.4.14.jar:ch/qos/logback/classic/turbo/TurboFilter.class */
public abstract class TurboFilter extends ContextAwareBase implements LifeCycle {
    private String name;
    boolean start = false;

    public abstract FilterReply decide(Marker marker, Logger logger, Level level, String str, Object[] objArr, Throwable th);

    public void start() {
        this.start = true;
    }

    @Override // ch.qos.logback.core.spi.LifeCycle
    public boolean isStarted() {
        return this.start;
    }

    public void stop() {
        this.start = false;
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
