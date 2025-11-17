package org.jooq.conf;

import jakarta.xml.bind.annotation.XmlEnum;
import jakarta.xml.bind.annotation.XmlType;

@XmlType(name = "RenderNameCase")
@XmlEnum
/* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/conf/RenderNameCase.class */
public enum RenderNameCase {
    AS_IS,
    LOWER,
    LOWER_IF_UNQUOTED,
    UPPER,
    UPPER_IF_UNQUOTED;

    public String value() {
        return name();
    }

    public static RenderNameCase fromValue(String v) {
        return valueOf(v);
    }
}
