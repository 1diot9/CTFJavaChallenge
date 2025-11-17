package ch.qos.logback.classic.sift;

import ch.qos.logback.classic.ClassicConstants;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.joran.spi.DefaultClass;
import ch.qos.logback.core.sift.Discriminator;
import ch.qos.logback.core.sift.SiftingAppenderBase;
import java.util.List;
import org.slf4j.Marker;

/* loaded from: server.jar:BOOT-INF/lib/logback-classic-1.4.14.jar:ch/qos/logback/classic/sift/SiftingAppender.class */
public class SiftingAppender extends SiftingAppenderBase<ILoggingEvent> {
    /* JADX INFO: Access modifiers changed from: protected */
    @Override // ch.qos.logback.core.sift.SiftingAppenderBase
    public long getTimestamp(ILoggingEvent event) {
        return event.getTimeStamp();
    }

    @Override // ch.qos.logback.core.sift.SiftingAppenderBase
    @DefaultClass(MDCBasedDiscriminator.class)
    public void setDiscriminator(Discriminator<ILoggingEvent> discriminator) {
        super.setDiscriminator(discriminator);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // ch.qos.logback.core.sift.SiftingAppenderBase
    public boolean eventMarksEndOfLife(ILoggingEvent event) {
        List<Marker> markers = event.getMarkerList();
        if (markers == null) {
            return false;
        }
        for (Marker m : markers) {
            if (m.contains(ClassicConstants.FINALIZE_SESSION_MARKER)) {
                return true;
            }
        }
        return false;
    }
}
