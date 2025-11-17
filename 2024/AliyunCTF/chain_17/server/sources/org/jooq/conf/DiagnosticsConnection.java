package org.jooq.conf;

import jakarta.xml.bind.annotation.XmlEnum;
import jakarta.xml.bind.annotation.XmlType;

@XmlType(name = "DiagnosticsConnection")
@XmlEnum
/* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/conf/DiagnosticsConnection.class */
public enum DiagnosticsConnection {
    DEFAULT,
    ON,
    OFF;

    public String value() {
        return name();
    }

    public static DiagnosticsConnection fromValue(String v) {
        return valueOf(v);
    }
}
