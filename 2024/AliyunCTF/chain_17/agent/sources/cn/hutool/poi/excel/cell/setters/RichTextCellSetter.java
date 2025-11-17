package cn.hutool.poi.excel.cell.setters;

import cn.hutool.poi.excel.cell.CellSetter;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.RichTextString;

/* loaded from: agent.jar:BOOT-INF/lib/hutool-all-5.8.16.jar:cn/hutool/poi/excel/cell/setters/RichTextCellSetter.class */
public class RichTextCellSetter implements CellSetter {
    private final RichTextString value;

    /* JADX INFO: Access modifiers changed from: package-private */
    public RichTextCellSetter(RichTextString value) {
        this.value = value;
    }

    @Override // cn.hutool.poi.excel.cell.CellSetter
    public void setValue(Cell cell) {
        cell.setCellValue(this.value);
    }
}
