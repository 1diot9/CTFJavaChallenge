package org.h2.engine;

import org.h2.util.HasSQL;

/* loaded from: agent.jar:BOOT-INF/lib/h2-2.2.224.jar:org/h2/engine/NullsDistinct.class */
public enum NullsDistinct implements HasSQL {
    DISTINCT,
    ALL_DISTINCT,
    NOT_DISTINCT;

    @Override // org.h2.util.HasSQL
    public StringBuilder getSQL(StringBuilder sb, int i) {
        sb.append("NULLS ");
        switch (this) {
            case DISTINCT:
                sb.append("DISTINCT");
                break;
            case ALL_DISTINCT:
                sb.append("ALL DISTINCT");
                break;
            case NOT_DISTINCT:
                sb.append("NOT DISTINCT");
                break;
        }
        return sb;
    }
}
