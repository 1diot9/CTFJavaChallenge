package org.jooq.util.xml.jaxb;

import jakarta.xml.bind.annotation.XmlEnum;
import jakarta.xml.bind.annotation.XmlType;

@XmlType(name = "TriggerActionTiming")
@XmlEnum
/* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/util/xml/jaxb/TriggerActionTiming.class */
public enum TriggerActionTiming {
    BEFORE,
    AFTER,
    INSTEAD_OF;

    public String value() {
        return name();
    }

    public static TriggerActionTiming fromValue(String v) {
        return valueOf(v);
    }
}
