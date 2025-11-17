package org.jooq.conf;

import jakarta.xml.bind.annotation.XmlEnum;
import jakarta.xml.bind.annotation.XmlType;
import org.springframework.http.HttpHeaders;

@XmlType(name = HttpHeaders.WARNING)
@XmlEnum
/* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/conf/Warning.class */
public enum Warning {
    IGNORE,
    LOG_DEBUG,
    LOG_INFO,
    LOG_WARN,
    THROW;

    public String value() {
        return name();
    }

    public static Warning fromValue(String v) {
        return valueOf(v);
    }
}
