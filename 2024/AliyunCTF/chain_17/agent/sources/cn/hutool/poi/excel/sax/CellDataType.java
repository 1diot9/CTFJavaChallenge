package cn.hutool.poi.excel.sax;

/* loaded from: agent.jar:BOOT-INF/lib/hutool-all-5.8.16.jar:cn/hutool/poi/excel/sax/CellDataType.class */
public enum CellDataType {
    BOOL("b"),
    ERROR("e"),
    FORMULA("formula"),
    INLINESTR("inlineStr"),
    SSTINDEX("s"),
    NUMBER(""),
    DATE("m/d/yy"),
    NULL("");

    private final String name;

    CellDataType(String name) {
        this.name = name;
    }

    public String getName() {
        return this.name;
    }

    public static CellDataType of(String name) {
        if (null == name) {
            return NUMBER;
        }
        if (BOOL.name.equals(name)) {
            return BOOL;
        }
        if (ERROR.name.equals(name)) {
            return ERROR;
        }
        if (INLINESTR.name.equals(name)) {
            return INLINESTR;
        }
        if (SSTINDEX.name.equals(name)) {
            return SSTINDEX;
        }
        if (FORMULA.name.equals(name)) {
            return FORMULA;
        }
        return NULL;
    }
}
