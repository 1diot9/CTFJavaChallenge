package org.jooq.conf;

import jakarta.xml.bind.annotation.adapters.XmlAdapter;
import java.util.regex.Pattern;

/* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/conf/RegexAdapter.class */
public class RegexAdapter extends XmlAdapter<String, Pattern> {
    public Pattern unmarshal(String v) throws Exception {
        if (v == null) {
            return null;
        }
        return Pattern.compile(v);
    }

    public String marshal(Pattern v) throws Exception {
        if (v == null) {
            return null;
        }
        return v.pattern();
    }
}
