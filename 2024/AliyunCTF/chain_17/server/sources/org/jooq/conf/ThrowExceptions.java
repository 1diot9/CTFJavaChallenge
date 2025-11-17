package org.jooq.conf;

import jakarta.xml.bind.annotation.XmlEnum;
import jakarta.xml.bind.annotation.XmlType;

@XmlType(name = "ThrowExceptions")
@XmlEnum
/* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/conf/ThrowExceptions.class */
public enum ThrowExceptions {
    THROW_ALL,
    THROW_FIRST,
    THROW_NONE;

    public String value() {
        return name();
    }

    public static ThrowExceptions fromValue(String v) {
        return valueOf(v);
    }
}
