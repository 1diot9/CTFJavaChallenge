package org.jooq.util.jaxb.tools;

import jakarta.xml.bind.annotation.adapters.XmlAdapter;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.jooq.tools.StringUtils;

/* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/util/jaxb/tools/StringAdapter.class */
public class StringAdapter extends XmlAdapter<String, String> {
    private static final Pattern PROPERTY_PATTERN = Pattern.compile("\\$\\{(.*?)\\}");

    public final String unmarshal(String v) throws Exception {
        if (v == null) {
            return null;
        }
        String result = v.trim();
        Matcher matcher = PROPERTY_PATTERN.matcher(result);
        while (matcher.find()) {
            String group0 = matcher.group(0);
            String group1 = matcher.group(1);
            result = StringUtils.replace(result, group0, System.getProperty(group1, group0));
        }
        return result;
    }

    public final String marshal(String v) throws Exception {
        if (v == null) {
            return null;
        }
        return v.trim();
    }
}
