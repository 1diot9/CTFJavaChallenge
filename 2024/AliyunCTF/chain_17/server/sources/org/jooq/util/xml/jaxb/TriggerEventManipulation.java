package org.jooq.util.xml.jaxb;

import jakarta.xml.bind.annotation.XmlEnum;
import jakarta.xml.bind.annotation.XmlType;

@XmlType(name = "TriggerEventManipulation")
@XmlEnum
/* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/util/xml/jaxb/TriggerEventManipulation.class */
public enum TriggerEventManipulation {
    INSERT,
    UPDATE,
    DELETE;

    public String value() {
        return name();
    }

    public static TriggerEventManipulation fromValue(String v) {
        return valueOf(v);
    }
}
