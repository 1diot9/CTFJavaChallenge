package org.h2.util;

/* loaded from: agent.jar:BOOT-INF/lib/h2-2.2.224.jar:org/h2/util/HasSQL.class */
public interface HasSQL {
    public static final int QUOTE_ONLY_WHEN_REQUIRED = 1;
    public static final int REPLACE_LOBS_FOR_TRACE = 2;
    public static final int NO_CASTS = 4;
    public static final int ADD_PLAN_INFORMATION = 8;
    public static final int DEFAULT_SQL_FLAGS = 0;
    public static final int TRACE_SQL_FLAGS = 3;

    StringBuilder getSQL(StringBuilder sb, int i);

    default String getTraceSQL() {
        return getSQL(3);
    }

    default String getSQL(int i) {
        return getSQL(new StringBuilder(), i).toString();
    }
}
