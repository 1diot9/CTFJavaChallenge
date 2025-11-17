package org.h2.table;

/* loaded from: agent.jar:BOOT-INF/lib/h2-2.2.224.jar:org/h2/table/TableType.class */
public enum TableType {
    TABLE_LINK,
    SYSTEM_TABLE,
    TABLE,
    VIEW,
    EXTERNAL_TABLE_ENGINE,
    MATERIALIZED_VIEW;

    @Override // java.lang.Enum
    public String toString() {
        if (this == EXTERNAL_TABLE_ENGINE) {
            return "EXTERNAL";
        }
        if (this == SYSTEM_TABLE) {
            return "SYSTEM TABLE";
        }
        if (this == TABLE_LINK) {
            return "TABLE LINK";
        }
        if (this == MATERIALIZED_VIEW) {
            return "MATERIALIZED VIEW";
        }
        return super.toString();
    }
}
