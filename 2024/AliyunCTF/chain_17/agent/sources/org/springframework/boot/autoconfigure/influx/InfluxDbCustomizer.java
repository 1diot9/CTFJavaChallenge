package org.springframework.boot.autoconfigure.influx;

import org.influxdb.InfluxDB;

@FunctionalInterface
@Deprecated(since = "3.2.0", forRemoval = true)
/* loaded from: agent.jar:BOOT-INF/lib/spring-boot-autoconfigure-3.2.2.jar:org/springframework/boot/autoconfigure/influx/InfluxDbCustomizer.class */
public interface InfluxDbCustomizer {
    void customize(InfluxDB influxDb);
}
