package com.fasterxml.jackson.databind.cfg;

/* loaded from: server.jar:BOOT-INF/lib/jackson-databind-2.15.3.jar:com/fasterxml/jackson/databind/cfg/ConfigFeature.class */
public interface ConfigFeature {
    boolean enabledByDefault();

    int getMask();

    boolean enabledIn(int i);
}
