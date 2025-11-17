package org.h2.expression.analysis;

/* loaded from: agent.jar:BOOT-INF/lib/h2-2.2.224.jar:org/h2/expression/analysis/WindowFrameExclusion.class */
public enum WindowFrameExclusion {
    EXCLUDE_CURRENT_ROW("EXCLUDE CURRENT ROW"),
    EXCLUDE_GROUP("EXCLUDE GROUP"),
    EXCLUDE_TIES("EXCLUDE TIES"),
    EXCLUDE_NO_OTHERS("EXCLUDE NO OTHERS");

    private final String sql;

    WindowFrameExclusion(String str) {
        this.sql = str;
    }

    public boolean isGroupOrNoOthers() {
        return this == EXCLUDE_GROUP || this == EXCLUDE_NO_OTHERS;
    }

    public String getSQL() {
        return this.sql;
    }
}
