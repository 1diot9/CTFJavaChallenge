package org.jooq.conf;

import jakarta.xml.bind.annotation.XmlEnum;
import jakarta.xml.bind.annotation.XmlType;

@XmlType(name = "ParseWithMetaLookups")
@XmlEnum
/* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/conf/ParseWithMetaLookups.class */
public enum ParseWithMetaLookups {
    OFF,
    IGNORE_ON_FAILURE,
    THROW_ON_FAILURE;

    public String value() {
        return name();
    }

    public static ParseWithMetaLookups fromValue(String v) {
        return valueOf(v);
    }
}
