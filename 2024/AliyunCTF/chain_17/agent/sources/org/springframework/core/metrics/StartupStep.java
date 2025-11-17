package org.springframework.core.metrics;

import java.util.function.Supplier;
import org.springframework.lang.Nullable;

/* loaded from: agent.jar:BOOT-INF/lib/spring-core-6.1.3.jar:org/springframework/core/metrics/StartupStep.class */
public interface StartupStep {

    /* loaded from: agent.jar:BOOT-INF/lib/spring-core-6.1.3.jar:org/springframework/core/metrics/StartupStep$Tag.class */
    public interface Tag {
        String getKey();

        String getValue();
    }

    /* loaded from: agent.jar:BOOT-INF/lib/spring-core-6.1.3.jar:org/springframework/core/metrics/StartupStep$Tags.class */
    public interface Tags extends Iterable<Tag> {
    }

    String getName();

    long getId();

    @Nullable
    Long getParentId();

    StartupStep tag(String key, String value);

    StartupStep tag(String key, Supplier<String> value);

    Tags getTags();

    void end();
}
