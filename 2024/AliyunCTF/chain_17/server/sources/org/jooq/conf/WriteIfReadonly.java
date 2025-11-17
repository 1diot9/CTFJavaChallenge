package org.jooq.conf;

import jakarta.xml.bind.annotation.XmlEnum;
import jakarta.xml.bind.annotation.XmlType;

@XmlType(name = "WriteIfReadonly")
@XmlEnum
/* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/conf/WriteIfReadonly.class */
public enum WriteIfReadonly {
    WRITE,
    IGNORE,
    THROW;

    public String value() {
        return name();
    }

    public static WriteIfReadonly fromValue(String v) {
        return valueOf(v);
    }
}
