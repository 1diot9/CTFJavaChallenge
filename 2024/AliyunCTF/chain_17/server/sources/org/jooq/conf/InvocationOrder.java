package org.jooq.conf;

import jakarta.xml.bind.annotation.XmlEnum;
import jakarta.xml.bind.annotation.XmlType;

@XmlType(name = "InvocationOrder")
@XmlEnum
/* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/conf/InvocationOrder.class */
public enum InvocationOrder {
    DEFAULT,
    REVERSE;

    public String value() {
        return name();
    }

    public static InvocationOrder fromValue(String v) {
        return valueOf(v);
    }
}
