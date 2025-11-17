package ch.qos.logback.classic.turbo;

import ch.qos.logback.core.spi.FilterReply;

/* loaded from: agent.jar:BOOT-INF/lib/logback-classic-1.4.14.jar:ch/qos/logback/classic/turbo/MatchingFilter.class */
public abstract class MatchingFilter extends TurboFilter {
    protected FilterReply onMatch = FilterReply.NEUTRAL;
    protected FilterReply onMismatch = FilterReply.NEUTRAL;

    public final void setOnMatch(String action) {
        if ("NEUTRAL".equals(action)) {
            this.onMatch = FilterReply.NEUTRAL;
        } else if ("ACCEPT".equals(action)) {
            this.onMatch = FilterReply.ACCEPT;
        } else if ("DENY".equals(action)) {
            this.onMatch = FilterReply.DENY;
        }
    }

    public final void setOnMismatch(String action) {
        if ("NEUTRAL".equals(action)) {
            this.onMismatch = FilterReply.NEUTRAL;
        } else if ("ACCEPT".equals(action)) {
            this.onMismatch = FilterReply.ACCEPT;
        } else if ("DENY".equals(action)) {
            this.onMismatch = FilterReply.DENY;
        }
    }
}
