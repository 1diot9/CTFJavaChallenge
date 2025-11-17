package org.jooq.conf;

import jakarta.xml.bind.annotation.XmlEnum;
import jakarta.xml.bind.annotation.XmlType;

@XmlType(name = "RenderImplicitWindowRange")
@XmlEnum
/* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/conf/RenderImplicitWindowRange.class */
public enum RenderImplicitWindowRange {
    OFF,
    ROWS_UNBOUNDED_PRECEDING,
    ROWS_ALL,
    RANGE_UNBOUNDED_PRECEDING,
    RANGE_ALL;

    public String value() {
        return name();
    }

    public static RenderImplicitWindowRange fromValue(String v) {
        return valueOf(v);
    }
}
