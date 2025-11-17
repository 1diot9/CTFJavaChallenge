package org.jooq.conf;

import jakarta.xml.bind.annotation.XmlEnum;
import jakarta.xml.bind.annotation.XmlType;

@XmlType(name = "NestedCollectionEmulation")
@XmlEnum
/* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/conf/NestedCollectionEmulation.class */
public enum NestedCollectionEmulation {
    NATIVE,
    DEFAULT,
    XML,
    JSON,
    JSONB;

    public String value() {
        return name();
    }

    public static NestedCollectionEmulation fromValue(String v) {
        return valueOf(v);
    }
}
