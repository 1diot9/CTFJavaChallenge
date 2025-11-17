package cn.hutool.poi.excel.cell.setters;

import cn.hutool.poi.excel.cell.CellSetter;
import org.apache.poi.ss.usermodel.Cell;

/* loaded from: agent.jar:BOOT-INF/lib/hutool-all-5.8.16.jar:cn/hutool/poi/excel/cell/setters/CharSequenceCellSetter.class */
public class CharSequenceCellSetter implements CellSetter {
    private final CharSequence value;

    /* JADX INFO: Access modifiers changed from: package-private */
    public CharSequenceCellSetter(CharSequence value) {
        this.value = value;
    }

    @Override // cn.hutool.poi.excel.cell.CellSetter
    public void setValue(Cell cell) {
        cell.setCellValue(this.value.toString());
    }
}
