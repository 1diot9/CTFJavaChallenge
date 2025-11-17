package cn.hutool.poi.excel.sax;

/* loaded from: agent.jar:BOOT-INF/lib/hutool-all-5.8.16.jar:cn/hutool/poi/excel/sax/ElementName.class */
public enum ElementName {
    row,
    c,
    v,
    f;

    public boolean match(String elementName) {
        return name().equals(elementName);
    }

    public static ElementName of(String elementName) {
        try {
            return valueOf(elementName);
        } catch (Exception e) {
            return null;
        }
    }
}
