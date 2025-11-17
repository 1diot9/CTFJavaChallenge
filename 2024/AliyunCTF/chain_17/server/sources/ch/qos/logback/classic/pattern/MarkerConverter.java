package ch.qos.logback.classic.pattern;

import ch.qos.logback.classic.spi.ILoggingEvent;
import java.util.List;
import org.slf4j.Marker;

/* loaded from: server.jar:BOOT-INF/lib/logback-classic-1.4.14.jar:ch/qos/logback/classic/pattern/MarkerConverter.class */
public class MarkerConverter extends ClassicConverter {
    private static String EMPTY = "";

    @Override // ch.qos.logback.core.pattern.Converter
    public String convert(ILoggingEvent le) {
        List<Marker> markers = le.getMarkerList();
        if (markers == null || markers.isEmpty()) {
            return EMPTY;
        }
        int size = markers.size();
        if (size == 1) {
            return markers.get(0).toString();
        }
        StringBuffer buf = new StringBuffer(32);
        for (int i = 0; i < size; i++) {
            if (i != 0) {
                buf.append(' ');
            }
            Marker m = markers.get(i);
            buf.append(m.toString());
        }
        return buf.toString();
    }
}
