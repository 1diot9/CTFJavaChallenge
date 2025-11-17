package org.jooq.util.xml.jaxb;

import jakarta.xml.bind.annotation.XmlEnum;
import jakarta.xml.bind.annotation.XmlType;

@XmlType(name = "TableConstraintType")
@XmlEnum
/* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/util/xml/jaxb/TableConstraintType.class */
public enum TableConstraintType {
    PRIMARY_KEY("PRIMARY KEY"),
    UNIQUE("UNIQUE"),
    CHECK("CHECK"),
    FOREIGN_KEY("FOREIGN KEY");

    private final String value;

    TableConstraintType(String v) {
        this.value = v;
    }

    public String value() {
        return this.value;
    }

    public static TableConstraintType fromValue(String v) {
        for (TableConstraintType c : values()) {
            if (c.value.equals(v)) {
                return c;
            }
        }
        throw new IllegalArgumentException(v);
    }

    @Override // java.lang.Enum
    public String toString() {
        switch (ordinal()) {
            case 0:
                return "PRIMARY KEY";
            case 3:
                return "FOREIGN KEY";
            default:
                return name();
        }
    }
}
