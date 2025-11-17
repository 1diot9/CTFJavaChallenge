package org.h2.expression.analysis;

/* loaded from: agent.jar:BOOT-INF/lib/h2-2.2.224.jar:org/h2/expression/analysis/WindowFrameBoundType.class */
public enum WindowFrameBoundType {
    UNBOUNDED_PRECEDING("UNBOUNDED PRECEDING"),
    PRECEDING("PRECEDING"),
    CURRENT_ROW("CURRENT ROW"),
    FOLLOWING("FOLLOWING"),
    UNBOUNDED_FOLLOWING("UNBOUNDED FOLLOWING");

    private final String sql;

    WindowFrameBoundType(String str) {
        this.sql = str;
    }

    public String getSQL() {
        return this.sql;
    }
}
