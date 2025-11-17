package org.springframework.core.metrics.jfr;

import jdk.jfr.Category;
import jdk.jfr.Description;
import jdk.jfr.Event;
import jdk.jfr.Label;

@Category({"Spring Application"})
@Label("Startup Step")
@Description("Spring Application Startup")
/* loaded from: agent.jar:BOOT-INF/lib/spring-core-6.1.3.jar:org/springframework/core/metrics/jfr/FlightRecorderStartupEvent.class */
class FlightRecorderStartupEvent extends Event {
    public final long eventId;
    public final long parentId;

    @Label("Name")
    public final String name;

    @Label("Tags")
    String tags = "";

    public FlightRecorderStartupEvent(long eventId, String name, long parentId) {
        this.name = name;
        this.eventId = eventId;
        this.parentId = parentId;
    }

    public void setTags(String tags) {
        this.tags = tags;
    }
}
