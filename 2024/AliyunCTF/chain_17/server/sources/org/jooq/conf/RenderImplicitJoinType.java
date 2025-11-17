package org.jooq.conf;

import jakarta.xml.bind.annotation.XmlEnum;
import jakarta.xml.bind.annotation.XmlType;

@XmlType(name = "RenderImplicitJoinType")
@XmlEnum
/* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/conf/RenderImplicitJoinType.class */
public enum RenderImplicitJoinType {
    DEFAULT,
    INNER_JOIN,
    LEFT_JOIN,
    SCALAR_SUBQUERY,
    THROW;

    public String value() {
        return name();
    }

    public static RenderImplicitJoinType fromValue(String v) {
        return valueOf(v);
    }
}
