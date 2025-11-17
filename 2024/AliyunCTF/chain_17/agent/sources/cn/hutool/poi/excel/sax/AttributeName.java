package cn.hutool.poi.excel.sax;

import org.xml.sax.Attributes;

/* loaded from: agent.jar:BOOT-INF/lib/hutool-all-5.8.16.jar:cn/hutool/poi/excel/sax/AttributeName.class */
public enum AttributeName {
    r,
    s,
    t;

    public boolean match(String attributeName) {
        return name().equals(attributeName);
    }

    public String getValue(Attributes attributes) {
        return attributes.getValue(name());
    }
}
