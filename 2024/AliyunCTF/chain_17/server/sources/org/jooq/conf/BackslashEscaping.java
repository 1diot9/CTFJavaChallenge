package org.jooq.conf;

import jakarta.xml.bind.annotation.XmlEnum;
import jakarta.xml.bind.annotation.XmlType;

@XmlType(name = "BackslashEscaping")
@XmlEnum
/* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/conf/BackslashEscaping.class */
public enum BackslashEscaping {
    DEFAULT,
    ON,
    OFF;

    public String value() {
        return name();
    }

    public static BackslashEscaping fromValue(String v) {
        return valueOf(v);
    }
}
