package cn.hutool.poi.excel.cell.setters;

import cn.hutool.core.util.NumberUtil;
import cn.hutool.poi.excel.cell.CellSetter;
import org.apache.poi.ss.usermodel.Cell;

/* loaded from: agent.jar:BOOT-INF/lib/hutool-all-5.8.16.jar:cn/hutool/poi/excel/cell/setters/NumberCellSetter.class */
public class NumberCellSetter implements CellSetter {
    private final Number value;

    /* JADX INFO: Access modifiers changed from: package-private */
    public NumberCellSetter(Number value) {
        this.value = value;
    }

    @Override // cn.hutool.poi.excel.cell.CellSetter
    public void setValue(Cell cell) {
        cell.setCellValue(NumberUtil.toDouble(this.value));
    }
}
