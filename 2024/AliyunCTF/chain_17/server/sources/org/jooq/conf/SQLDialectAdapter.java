package org.jooq.conf;

import jakarta.xml.bind.annotation.adapters.XmlAdapter;
import org.jooq.SQLDialect;

/* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/conf/SQLDialectAdapter.class */
public class SQLDialectAdapter extends XmlAdapter<String, SQLDialect> {
    public SQLDialect unmarshal(String v) throws Exception {
        return SQLDialect.valueOf(v);
    }

    public String marshal(SQLDialect v) throws Exception {
        return v.name();
    }
}
