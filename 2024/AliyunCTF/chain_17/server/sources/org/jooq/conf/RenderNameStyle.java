package org.jooq.conf;

import jakarta.xml.bind.annotation.XmlEnum;
import jakarta.xml.bind.annotation.XmlType;

@XmlType(name = "RenderNameStyle")
@XmlEnum
@Deprecated
/* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/conf/RenderNameStyle.class */
public enum RenderNameStyle {
    QUOTED,
    AS_IS,
    LOWER,
    UPPER;

    public String value() {
        return name();
    }

    public static RenderNameStyle fromValue(String v) {
        return valueOf(v);
    }
}
