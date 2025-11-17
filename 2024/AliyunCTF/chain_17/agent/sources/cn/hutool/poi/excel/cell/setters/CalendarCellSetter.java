package cn.hutool.poi.excel.cell.setters;

import cn.hutool.poi.excel.cell.CellSetter;
import java.util.Calendar;
import org.apache.poi.ss.usermodel.Cell;

/* loaded from: agent.jar:BOOT-INF/lib/hutool-all-5.8.16.jar:cn/hutool/poi/excel/cell/setters/CalendarCellSetter.class */
public class CalendarCellSetter implements CellSetter {
    private final Calendar value;

    /* JADX INFO: Access modifiers changed from: package-private */
    public CalendarCellSetter(Calendar value) {
        this.value = value;
    }

    @Override // cn.hutool.poi.excel.cell.CellSetter
    public void setValue(Cell cell) {
        cell.setCellValue(this.value);
    }
}
