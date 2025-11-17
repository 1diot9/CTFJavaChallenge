package org.jooq.util.xml.jaxb;

import jakarta.xml.bind.annotation.XmlEnum;
import jakarta.xml.bind.annotation.XmlType;

@XmlType(name = "RoutineType")
@XmlEnum
/* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/util/xml/jaxb/RoutineType.class */
public enum RoutineType {
    FUNCTION,
    PROCEDURE;

    public String value() {
        return name();
    }

    public static RoutineType fromValue(String v) {
        return valueOf(v);
    }
}
