package org.jooq.conf;

import jakarta.xml.bind.annotation.XmlEnum;
import jakarta.xml.bind.annotation.XmlType;

@XmlType(name = "RenderOptionalKeyword")
@XmlEnum
/* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/conf/RenderOptionalKeyword.class */
public enum RenderOptionalKeyword {
    OFF,
    ON,
    DEFAULT;

    public String value() {
        return name();
    }

    public static RenderOptionalKeyword fromValue(String v) {
        return valueOf(v);
    }
}
