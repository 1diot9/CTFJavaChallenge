package org.jooq.util.xml.jaxb;

import jakarta.xml.bind.annotation.XmlEnum;
import jakarta.xml.bind.annotation.XmlType;

@XmlType(name = "ObjectType")
@XmlEnum
/* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/util/xml/jaxb/ObjectType.class */
public enum ObjectType {
    DOMAIN("DOMAIN"),
    ROUTINE("ROUTINE"),
    TABLE("TABLE"),
    USER_DEFINED_TYPE("USER-DEFINED TYPE");

    private final String value;

    ObjectType(String v) {
        this.value = v;
    }

    public String value() {
        return this.value;
    }

    public static ObjectType fromValue(String v) {
        for (ObjectType c : values()) {
            if (c.value.equals(v)) {
                return c;
            }
        }
        throw new IllegalArgumentException(v);
    }

    @Override // java.lang.Enum
    public String toString() {
        switch (ordinal()) {
            case 3:
                return "USER-DEFINED TYPE";
            default:
                return name();
        }
    }
}
