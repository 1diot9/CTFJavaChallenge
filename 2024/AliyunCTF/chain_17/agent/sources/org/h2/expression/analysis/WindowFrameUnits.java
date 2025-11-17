package org.h2.expression.analysis;

/* loaded from: agent.jar:BOOT-INF/lib/h2-2.2.224.jar:org/h2/expression/analysis/WindowFrameUnits.class */
public enum WindowFrameUnits {
    ROWS,
    RANGE,
    GROUPS;

    public String getSQL() {
        return name();
    }
}
