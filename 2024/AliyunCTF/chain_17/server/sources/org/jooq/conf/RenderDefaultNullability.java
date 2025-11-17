package org.jooq.conf;

import jakarta.xml.bind.annotation.XmlEnum;
import jakarta.xml.bind.annotation.XmlType;

@XmlType(name = "RenderDefaultNullability")
@XmlEnum
/* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/conf/RenderDefaultNullability.class */
public enum RenderDefaultNullability {
    IMPLICIT_DEFAULT,
    IMPLICIT_NULL,
    EXPLICIT_NULL;

    public String value() {
        return name();
    }

    public static RenderDefaultNullability fromValue(String v) {
        return valueOf(v);
    }
}
