package org.springframework.cglib.core;

import org.springframework.asm.Type;

/* loaded from: server.jar:BOOT-INF/lib/spring-core-6.1.3.jar:org/springframework/cglib/core/Local.class */
public class Local {
    private Type type;
    private int index;

    public Local(int index, Type type) {
        this.type = type;
        this.index = index;
    }

    public int getIndex() {
        return this.index;
    }

    public Type getType() {
        return this.type;
    }
}
