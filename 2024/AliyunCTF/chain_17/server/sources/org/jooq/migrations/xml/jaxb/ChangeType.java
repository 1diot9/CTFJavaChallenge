package org.jooq.migrations.xml.jaxb;

import jakarta.xml.bind.annotation.XmlEnum;
import jakarta.xml.bind.annotation.XmlType;

@XmlType(name = "ChangeType")
@XmlEnum
/* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/migrations/xml/jaxb/ChangeType.class */
public enum ChangeType {
    ADD,
    MODIFY,
    DELETE,
    RENAME,
    COPY;

    public String value() {
        return name();
    }

    public static ChangeType fromValue(String v) {
        return valueOf(v);
    }
}
