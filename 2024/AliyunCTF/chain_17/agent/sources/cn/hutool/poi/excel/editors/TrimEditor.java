package cn.hutool.poi.excel.editors;

import cn.hutool.core.util.StrUtil;
import cn.hutool.poi.excel.cell.CellEditor;
import org.apache.poi.ss.usermodel.Cell;

/* loaded from: agent.jar:BOOT-INF/lib/hutool-all-5.8.16.jar:cn/hutool/poi/excel/editors/TrimEditor.class */
public class TrimEditor implements CellEditor {
    @Override // cn.hutool.poi.excel.cell.CellEditor
    public Object edit(Cell cell, Object value) {
        if (value instanceof String) {
            return StrUtil.trim((String) value);
        }
        return value;
    }
}
