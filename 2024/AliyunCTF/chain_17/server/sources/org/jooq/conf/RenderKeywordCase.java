package org.jooq.conf;

import jakarta.xml.bind.annotation.XmlEnum;
import jakarta.xml.bind.annotation.XmlType;

@XmlType(name = "RenderKeywordCase")
@XmlEnum
/* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/conf/RenderKeywordCase.class */
public enum RenderKeywordCase {
    AS_IS,
    LOWER,
    UPPER,
    PASCAL;

    public String value() {
        return name();
    }

    public static RenderKeywordCase fromValue(String v) {
        return valueOf(v);
    }
}
