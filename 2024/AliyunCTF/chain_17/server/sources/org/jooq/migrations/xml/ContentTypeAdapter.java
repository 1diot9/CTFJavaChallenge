package org.jooq.migrations.xml;

import jakarta.xml.bind.annotation.adapters.XmlAdapter;
import org.jooq.ContentType;

/* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/migrations/xml/ContentTypeAdapter.class */
public class ContentTypeAdapter extends XmlAdapter<String, ContentType> {
    public ContentType unmarshal(String v) throws Exception {
        return ContentType.valueOf(v);
    }

    public String marshal(ContentType v) throws Exception {
        return v.name();
    }
}
