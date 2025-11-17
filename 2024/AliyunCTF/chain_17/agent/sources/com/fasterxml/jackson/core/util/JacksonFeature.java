package com.fasterxml.jackson.core.util;

/* loaded from: agent.jar:BOOT-INF/lib/jackson-core-2.15.3.jar:com/fasterxml/jackson/core/util/JacksonFeature.class */
public interface JacksonFeature {
    boolean enabledByDefault();

    int getMask();

    boolean enabledIn(int i);
}
