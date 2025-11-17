package ch.qos.logback.classic.spi;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.turbo.TurboFilter;
import ch.qos.logback.core.spi.FilterReply;
import java.util.concurrent.CopyOnWriteArrayList;
import org.slf4j.Marker;

/* loaded from: server.jar:BOOT-INF/lib/logback-classic-1.4.14.jar:ch/qos/logback/classic/spi/TurboFilterList.class */
public final class TurboFilterList extends CopyOnWriteArrayList<TurboFilter> {
    private static final long serialVersionUID = 1;

    public FilterReply getTurboFilterChainDecision(Marker marker, Logger logger, Level level, String format, Object[] params, Throwable t) {
        int size = size();
        if (size == 1) {
            try {
                TurboFilter tf = get(0);
                return tf.decide(marker, logger, level, format, params, t);
            } catch (IndexOutOfBoundsException e) {
                return FilterReply.NEUTRAL;
            }
        }
        Object[] tfa = toArray();
        for (Object obj : tfa) {
            TurboFilter tf2 = (TurboFilter) obj;
            FilterReply r = tf2.decide(marker, logger, level, format, params, t);
            if (r == FilterReply.DENY || r == FilterReply.ACCEPT) {
                return r;
            }
        }
        return FilterReply.NEUTRAL;
    }
}
