package org.h2.engine;

import org.h2.command.Prepared;

/* loaded from: agent.jar:BOOT-INF/lib/h2-2.2.224.jar:org/h2/engine/Procedure.class */
public class Procedure {
    private final String name;
    private final Prepared prepared;

    public Procedure(String str, Prepared prepared) {
        this.name = str;
        this.prepared = prepared;
    }

    public String getName() {
        return this.name;
    }

    public Prepared getPrepared() {
        return this.prepared;
    }
}
