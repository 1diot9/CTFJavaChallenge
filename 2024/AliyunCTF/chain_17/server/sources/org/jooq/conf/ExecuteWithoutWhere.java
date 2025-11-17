package org.jooq.conf;

import jakarta.xml.bind.annotation.XmlEnum;
import jakarta.xml.bind.annotation.XmlType;

@XmlType(name = "ExecuteWithoutWhere")
@XmlEnum
/* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/conf/ExecuteWithoutWhere.class */
public enum ExecuteWithoutWhere {
    IGNORE,
    LOG_DEBUG,
    LOG_INFO,
    LOG_WARN,
    THROW;

    public String value() {
        return name();
    }

    public static ExecuteWithoutWhere fromValue(String v) {
        return valueOf(v);
    }
}
