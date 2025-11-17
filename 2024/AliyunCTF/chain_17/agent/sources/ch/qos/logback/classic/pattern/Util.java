package ch.qos.logback.classic.pattern;

import ch.qos.logback.classic.spi.ClassPackagingData;
import java.util.HashMap;
import java.util.Map;
import org.slf4j.Marker;

/* loaded from: agent.jar:BOOT-INF/lib/logback-classic-1.4.14.jar:ch/qos/logback/classic/pattern/Util.class */
public class Util {
    static Map<String, ClassPackagingData> cache = new HashMap();

    public static boolean match(Marker marker, Marker[] markerArray) {
        if (markerArray == null) {
            throw new IllegalArgumentException("markerArray should not be null");
        }
        for (Marker marker2 : markerArray) {
            if (marker.contains(marker2)) {
                return true;
            }
        }
        return false;
    }
}
