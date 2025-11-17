package org.jooq.conf;

import jakarta.xml.bind.annotation.XmlEnum;
import jakarta.xml.bind.annotation.XmlType;

@XmlType(name = "RenderQuotedNames")
@XmlEnum
/* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/conf/RenderQuotedNames.class */
public enum RenderQuotedNames {
    ALWAYS,
    EXPLICIT_DEFAULT_QUOTED,
    EXPLICIT_DEFAULT_UNQUOTED,
    NEVER;

    public String value() {
        return name();
    }

    public static RenderQuotedNames fromValue(String v) {
        return valueOf(v);
    }
}
