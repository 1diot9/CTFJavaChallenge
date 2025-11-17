package org.jooq.conf;

import jakarta.xml.bind.annotation.adapters.XmlAdapter;
import java.util.Locale;

/* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/conf/LocaleAdapter.class */
public class LocaleAdapter extends XmlAdapter<String, Locale> {
    public Locale unmarshal(String v) throws Exception {
        if (v == null) {
            return null;
        }
        Locale result = Locale.forLanguageTag(v);
        return result;
    }

    public String marshal(Locale v) throws Exception {
        if (v == null) {
            return null;
        }
        String result = v.toLanguageTag();
        return result;
    }
}
