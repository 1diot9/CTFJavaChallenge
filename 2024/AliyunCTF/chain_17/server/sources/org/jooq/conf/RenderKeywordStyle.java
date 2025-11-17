package org.jooq.conf;

import jakarta.xml.bind.annotation.XmlEnum;
import jakarta.xml.bind.annotation.XmlType;

@XmlType(name = "RenderKeywordStyle")
@XmlEnum
@Deprecated
/* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/conf/RenderKeywordStyle.class */
public enum RenderKeywordStyle {
    AS_IS,
    LOWER,
    UPPER,
    PASCAL;

    public String value() {
        return name();
    }

    public static RenderKeywordStyle fromValue(String v) {
        return valueOf(v);
    }
}
