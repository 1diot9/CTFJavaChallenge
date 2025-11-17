package org.jooq.util.xml.jaxb;

import jakarta.xml.bind.annotation.XmlEnum;
import jakarta.xml.bind.annotation.XmlType;

@XmlType(name = "TriggerActionOrientation")
@XmlEnum
/* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/util/xml/jaxb/TriggerActionOrientation.class */
public enum TriggerActionOrientation {
    ROW,
    STATEMENT;

    public String value() {
        return name();
    }

    public static TriggerActionOrientation fromValue(String v) {
        return valueOf(v);
    }
}
