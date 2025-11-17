package org.slf4j;

/* loaded from: server.jar:BOOT-INF/lib/slf4j-api-2.0.11.jar:org/slf4j/IMarkerFactory.class */
public interface IMarkerFactory {
    Marker getMarker(String str);

    boolean exists(String str);

    boolean detachMarker(String str);

    Marker getDetachedMarker(String str);
}
