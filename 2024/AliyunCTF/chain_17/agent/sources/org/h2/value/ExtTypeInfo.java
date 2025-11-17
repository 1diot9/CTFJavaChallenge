package org.h2.value;

import org.h2.util.HasSQL;

/* loaded from: agent.jar:BOOT-INF/lib/h2-2.2.224.jar:org/h2/value/ExtTypeInfo.class */
public abstract class ExtTypeInfo implements HasSQL {
    public String toString() {
        return getSQL(1);
    }
}
