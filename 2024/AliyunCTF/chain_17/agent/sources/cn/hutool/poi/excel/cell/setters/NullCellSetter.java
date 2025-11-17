package cn.hutool.poi.excel.cell.setters;

import cn.hutool.poi.excel.cell.CellSetter;
import org.apache.poi.ss.usermodel.Cell;

/* loaded from: agent.jar:BOOT-INF/lib/hutool-all-5.8.16.jar:cn/hutool/poi/excel/cell/setters/NullCellSetter.class */
public class NullCellSetter implements CellSetter {
    public static final NullCellSetter INSTANCE = new NullCellSetter();

    @Override // cn.hutool.poi.excel.cell.CellSetter
    public void setValue(Cell cell) {
        cell.setCellValue("");
    }
}
