package cn.hutool.poi.excel.cell.values;

import cn.hutool.poi.excel.cell.CellValue;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.FormulaError;

/* loaded from: agent.jar:BOOT-INF/lib/hutool-all-5.8.16.jar:cn/hutool/poi/excel/cell/values/ErrorCellValue.class */
public class ErrorCellValue implements CellValue<String> {
    private final Cell cell;

    public ErrorCellValue(Cell cell) {
        this.cell = cell;
    }

    /* JADX WARN: Can't rename method to resolve collision */
    @Override // cn.hutool.poi.excel.cell.CellValue
    public String getValue() {
        FormulaError error = FormulaError.forInt(this.cell.getErrorCellValue());
        return null == error ? "" : error.getString();
    }
}
