package org.jooq.types;

import java.io.Serializable;
import java.time.Duration;

/* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/types/Interval.class */
public interface Interval extends Serializable {
    Interval neg();

    Interval abs();

    int getSign();

    double doubleValue();

    float floatValue();

    long longValue();

    int intValue();

    byte byteValue();

    short shortValue();

    Duration toDuration();
}
