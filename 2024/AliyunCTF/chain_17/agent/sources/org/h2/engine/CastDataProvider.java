package org.h2.engine;

import org.h2.api.JavaObjectSerializer;
import org.h2.util.TimeZoneProvider;
import org.h2.value.ValueTimestampTimeZone;

/* loaded from: agent.jar:BOOT-INF/lib/h2-2.2.224.jar:org/h2/engine/CastDataProvider.class */
public interface CastDataProvider {
    ValueTimestampTimeZone currentTimestamp();

    TimeZoneProvider currentTimeZone();

    Mode getMode();

    JavaObjectSerializer getJavaObjectSerializer();

    boolean zeroBasedEnums();
}
