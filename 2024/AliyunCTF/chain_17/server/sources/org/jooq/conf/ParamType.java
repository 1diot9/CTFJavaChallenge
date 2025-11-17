package org.jooq.conf;

import jakarta.xml.bind.annotation.XmlEnum;
import jakarta.xml.bind.annotation.XmlType;

@XmlType(name = "ParamType")
@XmlEnum
/* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/conf/ParamType.class */
public enum ParamType {
    INDEXED,
    FORCE_INDEXED,
    NAMED,
    NAMED_OR_INLINED,
    INLINED;

    public String value() {
        return name();
    }

    public static ParamType fromValue(String v) {
        return valueOf(v);
    }
}
