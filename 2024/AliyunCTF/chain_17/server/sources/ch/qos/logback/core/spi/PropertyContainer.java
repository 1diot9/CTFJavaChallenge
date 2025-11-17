package ch.qos.logback.core.spi;

import java.util.Map;

/* loaded from: server.jar:BOOT-INF/lib/logback-core-1.4.14.jar:ch/qos/logback/core/spi/PropertyContainer.class */
public interface PropertyContainer {
    String getProperty(String str);

    Map<String, String> getCopyOfPropertyMap();
}
