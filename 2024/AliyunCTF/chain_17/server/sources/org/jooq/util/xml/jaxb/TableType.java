package org.jooq.util.xml.jaxb;

import jakarta.xml.bind.annotation.XmlEnum;
import jakarta.xml.bind.annotation.XmlType;

@XmlType(name = "TableType")
@XmlEnum
/* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/util/xml/jaxb/TableType.class */
public enum TableType {
    BASE_TABLE("BASE TABLE"),
    VIEW("VIEW"),
    MATERIALIZED_VIEW("MATERIALIZED VIEW"),
    GLOBAL_TEMPORARY("GLOBAL TEMPORARY");

    private final String value;

    TableType(String v) {
        this.value = v;
    }

    public String value() {
        return this.value;
    }

    public static TableType fromValue(String v) {
        for (TableType c : values()) {
            if (c.value.equals(v)) {
                return c;
            }
        }
        throw new IllegalArgumentException(v);
    }

    @Override // java.lang.Enum
    public String toString() {
        switch (this) {
            case BASE_TABLE:
                return "BASE TABLE";
            case VIEW:
            default:
                return name();
            case MATERIALIZED_VIEW:
                return "MATERIALIZED VIEW";
            case GLOBAL_TEMPORARY:
                return "GLOBAL TEMPORARY";
        }
    }
}
