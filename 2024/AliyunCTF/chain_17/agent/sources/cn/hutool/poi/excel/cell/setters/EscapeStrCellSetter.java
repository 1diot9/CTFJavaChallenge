package cn.hutool.poi.excel.cell.setters;

import cn.hutool.core.util.ReUtil;
import cn.hutool.core.util.StrUtil;
import java.util.regex.Pattern;

/* loaded from: agent.jar:BOOT-INF/lib/hutool-all-5.8.16.jar:cn/hutool/poi/excel/cell/setters/EscapeStrCellSetter.class */
public class EscapeStrCellSetter extends CharSequenceCellSetter {
    private static final Pattern utfPtrn = Pattern.compile("_x[0-9A-Fa-f]{4}_");

    public EscapeStrCellSetter(CharSequence value) {
        super(escape(StrUtil.str(value)));
    }

    private static String escape(String value) {
        if (value == null || false == value.contains("_x")) {
            return value;
        }
        return ReUtil.replaceAll(value, utfPtrn, "_x005F$0");
    }
}
