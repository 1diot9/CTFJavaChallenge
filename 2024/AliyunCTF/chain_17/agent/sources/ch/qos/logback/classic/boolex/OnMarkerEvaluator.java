package ch.qos.logback.classic.boolex;

import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.boolex.EvaluationException;
import ch.qos.logback.core.boolex.EventEvaluatorBase;
import java.util.ArrayList;
import java.util.List;
import org.slf4j.Marker;

/* loaded from: agent.jar:BOOT-INF/lib/logback-classic-1.4.14.jar:ch/qos/logback/classic/boolex/OnMarkerEvaluator.class */
public class OnMarkerEvaluator extends EventEvaluatorBase<ILoggingEvent> {
    List<String> markerList = new ArrayList();

    public void addMarker(String markerStr) {
        this.markerList.add(markerStr);
    }

    @Override // ch.qos.logback.core.boolex.EventEvaluator
    public boolean evaluate(ILoggingEvent event) throws NullPointerException, EvaluationException {
        List<Marker> markerListInEvent = event.getMarkerList();
        if (markerListInEvent == null || markerListInEvent.isEmpty()) {
            return false;
        }
        for (String markerStr : this.markerList) {
            for (Marker markerInEvent : markerListInEvent) {
                if (markerInEvent.contains(markerStr)) {
                    return true;
                }
            }
        }
        return false;
    }
}
