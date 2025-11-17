package cn.hutool.poi.excel.cell.setters;

import cn.hutool.poi.excel.cell.CellSetter;
import java.util.Date;
import org.apache.poi.ss.usermodel.Cell;

/* loaded from: agent.jar:BOOT-INF/lib/hutool-all-5.8.16.jar:cn/hutool/poi/excel/cell/setters/DateCellSetter.class */
public class DateCellSetter implements CellSetter {
    private final Date value;

    /* JADX INFO: Access modifiers changed from: package-private */
    public DateCellSetter(Date value) {
        this.value = value;
    }

    @Override // cn.hutool.poi.excel.cell.CellSetter
    public void setValue(Cell cell) {
        cell.setCellValue(this.value);
    }
}
