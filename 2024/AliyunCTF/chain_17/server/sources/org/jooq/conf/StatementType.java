package org.jooq.conf;

import jakarta.xml.bind.annotation.XmlEnum;
import jakarta.xml.bind.annotation.XmlType;

@XmlType(name = "StatementType")
@XmlEnum
/* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/conf/StatementType.class */
public enum StatementType {
    STATIC_STATEMENT,
    PREPARED_STATEMENT;

    public String value() {
        return name();
    }

    public static StatementType fromValue(String v) {
        return valueOf(v);
    }
}
