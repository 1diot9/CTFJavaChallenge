package org.jooq.conf;

import jakarta.xml.bind.annotation.XmlEnum;
import jakarta.xml.bind.annotation.XmlType;

@XmlType(name = "RenderTable")
@XmlEnum
/* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/conf/RenderTable.class */
public enum RenderTable {
    ALWAYS,
    WHEN_MULTIPLE_TABLES,
    WHEN_AMBIGUOUS_COLUMNS,
    NEVER;

    public String value() {
        return name();
    }

    public static RenderTable fromValue(String v) {
        return valueOf(v);
    }
}
